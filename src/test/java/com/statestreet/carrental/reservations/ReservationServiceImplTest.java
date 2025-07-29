package com.statestreet.carrental.reservations;

import com.statestreet.carrental.AbstractStorageTest;
import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.storage.StorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest extends AbstractStorageTest {

    private final StorageService storageService = new StorageServiceImpl();
    private final ReservationService reservationService = new ReservationServiceImpl(storageService);

    @BeforeEach
    public void init() throws StorageException {
        storageService.addClient(client);
        storageService.addClient(client2);

        storageService.addCar(car);
        storageService.addCar(car2);
    }

    @Test
    public void addReservationTest() {
        assertDoesNotThrow(() -> reservationService.addReservation(car, client, date, 5));
        assertThrows(ReservationException.class, () -> reservationService.addReservation(car, client, date.minusDays(1), 3));
    }

    @Test
    public void removeReservationTest() {
        final Reservation[] reservation = new Reservation[1];
        assertDoesNotThrow(() -> reservation[0] = reservationService.addReservation(car, client, date, 5));

        assertDoesNotThrow(() -> reservationService.removeReservation(reservation[0]));
        assertThrows(StorageException.class, () -> reservationService.removeReservation(reservation[0]));
    }

    @Test
    public void getReservationsTest() {
        final Reservation[] reservations = new Reservation[2];
        assertDoesNotThrow(() -> reservations[0] = reservationService.addReservation(car, client, date, 5));
        assertDoesNotThrow(() -> reservations[1] = reservationService.addReservation(car2, client2, date2, 5));

        assertEquals(reservations[0],
                reservationService.getReservations(existingReservation -> existingReservation.client() == client).getFirst());

        assertEquals(reservations[1],
                reservationService.getReservations(existingReservation -> existingReservation.startDateTime().isAfter(
                LocalDateTime.of(2025, 8, 2, 0, 0, 0))).getFirst()
        );
    }

}
