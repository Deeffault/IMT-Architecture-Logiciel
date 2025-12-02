package com.imt.adaptersoutbdd.vehicle;

import com.imt.adaptersoutbdd.vehicle.repositories.VehicleRepository;
import com.imt.adaptersoutbdd.vehicle.repositories.entities.VehicleEntity;
import com.imt.adaptersoutbdd.vehicle.repositories.mappers.VehicleBddMapper;
import com.imt.vehicle.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleBddService - Tests unitaires")
class VehicleBddServiceTest {

    @Mock
    private VehicleRepository repository;

    @Mock
    private VehicleBddMapper mapper;

    @InjectMocks
    private VehicleBddService service;

    private final String testId = "veh-123";
    private final UUID testUUID = UUID.fromString(testId);
    private Vehicle testVehicle;
    private VehicleEntity testVehicleEntity;

    @BeforeEach
    void setUp() {
        testVehicle = Vehicle.builder().id(testUUID).brand("Toyota").model("Corolla").build();
        testVehicleEntity = VehicleEntity.builder().id(testId).brand("Toyota").model("Corolla").build();
    }

    @Test
    @DisplayName("exist() - Vérifie l'appel au repository")
    void exist_shouldReturnTrue_whenVehicleExists() {
        when(repository.existsById(testId)).thenReturn(true);

        boolean result = service.exist(testUUID);

        assertTrue(result);
        verify(repository).existsById(testId);
    }

    @Test
    @DisplayName("getAll() - Retourne tous les véhicules mappés")
    void getAll_shouldReturnAllVehicles() {
        when(repository.findAll()).thenReturn(List.of(testVehicleEntity));
        when(mapper.from(testVehicleEntity)).thenReturn(testVehicle);

        Collection<Vehicle> result = service.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(testVehicle));
        verify(repository).findAll();
    }

    @Test
    @DisplayName("get() - Retourne le véhicule mappé si trouvé")
    void get_shouldReturnVehicle_whenFound() {
        when(repository.findById(testId)).thenReturn(Optional.of(testVehicleEntity));
        when(mapper.from(testVehicleEntity)).thenReturn(testVehicle);

        Optional<Vehicle> result = service.get(testUUID);

        assertTrue(result.isPresent());
        assertEquals(testVehicle, result.get());
    }

    @Test
    @DisplayName("save() - Sauvegarde l'entité et retourne le véhicule")
    void save_shouldSaveAndReturnVehicle() {
        when(mapper.to(testVehicle)).thenReturn(testVehicleEntity);
        when(repository.save(testVehicleEntity)).thenReturn(testVehicleEntity);
        when(mapper.from(testVehicleEntity)).thenReturn(testVehicle);

        Vehicle result = service.save(testVehicle);

        assertNotNull(result);
        assertEquals(testVehicle, result);
        verify(mapper).to(testVehicle);
        verify(repository).save(testVehicleEntity);
        verify(mapper).from(testVehicleEntity);
    }

    @Test
    @DisplayName("delete() - doit appeler le delete du repository")
    void delete_shouldCallRepository() {
        service.delete(testUUID);
        verify(repository).deleteById(testId);
    }

    @Test
    @DisplayName("findByLicencePlate() - doit retouner le véhicule mappé si trouvé")
    void findByLicencePlate_shouldCallRepo() {
        String licencePlate = "AB-123-CD";
        when(repository.findByLicensePlate(licencePlate)).thenReturn(Optional.of(testVehicleEntity));
        when(mapper.from(testVehicleEntity)).thenReturn(testVehicle);

        Optional<Vehicle> result = service.getByLicensePlate(licencePlate);

        assertTrue(result.isPresent());
        assertEquals(testVehicle, result.get());
        verify(repository).findByLicensePlate(licencePlate);
        verify(mapper).from(testVehicleEntity);
    }
}
