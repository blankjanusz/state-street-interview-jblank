package com.statestreet.carrental.storage;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.cars.CarStatus;
import com.statestreet.carrental.cars.CarType;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.util.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StorageServiceImplTest {

    private static final String VIN = "JBLVA2AE4EH877001";
    private static final String VIN2 = "JBLVA2AE4EH877002";
    private static final String VIN3 = "JBLVA2AE4EH877003";

    private static final Car car;
    private static final Car car2;

    private static final Client client;

    static {
            car = new Car(CarType.SUV, VIN);
            car2 = new Car(CarType.SUV, VIN2);

            client = new Client("Janusz", "Blank", "82031200000");

    }

    @Test
    public void addCarTest() throws ValidationException {
        StorageService storageService = new StorageServiceImpl();

        assertDoesNotThrow(() -> storageService.addCar(car));
        assertThrows(StorageException.class, () -> storageService.addCar(car));
    }

    @Test
    public void removeCarTest() throws ValidationException {
        StorageService storageService = new StorageServiceImpl();

        assertThrows(StorageException.class, () -> storageService.removeCar(car.vinNumber())); // no such car
        assertDoesNotThrow(() -> storageService.addCar(car));
        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber())); // marks as unavailable
        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber())); // marks as unavailable, no exception
    }

    @Test
    public void getCarsTest() throws ValidationException {
        StorageService storageService = new StorageServiceImpl();

        assertDoesNotThrow(() -> storageService.addCar(car));
        assertDoesNotThrow(() -> storageService.addCar(car2));

        List<Car> cars = storageService.getCars(carStatus -> carStatus == CarStatus.AVAILABLE);
        assertEquals(2, cars.size());
        assertTrue(cars.contains(car));
        assertTrue(cars.contains(car2));

        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber()));
        cars = storageService.getCars(carStatus -> carStatus == CarStatus.AVAILABLE);
        assertEquals(1, cars.size());
        assertFalse(cars.contains(car));
        assertTrue(cars.contains(car2));
    }

    @Test
    public void clientCRUDTest() {
        StorageService storageService = new StorageServiceImpl();
        assertDoesNotThrow(() -> storageService.addClient(client));
        assertThrows(StorageException.class, () -> storageService.addClient(client));
        assertDoesNotThrow(() -> storageService.removeClient(client.peselNumber()));
        assertThrows(StorageException.class, () -> storageService.removeClient(client.peselNumber()));
        assertEquals(0, storageService.getClients().size());
    }



}
