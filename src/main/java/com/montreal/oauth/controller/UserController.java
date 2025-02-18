package com.montreal.oauth.controller;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.montreal.oauth.domain.service.UserService;
import com.montreal.oauth.mapper.IUserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.montreal.core.domain.dto.CheckEmailDTO;
import com.montreal.core.domain.dto.response.CheckEmailResponse;
import com.montreal.core.domain.dto.response.MessageResponse;
import com.montreal.core.domain.enumerations.MessageTypeEnum;
import com.montreal.core.domain.exception.NegocioException;
import com.montreal.core.domain.service.EmailService;
import com.montreal.core.domain.service.ValidationService;
import com.montreal.core.exception_handler.ProblemType;
import com.montreal.oauth.domain.dto.AuthRequestDTO;
import com.montreal.oauth.domain.dto.CheckPasswordResetDTO;
import com.montreal.oauth.domain.dto.JwtResponseDTO;
import com.montreal.oauth.domain.dto.response.PassRecoveryResponse;
import com.montreal.oauth.domain.dto.RefreshTokenRequestDTO;
import com.montreal.oauth.domain.dto.request.UserRequest;
import com.montreal.oauth.domain.dto.response.UserResponse;
import com.montreal.oauth.domain.dto.request.PasswordChangeRequest;
import com.montreal.oauth.domain.entity.RefreshToken;
import com.montreal.oauth.domain.entity.UserInfo;
import com.montreal.oauth.domain.repository.IUserRepository;
import com.montreal.oauth.domain.service.JwtService;
import com.montreal.oauth.domain.service.RefreshTokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Usuário")
public class UserController {

    public static final String EMAIL_VERIFICATION_TITLE = "verificação de email cadastrado";
    public static final String REQUEST_SUCCESS_MESSAGE = "Requisição Realizada com Sucesso";
    public static final String REQUEST_FAILED_MESSAGE = "Requisição realizada com falha";
    public static final String PASSWORD_RECOVERY_TITLE = "recuperação de senha";

    private final IUserRepository iUserRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final ValidationService validation;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Operation(summary = "Alterar senha do usuário")
    @PatchMapping("/auth/user/{id}/password")
    public UserResponse changePassword(@PathVariable Long id,
                                       @RequestBody @Valid PasswordChangeRequest passwordChangeRequest) {

        return userService.changePassword(id, passwordChangeRequest.getNewPassword());

    }
    
    @Operation(summary = "Obter usuário por ID")
    @GetMapping("/auth/user/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        var userInfo = userService.getUserById(id);
        var UserResponse = IUserMapper.INSTANCE.toResponse(userInfo);

        return ResponseEntity.ok(UserResponse);

    }

    @Operation(summary = "Atualizar usuário")
    @PutMapping("/auth/user/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "Cadastrar usuário")
    @PostMapping("/auth/user")
    public UserResponse saveUser(@RequestBody @Valid UserRequest userRequest) {
        return userService.saveUser(userRequest);
    }

    @Operation(summary = "Listar usuários")
    @GetMapping("/auth/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            HttpServletRequest request) {
        try {
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

            Page<UserResponse> userResponses = userService.getAllUsersWithFilters(pageable, request);

            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erro ao buscar usuários", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Buscar perfis", hidden = true)
    @PostMapping("/auth/profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        UserResponse userResponse = userService.getUser();
        return ResponseEntity.ok().body(userResponse);
    }

    @Operation(summary = "Fazer login", hidden = false)
    @PostMapping("/auth/login")
    public JwtResponseDTO authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {

        return userService.authenticate(authRequestDTO);
    }


    @Operation(summary = "Atualizar token", hidden = true)
    @PostMapping("/auth/refresh-token")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken()).build();
                }).orElseThrow(() -> new NegocioException(ProblemType.RECURSO_NAO_ENCONTRADO, "Refresh Token não está no banco de dados"));
    }

    @Operation(summary = "Verificar usuário", hidden = true)
    @PostMapping("/auth/check-user-email")
    public ResponseEntity<CheckEmailResponse> checkUserEmail(@RequestBody CheckEmailDTO checkEmail) {

        Set<ConstraintViolation<CheckEmailDTO>> violations = getConstraintViolations(checkEmail);

        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
            return new ResponseEntity<>(getCheckEmailResponseError(errorMessages.toString()), HttpStatus.BAD_REQUEST);
        }

        String email = checkEmail.getEmail();
        UserResponse userInfo = userService.findByEmail(email);

        if (Optional.ofNullable(userInfo).isEmpty()) {
            return new ResponseEntity<>(getCheckEmailResponseError("Usuário não encontrado"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(getCheckEmailResponseSuccess(), HttpStatus.ACCEPTED);
    }


    @Operation(summary = "Recuperação de senha", hidden = true)
    @PostMapping("/auth/password-recovery")
    public ResponseEntity<?> passwordRecovery(@RequestBody CheckEmailDTO checkEmail) throws Exception {

        Set<ConstraintViolation<CheckEmailDTO>> violations = getConstraintViolations(checkEmail);

        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
            return new ResponseEntity<>(getCheckEmailResponseError(errorMessages.toString()), HttpStatus.BAD_REQUEST);
        }

        String email = checkEmail.getEmail();
        UserInfo userInfo = iUserRepository.findByEmail(email);

        if ((Optional.ofNullable(iUserRepository.findByEmail(email))).isEmpty()) {
            return new ResponseEntity<>(getCheckEmailResponseError("Usuário não encontrado"), HttpStatus.BAD_REQUEST);
        }


        String linkPlain = OffsetDateTime.now()
                .toEpochSecond() + "," +
                userInfo.getId().toString() + "," +
                userInfo.getFullName().replace(" ", "-") + "," +
                userInfo.getEmail() + "," +
                OffsetDateTime.now().toEpochSecond();

        String link = linkPlain;
        String linkParse = link.replace("/", "-W-");
        String linkEmail = "http://localhost:4002/reset-password?link=" + linkParse;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userInfo.setLink(link);
        userInfo.setReset(true);
        userInfo.setResetAt(timestamp);
        userService.update(userInfo);

        emailService.sendEmailFromTemplate(userInfo.getFullName(), linkEmail, userInfo.getEmail());

        List<PassRecoveryResponse.Object> objects = Collections.singletonList(PassRecoveryResponse.Object.builder().link(linkParse).build());
        return new ResponseEntity<>(getPassRecoveryResponseSuccess(objects, "Link de recuperação gerado com sucesso"), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Resetar senha", hidden = true)
    @PostMapping("/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody CheckPasswordResetDTO checkPassword) {

        Set<ConstraintViolation<CheckPasswordResetDTO>> violations = validation.getValidator().validate(checkPassword);

        if (!violations.isEmpty()) {
            List<String> objects = violations.stream().map(ConstraintViolation::getMessage).toList();

            MessageResponse msgResponse = userService.messageList("erros", objects, MessageTypeEnum.MSG_BAD_REQUEST, "A senha está mal formatada ou não foi informada!");

            return new ResponseEntity<>(msgResponse, HttpStatusCode.valueOf(msgResponse.getStatus()));
        } else {
            // Verificar e atualizar a senha com BCrypt
            String encodedPassword = passwordEncoder.encode(checkPassword.getPassword());
            MessageResponse msgResponse = userService.passwordReset(encodedPassword, checkPassword.getEmail(), checkPassword.getLink());

            return new ResponseEntity<>(msgResponse, HttpStatusCode.valueOf(msgResponse.getStatus()));
        }
    }

    @Operation(summary = "Finalizar Cadastro de usuário")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/auth/user/complete-registration/{idUser}")
    public UserResponse completeRegistration(@PathVariable Long idUser) {
        return userService.completeRegistration(idUser);

    }

    private Set<ConstraintViolation<CheckEmailDTO>> getConstraintViolations(CheckEmailDTO checkEmail) {
        return validation.getValidator().validate(checkEmail);
    }


    private static PassRecoveryResponse getPassRecoveryResponseSuccess(List<PassRecoveryResponse.Object> objects, String errorMessages) {
        return PassRecoveryResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .type(HttpStatus.ACCEPTED.getReasonPhrase())
                .detail(REQUEST_SUCCESS_MESSAGE)
                .title(PASSWORD_RECOVERY_TITLE)
                .userMessage(errorMessages)
                .objects(objects)
                .timestamp(OffsetDateTime.now())
                .build();
    }


    private static CheckEmailResponse getCheckEmailResponseError(String errorMessages) {
        return CheckEmailResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .type(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .detail(REQUEST_FAILED_MESSAGE)
                .title(EMAIL_VERIFICATION_TITLE)
                .userMessage(errorMessages)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    private static CheckEmailResponse getCheckEmailResponseSuccess() {
        return CheckEmailResponse.builder()
                .status(HttpStatus.ACCEPTED.value())
                .type(HttpStatus.ACCEPTED.getReasonPhrase())
                .detail(REQUEST_SUCCESS_MESSAGE)
                .title(EMAIL_VERIFICATION_TITLE)
                .userMessage("Email encontra-se cadastrado")
                .timestamp(OffsetDateTime.now())
                .build();
    }

}
