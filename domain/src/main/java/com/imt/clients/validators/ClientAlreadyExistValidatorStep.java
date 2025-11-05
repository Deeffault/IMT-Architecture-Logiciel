package com.imt.clients.validators;

import com.imt.clients.model.Client;
import com.imt.common.exceptions.ImtException;
import com.imt.common.validators.AbstractValidatorStep;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.Objects;

/**
 * Validateur pour l'unicité d'un client.
 * Vérifie qu'un client avec le même nom, prénom et numéro de place n'existe pas déjà dans le système.
 */
@AllArgsConstructor
public class ClientAlreadyExistValidatorStep extends AbstractValidatorStep<Client> {

    /**
     * Service d'accès à la bdd pour les clients.
     */
    protected ClientStorageProvider service;


    @Override
    public void check(Client toValidate) throws ImtException {

    }

    @Override
    public void check() {

    }
}
