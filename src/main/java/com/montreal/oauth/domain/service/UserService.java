package com.montreal.oauth.domain.service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.montreal.core.domain.dto.response.MessageResponse;
import com.montreal.core.domain.enumerations.MessageTypeEnum;
import com.montreal.core.domain.exception.ClientServiceException;
import com.montreal.core.domain.exception.ConflictUserException;
import com.montreal.core.domain.exception.CryptoException;
import com.montreal.core.domain.exception.EmailException;
import com.montreal.core.domain.exception.InternalErrorException;
import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.core.domain.exception.UserNotFoundException;
import com.montreal.core.domain.service.EmailService;
import com.montreal.core.exception_handler.ProblemType;
import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.repository.CompanyRepository;
import com.montreal.oauth.domain.component.Base64MultipartFile;
import com.montreal.oauth.domain.component.CryptoComponent;
import com.montreal.oauth.domain.dto.AuthRequestDTO;
import com.montreal.oauth.domain.dto.JwtResponseDTO;
import com.montreal.oauth.domain.dto.request.UserRequest;
import com.montreal.oauth.domain.dto.response.UserResponse;
import com.montreal.oauth.domain.entity.RefreshToken;
import com.montreal.oauth.domain.entity.Role;
import com.montreal.oauth.domain.entity.UserInfo;
import com.montreal.oauth.domain.enumerations.RoleEnum;
import com.montreal.oauth.domain.exception.AuthenticateException;
import com.montreal.oauth.domain.exception.DuplicateUserException;
import com.montreal.oauth.domain.exception.ResourceNotFoundException;
import com.montreal.oauth.domain.mapper.MessageMapper;
import com.montreal.oauth.domain.repository.IRoleRepository;
import com.montreal.oauth.domain.repository.IUserRepository;
import com.montreal.oauth.domain.repository.impl.RefreshTokenRepository;
import com.montreal.oauth.exception.BusinessException;
import com.montreal.oauth.mapper.IUserMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Value("${montreal.oauth.encryptSecretKey}")
    String encryptSecretKey;

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;
    private final CryptoComponent aes;
    private final EmailService emailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserImageService userImageService;
    private final CompanyRepository companyRepository;

    @Transactional
    public UserResponse saveUser(UserRequest userRequest) {

        try {

            validateUserCreated(userRequest);

            findRoles(userRequest);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = userRequest.getPassword();
            String encodedPassword = encoder.encode(rawPassword);

            var user = IUserMapper.INSTANCE.toEntity(userRequest);
            user.setCreatedByAdmin(true);
            user.setPasswordChangedByUser(false);
            user.setResetAt(new Timestamp(System.currentTimeMillis()));
            user.setPassword(encodedPassword);
            
            var savedUser = userRepository.save(user);

            userRepository.refresh(savedUser);

            sendEmailWithRollback(savedUser);

            return IUserMapper.INSTANCE.toResponse(savedUser);

        } catch (DataIntegrityViolationException e) {
            throw new ConflictUserException(String.format("Usuário com o username '%s' já existe", userRequest.getUsername()));
        } catch (DuplicateUserException | NegocioException | EmailException | ClientServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao salvar usuário", e);
            throw new InternalErrorException(String.format("Falha ao salvar usuário, erro: %s", e.getMessage()));
        }
    }

    private void sendEmailWithRollback(UserInfo savedUser) {
        try {

            emailService.sendEmailRegistrationConfirmation(savedUser);

        } catch (EmailException e) {
            log.error("Erro ao enviar e-mail de confirmação. Removendo usuário criado.", e);
            userRepository.deleteById(savedUser.getId());
            throw new EmailException("Erro ao enviar e-mail. Usuário removido.", e);
        }
    }


    public UserResponse getUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = (UserDetails) authentication.getPrincipal();
        String usernameFromAccessToken = userDetail.getUsername();
        UserInfo user = userRepository.findByUsername(usernameFromAccessToken);

        return modelMapper.map(user, UserResponse.class);
    }

    public MessageResponse passwordRecoveryValidation(String key, List<String> objects) {
        return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_BAD_REQUEST, "O email está mal formatado ou não foi informado!", this.addListObj(key, objects)).build();
    }

    public MessageResponse messageList(String key, List<String> objects, MessageTypeEnum msgType, String details) {
        return MessageMapper.createMessageBuilder(msgType, details, this.addListObj(key, objects)).build();
    }

    public UserResponse findByEmail(String email) {
        return modelMapper.map(userRepository.findByEmail(email), UserResponse.class);
    }

    public Page<UserResponse> getAllUsersWithFilters(Pageable pageable, HttpServletRequest request) {
        List<Specification<UserInfo>> specs = new ArrayList<>();

        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0 && !key.equals("page") && !key.equals("size") && !key.equals("sort")) {
                String value = values[0];

                // Ajuste para "enabled" e tipo booleano
                if (key.equals("enabled") || key.equals("isEnabled")) {
                    Boolean enabledValue = Boolean.parseBoolean(value);
                    specs.add((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("isEnabled"), enabledValue));
                } else {
                    // Condição padrão para outros campos de texto
                    specs.add((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get(key)), "%" + value.toLowerCase() + "%"));
                }
            }
        });

        // Combine todas as especificações usando OR
        Specification<UserInfo> combinedSpec = specs.stream()
                .reduce(Specification::or)
                .orElse(null);

        Page<UserInfo> users = userRepository.findAll(combinedSpec, pageable);
        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    public MessageResponse passwordRecovery(String email) {

        try {

            Optional<UserInfo> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));

            if (optionalUser.isEmpty()) {
                return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_NOT_FOUND, "O email não foi encontrado!", this.addSingleObj("erros", "Não foi possível identificar o usuário com o email informado!")).build();
            }

            UserInfo user = optionalUser.get();

            String linkPlain = OffsetDateTime.now().toEpochSecond() +
                    "," +
                    user.getId() +
                    "," +
                    user.getFullName().replace(" ", "-") +
                    "," +
                    user.getEmail() +
                    "," +
                    OffsetDateTime.now().toEpochSecond();

            String link = aes.encryptFromString(linkPlain, encryptSecretKey);
            String linkParse = link.replace("/", "-W-");
            String linkEmail = "http://localhost:4202/#/home?link=" + linkParse;
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            user.setLink(link);
            user.setReset(true);
            user.setResetAt(timestamp);
            userRepository.save(user);

            emailService.sendEmailFromTemplate(user.getFullName(), linkEmail, user.getEmail());

            return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_OK, "Link de recuperação gerado com sucesso!", this.addSingleObj("link", linkParse)).build();

        } catch (CryptoException e) {
            throw e;

        } catch (Exception e) {
            throw new InternalErrorException("Falha ao gerar link de recuperação de senha!", e);
        }

    }

    public MessageResponse login(String username, String password) {

        UserInfo user = userRepository.findByUsername(username);

        if ((Optional.ofNullable(user)).isEmpty()) {
            return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_UNAUTHORIZED, "Acesso Negado!", this.addSingleObj("erros", "Não foi possível acessar com os dados informados!")).build();
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if(authentication.isAuthenticated()) {

            String accessToken;
            try {
                accessToken = aes.encryptFromString(jwtService.GenerateToken(username), encryptSecretKey);
            } catch (Exception e) {
                throw new NegocioException(ProblemType.ERRO_NEGOCIO, "Erro ao gerar o token de acesso", e);
            }

            String token = refreshTokenService.getTokenByUserId(user.getId());
            if(token.isEmpty()) {
                RefreshToken refreshToken =  refreshTokenService.createRefreshToken(username);
                token = refreshToken.getToken();
            }

            var jwtResponseDTO = JwtResponseDTO.builder().accessToken(accessToken).token(token).build();

            Map<String, List<String>> list = new HashMap<>();

            list.put("token", Collections.singletonList(jwtResponseDTO.getToken()));
            list.put("accessToken", Collections.singletonList(jwtResponseDTO.getAccessToken()));


            return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_OK, "Acesso realizado com sucesso!", list).build();

        } else {
            return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_UNAUTHORIZED, "Acesso Negado!", this.addSingleObj("erros", "Não foi possível acessar com os dados informados!")).build();

        }

    }

    public MessageResponse passwordReset(String password, String email, String link) {

        UserInfo user = userRepository.findByEmail(email);

        if ((Optional.ofNullable(user)).isEmpty()) {
            return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_NOT_FOUND, "O email não foi encontrado", this.addSingleObj("erros", "Não foi possível acessar com os dados informados!")).build();
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        user.setPassword(encodedPassword);

        user.setReset(false);
        userRepository.save(user);

        return MessageMapper.createMessageBuilder(MessageTypeEnum.MSG_OK, "Link de recuperação gerado com sucesso!", this.addSingleObj("OK", "!")).build();

    }

    public void update(UserInfo userInfo) {
        userRepository.save(userInfo);
    }


    public Map<String, List<String>> addListObj(String key, List<String> objects) {
        return new HashMap<>(Map.of(key, new ArrayList<>(objects)));
    }

    public Map<String, List<String>> addSingleObj(String key, String value) {
        return new HashMap<>(Map.of(key, Collections.singletonList(value)));
    }

    private void findRoles(UserRequest userRequest) {
        userRequest.getRoles().forEach(role ->
            roleRepository.findByName(role.getName()).ifPresentOrElse(
                    foundRole -> role.setId(foundRole.getId()),
                    () -> {
                        throw new NegocioException(String.format("Não foi possível encontrar o papel com o nome: %s", role.getName()));
                    }
            )
        );
    }

    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        try {
        	
            UserInfo existingUser = userRepository.findFirstById(userId);
            if (existingUser == null) {
                throw new UserNotFoundException("Não é possível encontrar o registro com o identificador: " + userId);
            }

            if (!existingUser.getUsername().equals(userRequest.getUsername()) && userRepository.existsByUsername(userRequest.getUsername())) {
                throw new ConflictUserException(String.format("Usuário com o username '%s' já existe", userRequest.getUsername()));
            }

            if (!existingUser.getEmail().equals(userRequest.getEmail()) && userRepository.existsByEmail(userRequest.getEmail())) {
                throw new ConflictUserException(String.format("Usuário com o email '%s' já existe", userRequest.getEmail()));
            }
            
            Set<Role> userRoles = userRequest.getRoles().stream()
            	    .map(roleRequest -> {
                        Role role = new Role();
            	        role.setId(roleRequest.getId());
            	        role.setName(roleRequest.getName());
                        role.setBiometricValidation(roleRequest.getBiometricValidation());
                        role.setRequiresTokenFirstLogin(roleRequest.getRequiresTokenFirstLogin());
            	        return role;
            	    })
            	    .collect(Collectors.toSet());

            existingUser.setUsername(userRequest.getUsername());
            existingUser.setFullName(userRequest.getFullName());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setEnabled(userRequest.isEnabled());
            existingUser.setRoles(userRoles);
            existingUser.setPasswordChangedByUser(existingUser.isPasswordChangedByUser());
            existingUser.setCreatedByAdmin(existingUser.isCreatedByAdmin());
            existingUser.setCompanyId(userRequest.getCompanyId());
            existingUser.setCpf(userRequest.getCpf());
            existingUser.setPhone(userRequest.getPhone());
            
            // Save user
            UserInfo updatedUser = userRepository.save(existingUser);
            userRepository.refresh(updatedUser);

            return modelMapper.map(updatedUser, UserResponse.class);

        } catch (Exception e) {
            log.error("Erro ao atualizar usuario ", e);
            throw new InternalErrorException("Falha ao atualizar usuário");
        }
    }
    
    public UserResponse changePassword(Long userId, String newPassword) {

        UserInfo user = getUserById(userId);

        validatePassword(newPassword);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedNewPassword = encoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        user.setPasswordChangedByUser(true);
        user.setEnabled(true);

        var userSaved = userRepository.save(user);

        return IUserMapper.INSTANCE.toResponse(userSaved);

    }

    public UserInfo getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + id));
    }

    public JwtResponseDTO authenticate(AuthRequestDTO auth) {

        try {

            log.info("Autenticando usuário: {}", auth.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(auth.getUsername(), auth.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserInfo user = userRepository.findByUsername(auth.getUsername());

                if (!user.isPasswordChangedByUser()) {
                    throw new AuthenticateException(String.format("Usuário %s não definiu a senha", auth.getUsername()));
                }
                
                boolean isAdmin = user.getRoles().stream()
                        .anyMatch(role -> role.getName() == RoleEnum.ROLE_ADMIN);

                if (!isAdmin) {
                	CompanyCollection company = companyRepository.findById(user.getCompanyId())
                			.orElseThrow(() -> new AuthenticateException("Empresa associada ao usuário não encontrada."));
                	
                	if (!company.isActive()) {
                		throw new BusinessException(
                				String.format("Empresa %s está inativa e não pode acessar o sistema.", company.getName())
                				);
                	}
                }


                String token = refreshTokenService.getTokenByUserId(user.getId());
                String accessToken = jwtService.GenerateToken(auth.getUsername());

                if (token.isEmpty()) {
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(auth.getUsername());
                    token = refreshToken.getToken();
                }

                return JwtResponseDTO.builder()
                        .accessToken(accessToken)
                        .token(token)
                        .build();

            } else {
                throw new AuthenticateException("Falha ao autenticar Usuário " + auth.getUsername());
            }

        } catch (AuthenticateException e) {
            throw e;

        } catch (DisabledException e) {
            throw new AuthenticateException(String.format("Usuário %s está desativado", auth.getUsername()));

        } catch (Exception e) {
            throw new AuthenticateException(String.format("Falha ao autenticar Usuário %s - Falha: %s",
                    auth.getUsername(), e.getMessage()));

        }

    }

    private void validateUserCreated(UserRequest userRequest) {
        boolean isAdmin = userRequest.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleEnum.ROLE_ADMIN);

        log.info("Validando usuário. É administrador? {}", isAdmin);

        if (!isAdmin && (userRequest.getCompanyId() == null || userRequest.getCompanyId().isEmpty())) {
            throw new NegocioException("O campo companyId é obrigatório para usuários não administradores.");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateUserException(String.format("O email %s já está em uso.", userRequest.getEmail()));
        }
        if (userRepository.existsByCpf(userRequest.getCpf())) {
            throw new DuplicateUserException(String.format("O CPF %s já está em uso.", userRequest.getCpf()));
        }
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new DuplicateUserException(String.format("O username %s já está em uso.", userRequest.getUsername()));
        }
    }


    public UserResponse completeRegistration(Long idUser) {
        log.info("Completando registro para usuário com ID: {}", idUser);

        try {

            var user = getUserById(idUser);

            user.setPasswordChangedByUser(true);
            user.setEnabled(true);
            user.setTokenTemporary(null);
            user.setTokenExpiredAt(null);

            var userSaved = userRepository.save(user);

            return IUserMapper.INSTANCE.toResponse(userSaved);

        } catch (NotFoundException e) {
            throw e;

        } catch (Exception e) {
            log.error("Erro ao completar o registro do usuário", e);
            throw new InternalErrorException("Falha ao completar o registro do usuário");

        }

    }

    /**
     * Valida os critérios de força da senha.
     */
    private void validatePassword(String password) {
        log.info("Validando critérios da senha");

        if (password.length() < 4 || password.length() > 8) {
            throw new NegocioException("A senha deve ter entre 4 e 8 caracteres");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new NegocioException("A senha deve conter pelo menos uma letra minúscula");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new NegocioException("A senha deve conter pelo menos uma letra maiúscula");
        }
        if (!password.matches(".*\\d.*")) {
            throw new NegocioException("A senha deve conter pelo menos um número");
        }
        if (!password.matches(".*[_@#].*")) {
            throw new NegocioException("A senha deve conter pelo menos um dos caracteres especiais: _ @ #");
        }
    }

    /**
     * Converte uma string base64 em um objeto MultipartFile.
     */
    private MultipartFile convertBase64ToMultipartFile(String base64, Long idUser) {
        log.info("Convertendo imagem base64 para MultipartFile");

        try {
            String[] parts = base64.split(",");
            byte[] fileContent = Base64.getDecoder().decode(parts[1]);
            String fileName = String.format("IMAGE_FACE_USER_%s", idUser);
            String contentType = "image/jpeg";
            return new Base64MultipartFile(fileContent, fileName, fileName, contentType);
        } catch (IllegalArgumentException e) {
            throw new NegocioException("A imagem enviada está em um formato inválido");
        }

    }
    
    public UserInfo findById(Long userId) {
        log.info("Buscando usuário pelo ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));

    }

    public List<UserInfo> findUsersByCompanyId(String companyId) {
        log.info("Buscando usuários pelo companyId: {}", companyId);

        List<UserInfo> users = userRepository.findAllByCompanyId(companyId);
        if (users.isEmpty()) {
            log.warn("Nenhum usuário encontrado para o companyId: {}", companyId);
            throw new ResourceNotFoundException("Nenhum usuário encontrado para a empresa com ID: " + companyId);
        }

        return users;
    }

    public UserInfo getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não está autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            UserInfo user = userRepository.findByUsername(username);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado");
            }

            return user;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não está autenticado");
        }
    }



}
