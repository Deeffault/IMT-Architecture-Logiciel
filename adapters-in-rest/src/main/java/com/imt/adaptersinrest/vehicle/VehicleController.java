package com.imt.adaptersinrest.vehicle;



import com.imt.adaptersinrest.vehicle.model.input.VehicleInput;
import com.imt.adaptersinrest.vehicle.model.input.VehicleUpdateInput;
import com.imt.adaptersinrest.vehicle.model.output.VehicleOutput;
import com.imt.common.exceptions.ImtException;
import com.imt.vehicle.VehicleServiceValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for vehicle management.
 * Entry point: /api/imt/v1/vehicles
 */
@RestController
@RequestMapping("api/imt/v1/vehicles")
public class VehicleController {

    private final VehicleServiceValidator service;

    public VehicleController(VehicleServiceValidator service) {
        this.service = service;
    }


    // Get all vehicles
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Collection<VehicleOutput> getAll() {
        return service.getAll()
                .stream()
                .map(VehicleOutput::from)
                .collect(Collectors.toList());
    }

    // Create a new vehicle
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleOutput create(@RequestBody final VehicleInput input) throws ImtException {
        return VehicleOutput.from(service.create(VehicleInput.convert(input)));
    }

    // Get a vehicle by ID
    @GetMapping(value = "/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public VehicleOutput getOne(@PathVariable String vehicleId) {
        return service.getOne(UUID.fromString(vehicleId))
                .map(VehicleOutput::from)
                .orElseThrow(() -> new NoSuchElementException("Vehicle does not exist."));
    }

    // Update a vehicle
    @PatchMapping(value = "/{vehicleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String vehicleId, @RequestBody VehicleUpdateInput input) throws ImtException {
        service.update(
                service.getOne(UUID.fromString(vehicleId)).map(
                        alreadySaved -> VehicleUpdateInput.from(input, alreadySaved)
                ).orElseThrow(() -> new NoSuchElementException("Vehicle does not exist."))
        );
    }

    // Delete a vehicle
    @DeleteMapping(value = "/{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String vehicleId) throws ImtException {
        service.delete(UUID.fromString(vehicleId));
    }
}

