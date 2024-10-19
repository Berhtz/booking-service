package com.berhtz.booking;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.berhtz.booking.client.Client;
import com.berhtz.booking.client.ClientController;
import com.berhtz.booking.client.ClientService;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(ClientController.class)
public class ClientControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Test
    public void testGetAllClients() throws Exception {
        Mockito.when(clientService.getAllClients()).thenReturn(
            Arrays.asList(new Client(null, "Jhon Doe", "john@example.com", "123456789", null))
        );

        mockMvc.perform(get("/api/v0/pool/client/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Jhon Doe"));
    }

    @Test
    public void testGetClientById() throws Exception {
        Long clientId = 1L;
        Mockito.when(clientService.getClientById(clientId)).thenReturn(
            new Client(clientId, "Jane Doe", "987654321", "jane@example.com", null)
        );

        mockMvc.perform(get("/api/v0/pool/client/get/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    public void testAddClient() throws Exception {
      
        Client savedClient = new Client(1L, "Alice", "alice@example.com", "1122334455", null);

        Mockito.when(clientService.saveClient(Mockito.any(Client.class))).thenReturn(savedClient);

        mockMvc.perform(post("/api/v0/pool/client/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\",\"email\":\"alice@example.com\",\"phone\":\"1122334455\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    public void testUpdateClient() throws Exception {
        Long clientId = 1L;
        Client updatedClient = new Client(clientId, "Bob Updated", "bob.updated@example.com", "999888777", null);

        Mockito.when(clientService.updateClient(Mockito.eq(clientId), Mockito.any(Client.class))).thenReturn(updatedClient);

        mockMvc.perform(put("/api/v0/pool/client/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Bob Updated\",\"email\":\"bob.updated@example.com\",\"phone\":\"999888777\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bob Updated"));
    }
}
