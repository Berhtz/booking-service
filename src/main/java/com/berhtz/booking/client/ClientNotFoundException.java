package com.berhtz.booking.client;

public class ClientNotFoundException extends RuntimeException {
    ClientNotFoundException(Long id) {
        super("Client not found " + id);
    }
}
