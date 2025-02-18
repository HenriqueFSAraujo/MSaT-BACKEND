package com.montreal.msiav_bh.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableResponse {

    @Schema(description = "Número da página atual")
    private Integer pageNumber;

    @Schema(description = "Número de elementos por página")
    private Integer pageSize;

    @Schema(description = "Total de elementos disponíveis")
    private Long totalElements;

}
