package com.imt.vehicle;

import com.imt.vehicle.model.Vehicle;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface VehicleStorageProvider {
    boolean exist(final UUID identifier);
    Collection<Vehicle> getAll();
    Optional<Vehicle> get(final UUID identifier);
    Optional<Vehicle> getByLicensePlate(final String licensePlate);
    Vehicle save(final Vehicle vehicle);
    void delete(final UUID identifier);
}
