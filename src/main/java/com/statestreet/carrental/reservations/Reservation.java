package com.statestreet.carrental.reservations;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;

import java.time.LocalDateTime;

public record Reservation(Car car, Client client, LocalDateTime startDateTime, LocalDateTime endDateTime) {
}
