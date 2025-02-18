package com.montreal.msiav_bh.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.montreal.msiav_bh.collection.VehicleAddressCollection;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProbableAddressRepository extends MongoRepository<VehicleAddressCollection, String> {

    /**
     * Busca todos os endereços prováveis associados a um veículo.
     *
     * @param vehicleId O ID do veículo.
     * @return Lista de VehicleAddress com os endereços associados ao veículo.
     */
    List<VehicleAddressCollection> findByVehicleId(String vehicleId);

    /**
     * Busca a associação entre um endereço e um veículo pelo addressId e vehicleId.
     *
     * @param addressId O ID do endereço.
     * @param vehicleId O ID do veículo.
     * @return Optional contendo a associação encontrada, se existir.
     */
    Optional<VehicleAddressCollection> findByAddressIdAndVehicleId(String addressId, String vehicleId);

    /**
     * Busca a associação de um endereço pelo ID do endereço.
     *
     * @param addressId O ID do endereço.
     * @return Optional contendo a associação encontrada, se existir.
     */
    Optional<VehicleAddressCollection> findByAddressId(String addressId);

    /**
     * Busca a associação entre um veículo e um endereço pelo vehicleId e addressId.
     *
     * @param vehicleId O ID do veículo.
     * @param addressId O ID do endereço.
     * @return Optional contendo a associação encontrada, se existir.
     */
    Optional<VehicleAddressCollection> findByVehicleIdAndAddressId(String vehicleId, String addressId);
}
