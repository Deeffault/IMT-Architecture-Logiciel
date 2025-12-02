package com.imt.adaptersoutbdd.contracts.repositories;

import com.imt.adaptersoutbdd.contracts.repositories.entities.ContractEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends MongoRepository<ContractEntity, String> {

    List<ContractEntity> findByClientId(String clientId);

    List<ContractEntity> findByVehicleId(String vehicleId);

    List<ContractEntity> findByVehicleIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String vehicleId,
            LocalDate startDate,
            LocalDate endDate
    );
}


