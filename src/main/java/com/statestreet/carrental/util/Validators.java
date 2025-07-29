package com.statestreet.carrental.util;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.clients.Client;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Validators {

    private static final Pattern pattern = Pattern.compile("[(A-H|J-N|P|R-Z|0-9)]{17}");

    public static void validateReservation(Car car, Client client, LocalDateTime startDate, int numberOfDays) throws ValidationException {
        if (car == null) {
            throw new ValidationException("Null car");
        }
        if (client == null) {
            throw new ValidationException("Null client");
        }
        if (startDate == null || startDate.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Invalid start date and time");
        }
        if (numberOfDays <= 0) {
            throw new ValidationException("Invalid number of days");
        }
    }

    public static void validateClient(String fistName, String lastName, String peselNumber) throws ValidationException {
        if (!isValidName(fistName)) {
            throw new ValidationException("Invalid first name " + fistName);
        }
        if (!isValidName(lastName)) {
            throw new ValidationException("Invalid last name " + lastName);
        }
        if (!isValidPesel(peselNumber)) {
            throw new ValidationException("Invalid pesel " + peselNumber);
        }
    }

    private static boolean isValidName(String name) {
        return name != null && !name.isBlank();
    }

    public static void validateVin(String vinString) throws ValidationException {
        if (vinString == null || !pattern.matcher(vinString).matches()) {
            throw new ValidationException("Invalid VIN " + vinString);
        }
    }

    private static boolean isValidPesel(String peselString) {
        // add pesel validation here
        return true;
    }
}



