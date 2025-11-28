package com.imt.adaptersoutbdd.vehicle;

import com.imt.adaptersoutbdd.vehicle.repositories.VehicleRepository;
import com.imt.adaptersoutbdd.vehicle.repositories.mappers.VehicleBddMapper;
import com.imt.vehicle.VehicleStorageProvider;
import com.imt.vehicle.model.Vehicle;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VehicleBddService implements VehicleStorageProvider {

    private VehicleRepository vehicleRepository;

    private VehicleBddMapper vehicleBddMapper;

    @Override
    public boolean exist(UUID identifier) {
        return false;
    }

    @Override
    public Collection<Vehicle> getAll() {
        List<Vehicle> vehicles = vehicleRepository.findAll()
                .stream()
                .map(vehicleBddMapper::from)
                .toList();
        return vehicles;
    }

    @Override
    public Optional<Vehicle> get(UUID identifier) {
        return Optional.ofNullable(identifier)
                .map(UUID::toString)
                .flatMap(this.vehicleRepository::findById)
                .map(this.vehicleBddMapper::from);
    }

    @Override
    public Vehicle save(final Vehicle vehicle) {
        Optional.ofNullable(vehicle);  
        return this.vehicleBddMapper.from(
                this.vehicleRepository.save(
                        this.vehicleBddMapper.to(vehicle)
                )
        );
    }

    @Override
    public void delete(UUID identifier) {
        Optional.ofNullable(identifier)
                .map(UUID::toString)
                .ifPresent(this.vehicleRepository::deleteById);
    }

    @Override
    public Optional<Vehicle> getByLicensePlate(String licensePlate) {
        return Optional.ofNullable(licensePlate)
                .flatMap(this.vehicleRepository::findByLicensePlate)
                .map(this.vehicleBddMapper::from);
    }
}
