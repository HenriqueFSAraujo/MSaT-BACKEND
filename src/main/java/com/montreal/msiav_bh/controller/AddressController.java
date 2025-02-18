package com.montreal.msiav_bh.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.montreal.msiav_bh.collection.AddressCollection;
import com.montreal.msiav_bh.controller.base.BaseController;
import com.montreal.msiav_bh.dto.AddressDTO;
import com.montreal.msiav_bh.service.AddressService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/address")
@Tag(name = "Endereço", description = "Operações de CRUD para gerenciamento de endereços")
public class AddressController extends BaseController<AddressCollection, AddressDTO> {

    public AddressController(AddressService addressService) {
        super(addressService);
    }
}
