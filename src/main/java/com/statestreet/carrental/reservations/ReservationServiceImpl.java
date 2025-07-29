package com.statestreet.carrental.reservations;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.util.ValidationException;
import com.statestreet.carrental.util.Validators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class ReservationServiceImpl implements ReservationService {

    private final StorageService storageService;

    public ReservationServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void addReservation(Car car, Client client, LocalDateTime startDate, int numberOfDays) throws StorageException, ValidationException {
        Validators.validateReservation(car, client, startDate, numberOfDays);
        storageService.addReservation(new Reservation(
                car,
                client,
                startDate.withSecond(0).withNano(0),
                startDate.plusDays(numberOfDays))
        );
    }

    @Override
    public void removeReservation(Reservation reservation) throws StorageException {
        storageService.removeReservation(reservation);
    }

    @Override
    public List<Reservation> getReservations(Predicate<Reservation> filter) {
        storageService.getReservations(filter)
    }
}
