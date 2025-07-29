package com.statestreet.carrental.reservations;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.util.ValidationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public interface ReservationService {

    void addReservation(Car car, Client client, LocalDateTime startDate, int numberOfDays) throws StorageException, ValidationException;
    void removeReservation(Reservation reservation) throws StorageException;
    List<Reservation> getReservations(Predicate<Reservation> filter);

}
