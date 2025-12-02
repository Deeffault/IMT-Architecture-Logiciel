package com.imt.adaptersinrest.contracts;

import com.imt.adaptersinrest.contracts.model.input.ContractInput;
import com.imt.adaptersinrest.contracts.model.input.ContractUpdateInput;
import com.imt.adaptersinrest.contracts.model.output.ContractOutput;
import com.imt.common.exceptions.ImtException;
import com.imt.contracts.ContractsServiceValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller pour la gestion des contrats de location.
 * Point d'entrée : /api/imt/v1/contracts
 *
 * Aligne l'exposition REST sur la logique du domaine (ContractsService / ContractsServiceValidator)
 * et sur le style utilisé pour les clients et les véhicules.
 */
@RestController
@RequestMapping("api/imt/v1/contracts")
public class ContractsController {

    private final ContractsServiceValidator service;

    public ContractsController(final ContractsServiceValidator service) {
        this.service = service;
    }

    /**
     * Récupération de tous les contrats.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Collection<ContractOutput> getAll() {
        return service.getAll()
                .stream()
                .map(ContractOutput::from)
                .collect(Collectors.toList());
    }

    /**
     * Récupération d'un contrat par son identifiant.
     */
    @GetMapping(value = "/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ContractOutput getOne(@PathVariable final String contractId) {
        return service.getOne(UUID.fromString(contractId))
                .map(ContractOutput::from)
                .orElseThrow(() -> new NoSuchElementException("Contract does not exist."));
    }

    /**
     * Récupération des contrats d'un client.
     */
    @GetMapping(value = "/by-client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Collection<ContractOutput> getByClient(@PathVariable final String clientId) {
        return service.getByClient(UUID.fromString(clientId))
                .stream()
                .map(ContractOutput::from)
                .collect(Collectors.toList());
    }

    /**
     * Récupération des contrats d'un véhicule.
     */
    @GetMapping(value = "/by-vehicle/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Collection<ContractOutput> getByVehicle(@PathVariable final String vehicleId) {
        return service.getByVehicle(UUID.fromString(vehicleId))
                .stream()
                .map(ContractOutput::from)
                .collect(Collectors.toList());
    }

    /**
     * Création d'un nouveau contrat.
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ContractOutput create(@RequestBody final ContractInput input) throws ImtException {
        return ContractOutput.from(service.create(ContractInput.convert(input)));
    }

    /**
     * Mise à jour partielle d'un contrat existant.
     */
    @PatchMapping(value = "/{contractId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable final String contractId,
                       @RequestBody final ContractUpdateInput input) throws ImtException {
        service.update(
                service.getOne(UUID.fromString(contractId))
                        .map(existing -> ContractUpdateInput.from(input, existing))
                        .orElseThrow(() -> new NoSuchElementException("Contract does not exist."))
        );
    }

    /**
     * Suppression d'un contrat.
     */
    @DeleteMapping(value = "/{contractId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final String contractId) throws ImtException {
        service.delete(UUID.fromString(contractId));
    }
}


