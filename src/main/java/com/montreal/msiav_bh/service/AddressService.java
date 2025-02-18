package com.montreal.msiav_bh.service;

import com.montreal.core.domain.exception.NotFoundException;
import com.montreal.msiav_bh.collection.AddressCollection;
import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.mapper.IAddressMapper;
import com.montreal.msiav_bh.repository.AddressRepository;
import com.montreal.msiav_bh.service.base.BaseService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AddressService extends BaseService<AddressCollection, AddressDTO> {

    @Autowired
    public AddressService(AddressRepository addressRepository, IAddressMapper addressMapper, MongoTemplate mongoTemplate) {
        super(addressRepository, addressMapper, mongoTemplate, AddressCollection.class);
    }

    @Override
    protected void setEntityId(AddressCollection entity, String id) {
        entity.setId(id);
    }

    public Page<AddressDTO> findAll(Pageable pageable, Map<String, String> searchCriteria) {
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

        // Executa a consulta paginada
        List<AddressCollection> addresses = mongoTemplate.find(query, AddressCollection.class);
        Page<AddressCollection> addressPage = PageableExecutionUtils.getPage(addresses, pageable, () -> mongoTemplate.count(query, AddressCollection.class));
        return addressPage.map(mapper::toDto);
    }
    
    public void validateAddressIdExists(String addressId) {
    	log.info("Validando se a endereço com ID {} existe.", addressId);
        boolean exists = repository.existsById(addressId);

        if (!exists) {
            log.error("Endereço com ID {} não encontrado.", addressId);
            throw new NotFoundException(String.format("Endereço com ID %s não encontrado.", addressId));
        }
    }
}
