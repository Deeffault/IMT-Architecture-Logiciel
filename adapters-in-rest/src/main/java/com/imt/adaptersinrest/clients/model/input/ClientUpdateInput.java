package com.imt.adaptersinrest.clients.model.input;

import com.imt.adaptersinrest.common.model.input.AbstractUpdateInput;
import com.imt.adaptersinrest.common.model.input.UpdatableProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour la mise à jour partielle (PATCH) d'un client.
 * Utilise UpdatableProperty pour chaque champ.
 */
@Getter
@Setter
public class ClientUpdateInput extends AbstractUpdateInput {

    private UpdatableProperty<String> lastName = UpdatableProperty.empty();
    private UpdatableProperty<String> firstName = UpdatableProperty.empty();
    private UpdatableProperty<String> dateOfBirth = UpdatableProperty.empty();
    private UpdatableProperty<String> adress = UpdatableProperty.empty();

    // On ne permet pas la mise à jour du numéro de permis (identifiant métier)
    // private UpdatableProperty<String> licenseNumber = UpdatableProperty.empty();

}
