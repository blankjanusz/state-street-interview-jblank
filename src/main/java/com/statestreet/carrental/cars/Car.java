package com.statestreet.carrental.cars;

public record Car(CarType type, String vinNumber, CarStatus carStatus) {

    public Car(CarType type, String vinNumber) {
        this(type, vinNumber, CarStatus.AVAILABLE);
    }
}