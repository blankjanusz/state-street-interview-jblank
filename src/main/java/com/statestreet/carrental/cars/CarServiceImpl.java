package com.statestreet.carrental.cars;

import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.util.ValidationException;
import com.statestreet.carrental.util.Validators;

import java.util.List;
import java.util.function.Predicate;

public class CarServiceImpl implements CarService {

    private final StorageService storageService;

    public CarServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Car addCar(CarType type, String vinNumber) throws StorageException, ValidationException {
        Validators.validateVin(vinNumber);
        Car newCar = new Car(type, vinNumber);
        storageService.addCar(newCar);
        return newCar;
    }

    @Override
    public void removeCar(String vinNumber) throws StorageException {
        storageService.removeCar(vinNumber);
    }

    @Override
    public List<Car> getCars(Predicate<Car> filter) {
        return storageService.getCars(filter);
    }
}
