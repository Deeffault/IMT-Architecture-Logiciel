package com.imt.contracts;

import com.imt.contracts.model.Contract;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ContractStorageProvider {
    boolean exist(final UUID identifier);

    Collection<Contract> getAll();

    Optional<Contract> get(final UUID identifier);

    Collection<Contract> findByClientIdentifier(final UUID clientIdentifier);

    Collection<Contract> findByVehicleIdentifier(final UUID vehicleIdentifier);

    Collection<Contract> findByVehicleIdentifierBetween(final UUID vehicleIdentifier, final LocalDate startDate, final LocalDate endDate);

    Contract save(final Contract contract);

    void delete(final UUID identifier);
}


