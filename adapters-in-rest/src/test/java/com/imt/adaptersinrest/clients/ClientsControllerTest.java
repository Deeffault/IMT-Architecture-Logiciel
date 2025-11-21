package com.imt.adaptersinrest.clients;

import com.imt.adaptersinrest.clients.mapper.ClientApiMapper;
import com.imt.adaptersinrest.clients.model.input.ClientInput;
import com.imt.adaptersinrest.clients.model.input.ClientUpdateInput;
import com.imt.adaptersinrest.clients.model.output.ClientOutput;
import com.imt.clients.ClientsService;
import com.imt.clients.ClientsServiceValidator;
import com.imt.clients.model.Client;
import com.imt.common.exceptions.ConflictException;
import com.imt.common.exceptions.ImtException;
import com.imt.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientsController - Tests unitaires")
class ClientsControllerTest {

    @Mock
    private ClientsServiceValidator clientsServiceValidator;

    @Mock
    private ClientsService clientsService;

    @Mock
    private ClientApiMapper mapper;

    private ClientsController controller;

    private ClientInput clientInput;
    private Client clientDomain;
    private ClientOutput clientOutput;
    private String clientId = "client-123";

    @BeforeEach
    void setUp() {
        controller = new ClientsController(clientsServiceValidator, clientsService, mapper);

        clientInput = new ClientInput();
        clientInput.setLastName("Dupont");
        clientInput.setFirstName("Jean");

        clientDomain = Client.builder()
                .id(clientId)
                .lastName("Dupont")
                .firstName("Jean")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .licenseNumber("AB123CD")
                .adress("Paris")
                .build();

        clientOutput = new ClientOutput();
        clientOutput.setId(clientId);
        clientOutput.setLastName("Dupont");
    }

    @Nested
    @DisplayName("createClient (POST)")
    class CreateTests {

        @Test
        @DisplayName("Doit créer un client avec succès (201 Created)")
        void shouldCreateClientSuccessfully() throws ImtException {
            when(mapper.toDomain(clientInput)).thenReturn(clientDomain);
            when(clientsServiceValidator.create(clientDomain)).thenReturn(clientDomain);
            when(mapper.toDto(clientDomain)).thenReturn(clientOutput);

            ResponseEntity<ClientOutput> response = controller.createClient(clientInput);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(clientOutput);
            verify(clientsServiceValidator).create(clientDomain);
        }

        @Test
        @DisplayName("Doit propager l'exception si le service échoue (ex: Conflict)")
        void shouldPropagateExceptionWhenServiceFails() throws ImtException {
            when(mapper.toDomain(clientInput)).thenReturn(clientDomain);
            doThrow(new ConflictException("Client existe déjà"))
                    .when(clientsServiceValidator).create(clientDomain);

            assertThatThrownBy(() -> controller.createClient(clientInput))
                    .isInstanceOf(ConflictException.class)
                    .hasMessage("Client existe déjà");
        }
    }

    @Nested
    @DisplayName("updateClient (PATCH)")
    class UpdateTests {

        private ClientUpdateInput updateInput;

        @BeforeEach
        void setUpUpdate() {
            updateInput = new ClientUpdateInput();
        }

        @Test
        @DisplayName("Doit mettre à jour un client existant avec succès (200 OK)")
        void shouldUpdateClientSuccessfully() throws ImtException {
            when(clientsService.getOne(clientId)).thenReturn(Optional.of(clientDomain));

            Client updatedClient = clientDomain.toBuilder().lastName("NouveauNom").build();
            when(mapper.toDomain(updateInput, clientDomain)).thenReturn(updatedClient);

            ClientOutput updatedOutput = new ClientOutput();
            updatedOutput.setLastName("NouveauNom");
            when(mapper.toDto(updatedClient)).thenReturn(updatedOutput);

            ResponseEntity<ClientOutput> response = controller.updateClient(clientId, updateInput);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(updatedOutput);
            verify(clientsServiceValidator).update(updatedClient);
        }

        @Test
        @DisplayName("Doit lever une exception si le client n'existe pas")
        void shouldThrowExceptionWhenClientNotFound() throws ImtException {
            lenient().when(clientsService.getOne(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.updateClient(clientId, updateInput))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Client non trouvé");

            verify(clientsServiceValidator, never()).update(any());
        }
    }

    @Nested
    @DisplayName("getClientById (GET)")
    class GetByIdTests {

        @Test
        @DisplayName("Doit retourner un client existant (200 OK)")
        void shouldReturnClientWhenFound() throws ImtException {
            when(clientsService.getOne(clientId)).thenReturn(Optional.of(clientDomain));
            when(mapper.toDto(clientDomain)).thenReturn(clientOutput);

            ResponseEntity<ClientOutput> response = controller.getClientById(clientId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(clientOutput);
        }

        @Test
        @DisplayName("Doit lever une exception si le client n'est pas trouvé")
        void shouldThrowExceptionWhenNotFound() {
            when(clientsService.getOne(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> controller.getClientById(clientId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllClients (GET)")
    class GetAllTests {

        @Test
        @DisplayName("Doit retourner la liste des clients")
        void shouldReturnAllClients() {
            List<Client> clients = Arrays.asList(clientDomain, clientDomain);
            when(clientsService.getAll()).thenReturn(clients);
            when(mapper.toDto(any(Client.class))).thenReturn(clientOutput);

            ResponseEntity<Collection<ClientOutput>> response = controller.getAllClients();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
            verify(clientsService).getAll();
        }

        @Test
        @DisplayName("Doit retourner une liste vide si aucun client")
        void shouldReturnEmptyListWhenNoClients() {
            lenient().when(clientsService.getAll()).thenReturn(Collections.emptyList());

            ResponseEntity<Collection<ClientOutput>> response = controller.getAllClients();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEmpty();
        }
    }

    @Nested
    @DisplayName("deleteClient (DELETE)")
    class DeleteTests {

        @Test
        @DisplayName("Doit supprimer un client avec succès (204 No Content)")
        void shouldDeleteClientSuccessfully() throws ImtException {
            ResponseEntity<Void> response = controller.deleteClient(clientId);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(clientsService).delete(clientId);
        }
    }
}