package com.montreal.msiav_bh.mapper;

import org.springframework.stereotype.Component;

import com.montreal.msiav_bh.collection.SeizureDateCollection;
import com.montreal.msiav_bh.dto.request.SeizureDateRequest;
import com.montreal.msiav_bh.dto.response.SeizureDateResponse;

@Component
public class SeizureDateMapper {

    // Converte SeizureDateRequest para SeizureDate (Entity)
    public SeizureDateCollection toEntity(SeizureDateRequest request) {
        SeizureDateCollection seizureDate = new SeizureDateCollection();
        
        seizureDate.setVeiculeId(request.getVehicleId());
        seizureDate.setSeizureDate(request.getSeizureDate());

        return seizureDate;
    }

    // Converte SeizureDate (Entity) para SeizureDateResponse (DTO)
    public SeizureDateResponse toResponse(SeizureDateCollection seizureDate) {
        return SeizureDateResponse.builder()
                .seizureDate(seizureDate.getSeizureDate()) // Mapeando a data de apreensão
                .build();
    }
}
