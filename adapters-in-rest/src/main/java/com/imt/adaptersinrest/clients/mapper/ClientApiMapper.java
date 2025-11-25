package com.imt.adaptersinrest.clients.mapper;

import com.imt.adaptersinrest.clients.model.input.ClientInput;
import com.imt.adaptersinrest.clients.model.input.ClientUpdateInput;
import com.imt.adaptersinrest.clients.model.output.ClientOutput;
import com.imt.clients.model.Client;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ClientApiMapper {

    /**
     * Convertit un DTO de création (POST) en objet du domaine.
     */
    public Client toDomain(ClientInput dto) {
        return Client.builder()
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .licenseNumber(dto.getLicenseNumber())
                .adress(dto.getAdress())
                .build();
    }

    /**
     * Applique les modifications d'un DTO de mise à jour partielle (PATCH) à un objet du domaine.
     */
    public Client toDomain(ClientUpdateInput dto, Client existingClient) {
        return existingClient.toBuilder()
                .lastName(dto.getLastName().defaultIfNotOverwrite(existingClient.getLastName()))
                .firstName(dto.getFirstName().defaultIfNotOverwrite(existingClient.getFirstName()))
                .dateOfBirth(LocalDate.parse(dto.getDateOfBirth().defaultIfNotOverwrite(String.valueOf(existingClient.getDateOfBirth()))))
                .adress(dto.getAdress().defaultIfNotOverwrite(existingClient.getAdress()))
                .build();
    }

    /**
     * Convertit un objet du Domaine en DTO de sortie (Réponse JSON).
     */
    public ClientOutput toDto(Client client) {
        ClientOutput dto = new ClientOutput();
        dto.setId(client.getId());
        dto.setLastName(client.getLastName());
        dto.setFirstName(client.getFirstName());
        dto.setDateOfBirth(client.getDateOfBirth());
        dto.setLicenseNumber(client.getLicenseNumber());
        dto.setAdress(client.getAdress());
        return dto;
    }
}
