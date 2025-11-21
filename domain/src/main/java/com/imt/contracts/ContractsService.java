package com.imt.contracts;

import com.imt.common.exceptions.ImtException;
import com.imt.contracts.model.Contract;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ContractsService {

    protected ContractStorageProvider service;

    public Collection<Contract> getAll() {
        return Objects.requireNonNullElse(this.service.getAll(), Collections.emptySet());
    }

    public Optional<Contract> getOne(final UUID identifier) {
        return this.service.get(identifier);
    }

    public Collection<Contract> getByClient(final UUID clientIdentifier) {
        return Objects.requireNonNullElse(
                this.service.findByClientIdentifier(clientIdentifier),
                Collections.emptySet()
        );
    }

    public Collection<Contract> getByVehicle(final UUID vehicleIdentifier) {
        return Objects.requireNonNullElse(
                this.service.findByVehicleIdentifier(vehicleIdentifier),
                Collections.emptySet()
        );
    }

    public Collection<Contract> getByVehicleBetween(final UUID vehicleIdentifier,
                                                    final LocalDate startDate,
                                                    final LocalDate endDate) {
        return Objects.requireNonNullElse(
                this.service.findByVehicleIdentifierBetween(vehicleIdentifier, startDate, endDate),
                Collections.emptySet()
        );
    }

    public Contract create(final Contract newContract) throws ImtException {
        return this.service.save(newContract);
    }

    public void update(final Contract updatedContract) throws ImtException {
        this.service.save(updatedContract);
    }

    public void delete(final UUID identifier) throws ImtException {
        this.service.delete(identifier);
    }
}


