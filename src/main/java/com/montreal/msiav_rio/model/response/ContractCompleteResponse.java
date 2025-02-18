package com.montreal.msiav_rio.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractCompleteResponse {

    private boolean success;
    private DataContractCompleteResponse data;
    private String message;

}
