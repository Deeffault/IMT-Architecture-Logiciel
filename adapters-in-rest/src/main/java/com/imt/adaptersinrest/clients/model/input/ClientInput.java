package com.imt.adaptersinrest.clients.model.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour la création (POST) d'un client.
 */
@Getter
@Setter
public class ClientInput {
    @NotNull(message = "Last name cannot be null")
    @Pattern(regexp = "^[a-zA-Z- ]{2,100}$", message = "Last name is invalid")
    private String lastName;

    @NotNull(message = "First name cannot be null")
    @Pattern(regexp = "^[a-zA-Z- ]{2,100}$", message = "First name is invalid")
    private String firstName;

    @NotNull(message = "License number cannot be null")
    @Pattern(regexp = "^[A-Za-z]{2}\\d{3}[A-Za-z]{2}$", message = "License number pattern is invalid")
    private String licenseNumber;

    @NotNull(message = "Adress cannot be null")
    @Size(min = 5, max = 255, message = "Adress must be between 5 and 255 characters")
    private String adress;
}
