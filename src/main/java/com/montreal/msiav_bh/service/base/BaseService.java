package com.montreal.msiav_bh.service.base;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.mapper.base.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class BaseService<E, D> {

    protected final MongoRepository<E, String> repository;
    protected final BaseMapper<E, D> mapper;
    protected final MongoTemplate mongoTemplate;
    protected final Class<E> entityClass;

    protected BaseService(MongoRepository<E, String> repository, BaseMapper<E, D> mapper, MongoTemplate mongoTemplate, Class<E> entityClass) {
        this.repository = repository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }

    public D save(D dto) {
        try {
            log.info("Salvando entidade: {}", dto);
            E entity = mapper.toEntity(dto);
            E savedEntity = repository.save(entity);
            D savedDto = mapper.toDto(savedEntity);
            log.info("Entidade salva com sucesso: {}", savedDto);
            return savedDto;
        } catch (Exception e) {
            log.error("Erro ao salvar entidade: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao salvar entidade", e);
        }
    }

    public D update(D dto, String id) {
        try {
            log.info("Atualizando entidade com ID {}: {}", id, dto);
            if (!repository.existsById(id)) {
                log.warn("Entidade com ID {} não encontrada para atualização", id);
                throw new NotFoundException("Entidade não encontrada com ID: " + id);
            }
            E entity = mapper.toEntity(dto);
            setEntityId(entity, id);
            E updatedEntity = repository.save(entity);
            D updatedDto = mapper.toDto(updatedEntity);
            log.info("Entidade atualizada com sucesso: {}", updatedDto);
            return updatedDto;
        } catch (Exception e) {
            log.error("Erro ao atualizar entidade com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao atualizar entidade", e);
        }
    }

    public Page<D> findAll(Pageable pageable) {
        try {
            log.info("Buscando todas as entidades com paginação.");
            Page<E> entitiesPage = repository.findAll(pageable);
            return entitiesPage.map(mapper::toDto);
        } catch (Exception e) {
            log.error("Erro ao buscar todas as entidades com paginação", e);
            throw new RuntimeException("Erro ao buscar todas as entidades", e);
        }
    }

    public Page<D> findAll(Pageable pageable, Map<String, String> searchCriteria) {
        try {
            Query query = new Query().with(pageable);

            if (searchCriteria != null && !searchCriteria.isEmpty()) {
                List<Criteria> criteriaList = searchCriteria.entrySet().stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .map(entry -> Criteria.where(entry.getKey()).regex(entry.getValue(), "i"))
                        .toList();

                if (!criteriaList.isEmpty()) {
                    query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
                }
            }

            List<E> entities = mongoTemplate.find(query, entityClass);
            Page<E> entityPage = PageableExecutionUtils.getPage(entities, pageable, () -> mongoTemplate.count(query, entityClass));
            return entityPage.map(mapper::toDto);
        } catch (Exception e) {
            log.error("Erro ao buscar entidades com critérios de pesquisa", e);
            throw new RuntimeException("Erro ao buscar entidades com critérios de pesquisa", e);
        }
    }

    public D findById(String id) {
        try {
            log.info("Buscando entidade por ID: {}", id);
            E entity = repository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Entidade não encontrada com ID: " + id));
            D dto = mapper.toDto(entity);
            log.info("Entidade encontrada: {}", dto);
            return dto;
        } catch (NotFoundException e) {
            log.warn("Entidade com ID {} não encontrada: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao buscar entidade por ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao buscar entidade por ID", e);
        }
    }

    public void delete(String id) {
        try {
            log.info("Deletando entidade com ID: {}", id);
            if (!repository.existsById(id)) {
                log.warn("Entidade com ID {} não encontrada para exclusão", id);
                throw new NotFoundException("Entidade não encontrada com ID: " + id);
            }
            repository.deleteById(id);
            log.info("Entidade com ID {} deletada com sucesso.", id);
        } catch (Exception e) {
            log.error("Erro ao deletar entidade com ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Erro ao deletar entidade", e);
        }
    }

    protected abstract void setEntityId(E entity, String id);
}
