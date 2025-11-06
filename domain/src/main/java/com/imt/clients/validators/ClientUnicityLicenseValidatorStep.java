package com.imt.clients.validators;

import com.imt.clients.ClientStorageProvider;
import com.imt.clients.model.Client;
import com.imt.common.exceptions.ConflictException;
import com.imt.common.exceptions.ImtException;
import com.imt.common.validators.AbstractValidatorStep;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientUnicityLicenseValidatorStep extends AbstractValidatorStep<Client> {
    protected ClientStorageProvider service;

    @Override
    public void check(Client toValidate) throws ImtException {
        if (service.findByLicenseNumber(toValidate.getLicenseNumber()).isPresent()) {
            throw new ConflictException(String.format(
                    "A client with license number '%s' already exists.",
                    toValidate.getLicenseNumber()
            ));
        }
    }
}
