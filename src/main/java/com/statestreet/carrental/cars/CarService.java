package com.statestreet.carrental.cars;

import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.util.ValidationException;

import java.util.List;
import java.util.function.Predicate;

public interface CarService {

    Car addCar(CarType type, String vinNumber) throws StorageException, ValidationException;
    void removeCar(String vinNumber) throws StorageException; // if a car becomes unavailable for any reason
    List<Car> getCars(Predicate<Car> filter);

}
