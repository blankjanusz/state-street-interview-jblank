package com.statestreet.carrental;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.cars.CarType;
import com.statestreet.carrental.clients.Client;

import java.time.LocalDateTime;

public class AbstractStorageTest {

    protected static final LocalDateTime date = LocalDateTime.of(2025, 8, 1, 12, 0, 0);
    protected static final LocalDateTime date2 = LocalDateTime.of(2025, 8, 2, 12, 0, 0);

    protected static final String VIN = "JBLVA2AE4EH877001";
    protected static final String VIN2 = "JBLVA2AE4EH877002";
    protected static final String PESEL = "82031200001";
    protected static final String PESEL2 = "82031200002";

    protected static final Car car = new Car(CarType.SUV, VIN);
    protected static final Car car2 = new Car(CarType.SUV, VIN2);;

    protected static final Client client = new Client("Janusz", "Blank", PESEL);
    protected static final Client client2 = new Client("Grzegorz", "Blank", PESEL2);

}
