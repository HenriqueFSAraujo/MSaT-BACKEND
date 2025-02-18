package com.montreal.msiav_bh.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.dto.response.VehicleResponse;
import com.montreal.msiav_rio.model.response.ContractCompleteResponse;
import com.montreal.msiav_rio.model.response.VeiculoDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/download-pdf")
@RequiredArgsConstructor
public class PdfController {

   private final VehicleResponse vehicleResponse;

    @GetMapping("/{vehicleId}")
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable("vehicleId") String vehicleId) throws IOException {
        ContractCompleteResponse contractCompleteResponse = obterContractCompleteResponse(vehicleId);
        byte[] dadosPdf = Base64.getDecoder().decode(contractCompleteResponse.getData().getContrato().getCertidaoBuscaApreensao());

        byte[] pdfBytes = gerarPdf(); // Implementar a lógica correta para gerar o PDF
        ByteArrayInputStream bis = new ByteArrayInputStream(pdfBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=vehicle_" + vehicleId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private byte[] gerarPdf() {
        return new byte[0]; // PDF gerado em bytes
    }

    private ContractCompleteResponse obterContractCompleteResponse(String vehicleId) {
        ContractCompleteResponse contractCompleteResponse = new ContractCompleteResponse();

        if (contractCompleteResponse.getData() != null && contractCompleteResponse.getData().getVeiculos() != null) {
            for (VeiculoDTO vehicle : contractCompleteResponse.getData().getVeiculos()) {
                if (vehicleResponse.getId().equals(vehicleId)) {
                    System.out.println("Veículo encontrado");
                    return contractCompleteResponse;
                }
            }
        }

        System.out.println("Veículo não encontrado.");
        return new ContractCompleteResponse();
    }

}
