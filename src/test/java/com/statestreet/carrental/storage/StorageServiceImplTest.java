package com.statestreet.carrental.storage;

import com.statestreet.carrental.AbstractStorageTest;
import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.cars.CarStatus;
import com.statestreet.carrental.reservations.Reservation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class StorageServiceImplTest extends AbstractStorageTest {

    @Test
    public void addCarTest() {
        StorageService storageService = new StorageServiceImpl();

        assertDoesNotThrow(() -> storageService.addCar(car));
        assertThrows(StorageException.class, () -> storageService.addCar(car));
    }

    @Test
    public void removeCarTest() {
        StorageService storageService = new StorageServiceImpl();

        assertThrows(StorageException.class, () -> storageService.removeCar(car.vinNumber())); // no such car
        assertDoesNotThrow(() -> storageService.addCar(car));
        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber())); // marks as unavailable
        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber())); // marks as unavailable, no exception
    }

    @Test
    public void getCarsTest() {
        StorageService storageService = new StorageServiceImpl();

        assertDoesNotThrow(() -> storageService.addCar(car));
        assertDoesNotThrow(() -> storageService.addCar(car2));

        List<Car> cars = storageService.getCars(car -> car.carStatus() == CarStatus.AVAILABLE);
        assertEquals(2, cars.size());
        assertTrue(cars.contains(car));
        assertTrue(cars.contains(car2));

        assertDoesNotThrow(() -> storageService.removeCar(car.vinNumber()));
        cars = storageService.getCars(car -> car.carStatus() == CarStatus.AVAILABLE);
        assertEquals(1, cars.size());
        assertEquals(VIN2, cars.getFirst().vinNumber());

        cars = storageService.getCars(car -> car.carStatus() == CarStatus.UNAVAILABLE);
        assertEquals(1, cars.size());
        assertEquals(VIN, cars.getFirst().vinNumber());
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

    @Test
    public void reservationsCRUDTest() {
        StorageService storageService = new StorageServiceImpl();
        assertDoesNotThrow(() -> storageService.addClient(client));
        assertDoesNotThrow(() -> storageService.addClient(client2));
        assertDoesNotThrow(() -> storageService.addCar(car));
        assertDoesNotThrow(() -> storageService.addCar(car2));


        Reservation reservation = new Reservation(car, client, date, date.plusDays(1));
        Reservation reservation2 = new Reservation(car2, client2, date2, date2.plusDays(2));

        assertDoesNotThrow(() -> storageService.addReservation(reservation));
        assertThrows(StorageException.class, () -> storageService.addReservation(reservation));

        assertDoesNotThrow(() -> storageService.addReservation(reservation2));
        assertEquals(1,
        storageService.getReservations(res -> res.client().peselNumber().equals(PESEL)).size());

        assertDoesNotThrow(() -> storageService.removeReservation(reservation));
        assertThrows(StorageException.class, () -> storageService.removeReservation(reservation));
    }

    @Test
    public void threadSafetyTest() throws InterruptedException {

        StorageService storageService = new StorageServiceImpl();
        final AtomicInteger numberOfExceptions = new AtomicInteger();
        int threads = 10; // Runtime.getRuntime().availableProcessors();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                try {
                    startLatch.await();
                    storageService.addCar(car);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (StorageException e) {
                    numberOfExceptions.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
            t.start();
        }
        startLatch.countDown();
        endLatch.await();

        assertEquals(9, numberOfExceptions.get());
        assertEquals(1, storageService.getCars(car -> true).size());
    }

}
