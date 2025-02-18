package com.montreal.msiav_bh.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.montreal.msiav_bh.collection.VehicleAddressCollection;
import com.montreal.msiav_bh.controller.base.BaseController;
import com.montreal.msiav_bh.dto.ProbableAddressDTO;
import com.montreal.msiav_bh.service.ProbableAddressService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Endereço Provável do Veículo")
public class ProbableAddressController extends BaseController<VehicleAddressCollection, ProbableAddressDTO> {

    @Autowired
    private ProbableAddressService probableAddressService;

    protected ProbableAddressController(ProbableAddressService probableAddressService) {
        super(probableAddressService);
    }

    @PostMapping("/probable-address")
    @ResponseStatus(HttpStatus.CREATED)
    public ProbableAddressDTO addProbableAddress(@RequestBody  ProbableAddressDTO request) {
        return probableAddressService.save(request.getVehicleId(), request.getAddress());
    }

    @PutMapping("/probable-address/{addressId}")
    public ProbableAddressDTO updateProbableAddress(@PathVariable String addressId, @RequestBody  ProbableAddressDTO request) {
        return probableAddressService.update(request.getVehicleId(), addressId, request.getAddress());
    }

    @GetMapping("/probable-address/{vehicleId}")
    public List<ProbableAddressDTO> findProbableAddressesByVehicleId(@PathVariable String vehicleId) {
        return probableAddressService.findByVehicleId(vehicleId);
    }

    @GetMapping("/probable-address/address/{addressId}")
    public ProbableAddressDTO findProbableAddressByAddressId(@PathVariable String addressId) {
        return probableAddressService.findByAddressId(addressId);
    }

    @DeleteMapping("/probable-address/{addressId}/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProbableAddress(@PathVariable String addressId, @PathVariable String vehicleId) {
        probableAddressService.delete(addressId, vehicleId);
    }
}
