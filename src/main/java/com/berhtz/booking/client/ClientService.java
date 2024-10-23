package com.berhtz.booking.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    public Client saveClient(Client client) {
        String sql = "SELECT COUNT(*) " +
                "FROM clients " +
                "WHERE email = ?;";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, client.getEmail());
        if ((count != null && count > 0) == true) {
            throw new IllegalArgumentException("Client with this Email is already exist");
        }
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
