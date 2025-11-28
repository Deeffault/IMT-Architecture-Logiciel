package com.imt.vehicle.validators;

import com.imt.common.exceptions.ConflictException;
import com.imt.common.exceptions.ImtException;
import com.imt.common.validators.AbstractValidatorStep;
import com.imt.vehicle.VehicleStorageProvider;
import com.imt.vehicle.model.Vehicle;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class VehicleAlreadyExistValidatorStep extends AbstractValidatorStep<Vehicle> {

    protected VehicleStorageProvider service;

    @Override
    public void check(final Vehicle toValidate) throws ImtException {
        this.service.getByLicensePlate(toValidate.getLicensePlate())
                .ifPresent(existingVehicle -> {
                    if (!existingVehicle.getId().equals(toValidate.getId())) {
                        try {
                            throw new ConflictException(
                                    String.format("Un véhicule avec la plaque d'immatriculation '%s' existe déjà.", toValidate.getLicensePlate())
                            );
                        } catch (ConflictException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }
}
