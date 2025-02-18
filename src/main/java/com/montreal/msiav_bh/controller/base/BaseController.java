package com.montreal.msiav_bh.controller.base;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.montreal.core.annotations.CompanyFilterRequestAsQueryParam;
import com.montreal.msiav_bh.service.base.BaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Base API", description = "Endpoints comuns para operações CRUD")
public abstract class BaseController<E, D> {

    protected final BaseService<E, D> service;

    protected BaseController(BaseService<E, D> service) {
        this.service = service;
    }

    @Operation(summary = "Criar um novo item")
    @ApiResponse(responseCode = "201", description = "Item criado com sucesso")
    @CompanyFilterRequestAsQueryParam
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public D create(@RequestBody D dto) {
        try {
            log.info("Criando novo item: {}", dto);
            D createdItem = service.save(dto);
            log.info("Item criado com sucesso: {}", createdItem);
            return createdItem;
        } catch (Exception e) {
            log.error("Erro ao criar item: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Buscar item por ID")
    @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso")
    @CompanyFilterRequestAsQueryParam
    @GetMapping("/{id}")
    public D findById(@PathVariable String id) {
        try {
            log.info("Buscando item por ID: {}", id);
            D item = service.findById(id);
            log.info("Item encontrado: {}", item);
            return item;
        } catch (Exception e) {
            log.error("Erro ao buscar item por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Atualizar um item existente")
    @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso")
    @CompanyFilterRequestAsQueryParam
    @PutMapping("/{id}")
    public D update(@RequestBody D dto, @PathVariable String id) {
        try {
            log.info("Atualizando item com ID {}: {}", id, dto);
            D updatedItem = service.update(dto, id);
            log.info("Item atualizado com sucesso: {}", updatedItem);
            return updatedItem;
        } catch (Exception e) {
            log.error("Erro ao atualizar item com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Deletar um item por ID")
    @ApiResponse(responseCode = "204", description = "Item deletado com sucesso")
    @CompanyFilterRequestAsQueryParam
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        try {
            log.info("Deletando item com ID: {}", id);
            service.delete(id);
            log.info("Item com ID {} deletado com sucesso.", id);
        } catch (Exception e) {
            log.error("Erro ao deletar item com ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    @Operation(summary = "Listar todos os recursos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recursos recuperados com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro ao buscar recursos")
    })
    @GetMapping
    public ResponseEntity<Page<D>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            HttpServletRequest request
        ) {
            try {
                Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));

                // Construir critérios de busca a partir de todos os parâmetros de consulta, excluindo "page", "size", e "sort"
                Map<String, String> searchCriteria = new HashMap<>();
                Enumeration<String> parameterNames = request.getParameterNames();

                while (parameterNames.hasMoreElements()) {
                    String paramName = parameterNames.nextElement();
                    if (!List.of("page", "size", "sort").contains(paramName)) {
                        searchCriteria.put(paramName, request.getParameter(paramName));
                    }
                }

                log.info("Critérios de busca aplicados: {}", searchCriteria);

                Page<D> dtoPage = searchCriteria.isEmpty()
                        ? service.findAll(pageable)
                        : service.findAll(pageable, searchCriteria);

                return new ResponseEntity<>(dtoPage, HttpStatus.OK);
            } catch (Exception e) {
                log.error("Erro ao buscar recursos", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
}
