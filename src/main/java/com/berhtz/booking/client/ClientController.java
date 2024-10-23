package com.berhtz.booking.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/v0/pool/client/")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/all")
    List<Client> getClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/get/{id}")
    Client getClient(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Client> addClient(@Valid @RequestBody Client client) {

        Client savedClient = clientService.saveClient(client);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    /**
     * Обновление данных о клиенте
     * 
     * @return 
     */
    @PutMapping("/update")
    Client updateClient(@RequestBody Client client) {

        return clientService.updateClient(client.getId(), client);
    }
}
