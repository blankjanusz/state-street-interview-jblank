package com.statestreet.carrental.clients;

import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.util.ValidationException;

import java.util.List;

public interface ClientService {

    void addClient(String fistName, String lastName, String peselNumber) throws StorageException, ValidationException;
    void removeClient(String peselNumber) throws StorageException;
    List<Client> getClients();

}
