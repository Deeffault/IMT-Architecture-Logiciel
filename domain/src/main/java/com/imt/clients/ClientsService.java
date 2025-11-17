package com.imt.clients;

import com.imt.clients.model.Client;
import com.imt.common.exceptions.ImtException;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * Service métier de gestion des clients.
 * Fournit les opérations CRUD pour les clients.
 */
@AllArgsConstructor
public class ClientsService {
    protected ClientStorageProvider service;

    public Collection<Client> getAll() {
        return Objects.requireNonNullElse(this.service.getAll(), Collections.emptySet());
    }

    public Optional<Client> getOne(final UUID id) {
        return this.service.get(id);
    }

    public Client create(final Client client) throws ImtException {
        return this.service.save(client);
    }

    public void update(final Client client) throws ImtException {
        this.service.save(client);
    }

    public void delete(final UUID id) throws ImtException {
        this.service.delete(id);
    }
}
