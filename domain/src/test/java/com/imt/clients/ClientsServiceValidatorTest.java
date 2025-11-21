package com.imt.clients;

import com.imt.clients.model.Client;
import com.imt.common.exceptions.ConflictException;
import com.imt.common.exceptions.ImtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientsServiceValidator - Tests d'intégration du domaine")
class ClientsServiceValidatorTest {

    @Mock
    private ClientStorageProvider repository;

    private ClientsServiceValidator service;

    @BeforeEach
    void setUp() {
        // On injecte le mock du repository dans le service
        service = new ClientsServiceValidator(repository);
    }

    @Test
    @DisplayName("CREATE - Doit créer le client quand tout est valide")
    void shouldCreateClientWhenAllValid() throws ImtException {
        // Given
        Client newClient = Client.builder()
                .id(UUID.randomUUID().toString())
                .lastName("Valid")
                .firstName("User")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .licenseNumber("AB123YZ")
                .adress("123 Rue Test")
                .build();

        // Mock des vérifications d'unicité (rien n'existe)
        when(repository.findByLastNameAndFirstNameAndBirthDate(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(repository.findByLicenseNumber(any()))
                .thenReturn(Optional.empty());

        // Mock de la sauvegarde
        when(repository.save(any(Client.class))).thenReturn(newClient);

        // When
        Client created = service.create(newClient);

        // Then
        assertThat(created).isNotNull();
        verify(repository).save(newClient); // Vérifie que save() a été appelé
    }


    @Test
    @DisplayName("CREATE - Doit échouer si le permis existe déjà")
    void shouldNotCreateClientWhenLicenseExists() {
        // Given
        String existingLicense = "AB123YZ"; // Une valeur VALIDE pour le pattern, mais DÉJÀ PRISE

        Client invalidClient = Client.builder()
                .lastName("Valid")
                .firstName("User")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .licenseNumber(existingLicense)
                .adress("123 Rue Test")
                .build();

        // Mock : On dit "Si on cherche 'AB123YZ', on trouve quelqu'un"
        when(repository.findByLicenseNumber(existingLicense))
                .thenReturn(Optional.of(Client.builder().build()));

        // When & Then
        // 1. La ConstraintValidatorStep va passer (car "AB123YZ" respecte le pattern)
        // 2. La ClientUnicityLicenseValidatorStep va échouer (car le mock dit qu'il existe)
        assertThatThrownBy(() -> service.create(invalidClient))
                .isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("UPDATE - Doit mettre à jour le client")
    void shouldUpdateClient() throws ImtException {
        // Given
        Client updateClient = Client.builder()
                .id(UUID.randomUUID().toString())
                .lastName("Updated")
                .firstName("Name")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .licenseNumber("AB123XX")
                .adress("New Adress")
                .build();

        // Pour l'update, votre code actuel ne re-vérifie pas l'unicité,
        // il vérifie juste les contraintes techniques (@NotNull...)

        // When
        service.update(updateClient);

        // Then
        verify(repository).save(updateClient);
    }
}