package com.imt.clients;

import com.imt.clients.model.Client;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientStorageProvider {
    /**
     * Vérifie si un client existe.
     *
     * @param id l'identifiant du client
     * @return true si le client existe, false sinon
     */
    boolean exist(final UUID id);

    /**
     * Récupère tous les clients stockés.
     *
     * @return une collection de tous les clients, ou une collection vide si aucun client n'existe
     */
    Collection<Client> getAll();

    /**
     * Récupère un client spécifique par son identifiant.
     *
     * @param id l'identifiant unique du client
     * @return un Optional contenant le client s'il existe, Optional.empty() sinon
     */
    Optional<Client> get(final UUID id);

    /**
     * Sauvegarde un client (création ou mise à jour).
     *
     * @param client le client à sauvegarder
     * @return le client sauvegardé
     * @throws NullPointerException si le client est null
     */
    Client save(final Client client);

    /**
     * Supprime un client du stockage.
     *
     * @param id l'identifiant du client à supprimer
     */
    void delete(final UUID id);
}
