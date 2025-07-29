package com.statestreet.carrental.clients;

import com.statestreet.carrental.storage.StorageException;
import com.statestreet.carrental.storage.StorageService;
import com.statestreet.carrental.util.ValidationException;
import com.statestreet.carrental.util.Validators;

import java.util.List;

public class ClientServiceImpl implements ClientService {

    private final StorageService storageService;

    public ClientServiceImpl(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public void addClient(String fistName, String lastName, String peselNumber) throws StorageException, ValidationException {
        Validators.validateClient(fistName, lastName, peselNumber);
        storageService.addClient(new Client(fistName, lastName, peselNumber));
    }

    @Override
    public void removeClient(String peselNumber) throws StorageException {
        storageService.removeClient(peselNumber);
    }

    @Override
    public List<Client> getClients() {
        return storageService.getClients();
    }
}
