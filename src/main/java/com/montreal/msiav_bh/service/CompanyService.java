package com.montreal.msiav_bh.service;

import java.util.List;
import java.util.Map;

import com.montreal.core.domain.exception.NegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.montreal.msiav_bh.collection.AddressCollection;
import com.montreal.msiav_bh.collection.CompanyCollection;
import com.montreal.msiav_bh.dto.CompanyDTO;
import com.montreal.msiav_bh.enumerations.CompanyTypeEnum;
import com.montreal.msiav_bh.mapper.ICompanyMapper;
import com.montreal.msiav_bh.repository.CompanyRepository;
import com.montreal.msiav_bh.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CompanyService extends BaseService<CompanyCollection, CompanyDTO> {

    @Autowired
    public CompanyService(CompanyRepository companyRepository, ICompanyMapper companyMapper, MongoTemplate mongoTemplate) {
        super(companyRepository, companyMapper, mongoTemplate, CompanyCollection.class);
    }

    @Override
    protected void setEntityId(CompanyCollection entity, String id) {
        entity.setId(id);
    }

    public Page<CompanyDTO> findAll(Pageable pageable, Map<String, String> searchCriteria) {
        Query query = new Query().with(pageable);

        // Construção dinâmica dos critérios de busca usando AND
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            searchCriteria.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .forEach(entry -> query.addCriteria(Criteria.where(entry.getKey()).regex(entry.getValue(), "i")));
        }

        // Executa a consulta paginada
        List<CompanyCollection> companies = mongoTemplate.find(query, CompanyCollection.class);

        // Verifica e corrige o mapeamento de "address" se necessário
        companies.forEach(company -> {
            if (company.getAddress() == null) {
                // Adicione lógica para lidar com casos onde o address é nulo
                company.setAddress(new AddressCollection()); // Substitua por valores padrão, se necessário
            }
        });

        Page<CompanyCollection> companyPage = PageableExecutionUtils.getPage(
            companies,
            pageable,
            () -> mongoTemplate.count(query, CompanyCollection.class)
        );

        return companyPage.map(mapper::toDto);
    }

    public List<CompanyCollection> findAll() {
        return mongoTemplate.findAll(CompanyCollection.class);
    }
    
    public List<CompanyCollection> findAllByCompanyType(CompanyTypeEnum companyType) {
        Query query = new Query();
        query.addCriteria(Criteria.where("companyType").is(companyType));

        return mongoTemplate.find(query, CompanyCollection.class);
    }
    
    public void validateCompanyIdExists(String companyId) {
        log.info("Validando se a empresa com ID {} existe.", companyId);
        boolean exists = repository.existsById(companyId);

        if (!exists) {
            log.error("Empresa com ID {} não encontrada.", companyId);
            throw new NegocioException(String.format("Empresa com ID %s não encontrada.", companyId));
        }
    }


}