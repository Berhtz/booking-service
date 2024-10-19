package com.berhtz.booking.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client newClient) {
        Client client = clientRepository.findById(id).orElseThrow();
        client.setName(newClient.getName());
        client.setEmail(newClient.getEmail());
        client.setPhone(newClient.getPhone());
        return clientRepository.save(client);
    }
}
