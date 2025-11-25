package com.imt.adaptersinrest.clients;

import com.imt.adaptersinrest.clients.mapper.ClientApiMapper;
import com.imt.adaptersinrest.clients.model.input.ClientInput;
import com.imt.adaptersinrest.clients.model.input.ClientUpdateInput;
import com.imt.adaptersinrest.clients.model.output.ClientOutput;
import com.imt.clients.ClientsService;
import com.imt.clients.ClientsServiceValidator;
import com.imt.clients.model.Client;
import com.imt.common.exceptions.ImtException;
import com.imt.common.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Controleur REST Clients
 */
@RestController
@RequestMapping("/api/v1/clients")
public class ClientsController {

    private final ClientsServiceValidator clientsServiceValidator;
    private final ClientsService clientsService;
    private final ClientApiMapper mapper;

    // L'erreur "Could not autowire" ici est un faux positif de l'IDE si BeanConfiguration n'est pas scanné.
    // Au runtime, Spring trouvera les Beans définis dans BeanConfiguration.
    // C'est à mettre dans le module application si on veut éviter ce genre de problème au moment de build l'app.
    public ClientsController(
            ClientsServiceValidator clientsServiceValidator,
            ClientsService clientsService,
            ClientApiMapper mapper) {
        this.clientsServiceValidator = clientsServiceValidator;
        this.clientsService = clientsService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ClientOutput> createClient(@Valid @RequestBody ClientInput clientInput)
            throws ImtException {
        Client clientDomain = mapper.toDomain(clientInput);
        Client clientCree = clientsServiceValidator.create(clientDomain);
        return new ResponseEntity<>(mapper.toDto(clientCree), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientOutput> updateClient(
            @PathVariable String id,
            @RequestBody ClientUpdateInput clientUpdateInput
    ) throws ImtException {

        Client existingClient = clientsService.getOne(id)
                // Correction : Utilisation de ResourceNotFoundException au lieu de ImtException
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'id " + id));

        Client clientToUpdate = mapper.toDomain(clientUpdateInput, existingClient);
        clientsServiceValidator.update(clientToUpdate);

        return ResponseEntity.ok(mapper.toDto(clientToUpdate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientOutput> getClientById(@PathVariable String id) throws ImtException {
        Client client = clientsService.getOne(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé"));

        return ResponseEntity.ok(mapper.toDto(client));
    }

    @GetMapping
    public ResponseEntity<Collection<ClientOutput>> getAllClients() {
        Collection<ClientOutput> dtos = clientsService.getAll().stream()
                .map(mapper::toDto) // Cela devrait marcher si toDto est public dans le mapper
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) throws ImtException {
        clientsService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
