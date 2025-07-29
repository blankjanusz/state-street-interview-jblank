package com.statestreet.carrental.cars;

import com.statestreet.carrental.AbstractStorageTest;
import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.storage.StorageServiceImpl;
import com.statestreet.carrental.util.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarServiceImplTest extends AbstractStorageTest {

    private final StorageService storageService = new StorageServiceImpl(2);
    private final CarService carService = new CarServiceImpl(storageService);

    @Test
    public void crudTest() {

        final Car[] cars = new Car[2];
        assertDoesNotThrow(() -> cars[0] = carService.addCar(CarType.SUV, VIN));

        assertThrows(ValidationException.class, () -> carService.addCar(CarType.SUV, "invalidVIN"));
        assertThrows(StorageException.class, () -> carService.addCar(CarType.SEDAN, VIN));

        assertDoesNotThrow(() -> cars[1] = carService.addCar(CarType.SEDAN, VIN2));
        assertThrows(StorageException.class, () -> carService.addCar(CarType.VAN, "JBLVA2AE4EH877003"));

        assertEquals(1, carService.getCars(existingCar -> existingCar.type() == CarType.SUV).size());
        assertEquals(0, carService.getCars(existingCar -> existingCar.type() == CarType.VAN).size());

        assertDoesNotThrow(() -> carService.removeCar(cars[0].vinNumber()));
        assertThrows(StorageException.class, () -> carService.removeCar("JBLVA2AE4EH877003"));

        List<Car> unavailableCars = carService.getCars(existingCar -> existingCar.carStatus() == CarStatus.UNAVAILABLE);
        assertEquals(1, unavailableCars.size());
        assertEquals(unavailableCars.getFirst().vinNumber(), cars[0].vinNumber());

    }

}
