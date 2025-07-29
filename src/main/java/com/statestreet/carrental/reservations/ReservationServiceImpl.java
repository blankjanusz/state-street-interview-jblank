package com.statestreet.carrental.reservations;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.util.LocalDateTimeUtil;
import com.statestreet.carrental.util.ValidationException;
import com.statestreet.carrental.util.Validators;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

public class ReservationServiceImpl implements ReservationService {

    private final StorageService storageService;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    public ReservationServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Reservation addReservation(Car car, Client client, LocalDateTime startDate, int numberOfDays)
            throws StorageException, ValidationException, ReservationException {

        Validators.validateReservation(car, client, startDate, numberOfDays);

        Reservation newReservation = new Reservation(
                car,
                client,
                startDate.withSecond(0).withNano(0),
                startDate.plusDays(numberOfDays));

        try {
            writeLock.lock();

            List<Reservation> collidingReservations = storageService.getReservations(existingReservation ->
                    existingReservation.client().equals(newReservation.client()) &&
                            existingReservation.car().vinNumber().equals(newReservation.car().vinNumber()) &&
                            LocalDateTimeUtil.periodsOverlap(
                                    existingReservation.startDateTime(), existingReservation.endDateTime(),
                                    newReservation.startDateTime(), newReservation.endDateTime()
                            ));

            if (!collidingReservations.isEmpty()) {
                throw new ReservationException("Colliding reservations " + collidingReservations);
            }
            storageService.addReservation(newReservation);
        } finally {
            writeLock.unlock();
        }
        return newReservation;
    }

    @Override
    public void removeReservation(Reservation reservation) throws StorageException {
        try {
            writeLock.lock();
            storageService.removeReservation(reservation);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Reservation> getReservations(Predicate<Reservation> filter) {
        try {
            readLock.lock();
            return storageService.getReservations(filter);
        } finally {
            readLock.unlock();
        }
    }
}
