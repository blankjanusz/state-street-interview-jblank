package com.statestreet.carrental.storage;

import com.statestreet.carrental.cars.Car;
import com.statestreet.carrental.cars.CarType;
import com.statestreet.carrental.clients.Client;
import com.statestreet.carrental.reservations.Reservation;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Predicate;

import static com.statestreet.carrental.cars.CarStatus.UNAVAILABLE;

public class StorageServiceImpl implements StorageService {

    private final static EnumMap<CarType, Integer> DEFAULT_CARS_LIMITS = new EnumMap<>(CarType.class);
    static {
        DEFAULT_CARS_LIMITS.put(CarType.SUV, 10);
        DEFAULT_CARS_LIMITS.put(CarType.SEDAN, 10);
        DEFAULT_CARS_LIMITS.put(CarType.VAN, 10);
    }

    private final EnumMap<CarType, Integer> carsLimits;

    private final Map<String, Car> vinToCar = new HashMap<>();
    private final StampedLock carsLock = new StampedLock();

    private final Map<String, Client> peselToClient = new HashMap<>();
    private final StampedLock clientsLock = new StampedLock();

    private final List<Reservation> reservations = new ArrayList<>();
    private final StampedLock reservationsLock = new StampedLock();

    public StorageServiceImpl() {
        this.carsLimits = DEFAULT_CARS_LIMITS;
    }

    public StorageServiceImpl(EnumMap<CarType, Integer> carsLimits) {
        this.carsLimits = carsLimits;
    }

    @Override
    public void addCar(Car car) throws StorageException {
        long stamp = carsLock.writeLock();
        try {
            if (vinToCar.containsKey(car.vinNumber())) {
                throw new StorageException("Car " + car + " already present in the storage");
            }
            Integer carsLimit = carsLimits.get(car.type());
            if (vinToCar.values().stream().filter(existingCar -> existingCar.type() == car.type()).count() == carsLimit) {
                throw new StorageException("Max number of " + carsLimit + " reached");
            }
            vinToCar.put(car.vinNumber(), car);
        } finally {
            carsLock.unlockWrite(stamp);
        }
    }

    @Override
    public void removeCar(String vinNumber) throws StorageException {
        long stamp = carsLock.writeLock();
        try {
            Car foundCar = vinToCar.get(vinNumber);
            if (foundCar == null) {
                throw new StorageException("Car with VIN " + vinNumber + " not present in the storage");
            }
            vinToCar.put(vinNumber, new Car(
                    foundCar.type(), foundCar.vinNumber(), UNAVAILABLE
            ));
        } finally {
            carsLock.unlockWrite(stamp);
        }
    }

    @Override
    public List<Car> getCars(Predicate<Car> filter) {
        long stamp = carsLock.tryOptimisticRead();
        List<Car> cars = vinToCar.values().stream().filter(filter).toList();

        if (!carsLock.validate(stamp)) {
            stamp = carsLock.readLock();
            try {
                return vinToCar.values().stream().filter(filter).toList();
            } finally {
                carsLock.unlock(stamp);
            }
        }
        return cars;
    }

    @Override
    public void addClient(Client client) throws StorageException {
        long stamp = clientsLock.writeLock();
        try {
            if (peselToClient.containsKey(client.peselNumber())) {
                throw new StorageException("Client with pesel number " + client.peselNumber() + " already present in the storage");
            }
            peselToClient.put(client.peselNumber(), client);
        } finally {
            clientsLock.unlockWrite(stamp);
        }
    }

    @Override
    public void removeClient(String peselNumber) throws StorageException {
        long clientStamp = clientsLock.writeLock();
        long reservationStamp = reservationsLock.writeLock();

        try {
            if (peselToClient.get(peselNumber) == null) {
                throw new StorageException("Client with pesel number " + peselNumber + " not present in the storage");
            }
            peselToClient.remove(peselNumber);

            List<Reservation> clientReservations = filterReservations(existingReservation ->
                    existingReservation.client().peselNumber().equals(peselNumber));
            clientReservations.forEach(reservations::remove);

        } finally {
            clientsLock.unlockWrite(clientStamp);
            reservationsLock.unlock(reservationStamp);
        }
    }

    @Override
    public List<Client> getClients() {
        long stamp = clientsLock.tryOptimisticRead();
        List<Client> clients = new ArrayList<>(peselToClient.values());

        if (!clientsLock.validate(stamp)) {
            stamp = clientsLock.readLock();
            try {
                return new ArrayList<>(peselToClient.values());
            } finally {
                clientsLock.unlock(stamp);
            }
        }
        return clients;
    }

    @Override
    public void addReservation(Reservation reservation) throws StorageException {
        long reservationStamp = reservationsLock.writeLock();
        long clientStamp = clientsLock.writeLock();
        long carStamp = carsLock.writeLock();

        try {
            if (reservations.contains(reservation)) {
                throw new StorageException("Reservation " + reservation + " already present in the storage");
            }
            if (!peselToClient.containsKey(reservation.client().peselNumber())) {
                throw new StorageException("Unknown client with pesel number " + reservation.client().peselNumber());
            }
            String carVin = reservation.car().vinNumber();
            Car car = vinToCar.get(carVin);
            if (car == null) {
                throw new StorageException("Unknown car with VIN " + carVin);
            }
            if (car.carStatus() == UNAVAILABLE) {
                throw new StorageException("Car with VIN " + carVin + " is unavailable");
            }
            reservations.add(reservation);
        } finally {
            reservationsLock.unlockWrite(reservationStamp);
            clientsLock.unlock(clientStamp);
            carsLock.unlock(carStamp);
        }
    }

    @Override
    public void removeReservation(Reservation reservation) throws StorageException {
        long stamp = reservationsLock.writeLock();
        try {
            if (!reservations.contains(reservation)) {
                throw new StorageException("Reservation " + reservation + " not present in the storage");
            }
            reservations.remove(reservation);
        } finally {
            reservationsLock.unlockWrite(stamp);
        }
    }

    @Override
    public List<Reservation> getReservations(Predicate<Reservation> filter) {
        long stamp = reservationsLock.tryOptimisticRead();
        List<Reservation> filteredReservations = filterReservations(filter);

        if (!reservationsLock.validate(stamp)) {
            stamp = reservationsLock.readLock();
            try {
                return filterReservations(filter);
            } finally {
                reservationsLock.unlock(stamp);
            }
        }
        return filteredReservations;
    }

    private List<Reservation> filterReservations(Predicate<Reservation> filter) {
        return (new ArrayList<>(reservations)).stream()
                .filter(filter).toList();
    }

}
