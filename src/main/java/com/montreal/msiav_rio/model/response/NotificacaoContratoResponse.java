package com.montreal.msiav_rio.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacaoContratoResponse {

    private boolean success;
    private List<DataNotificacaoResponse> data;
    private String message;

}
