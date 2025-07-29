package com.statestreet.carrental.storage;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.reservations.Reservation;

import java.util.List;
import java.util.function.Predicate;

public interface StorageService {

    void addCar(Car car) throws StorageException;
    void removeCar(String vinNumber) throws StorageException; // if a car becomes unavailable for any reason
    List<Car> getCars(Predicate<Car> filter);

    void addClient(Client client) throws StorageException;
    void removeClient(String peselNumber) throws StorageException; // if a client demands to be removed (GDPR, RODO)
    List<Client> getClients();

    void addReservation(Reservation reservation) throws StorageException;
    void removeReservation(Reservation reservation) throws StorageException; // if a client cancels a reservation
    List<Reservation> getReservations(Predicate<Reservation> filter);

}
