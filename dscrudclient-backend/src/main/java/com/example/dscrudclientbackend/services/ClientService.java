package com.example.dscrudclientbackend.services;

import com.example.dscrudclientbackend.dto.ClientDTO;
import com.example.dscrudclientbackend.entities.Client;
import com.example.dscrudclientbackend.repositories.ClientRepository;
import com.example.dscrudclientbackend.services.exceptions.DatabaseException;
import com.example.dscrudclientbackend.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public Page<ClientDTO> findAllPaged(PageRequest pageRequest) {
        Page<Client> clientList = clientRepository.findAll(pageRequest);
        return clientList.map(ClientDTO::new);
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        Client clientEntity = optionalClient.orElseThrow(() -> new ResourceNotFoundException("Entity not found."));
        return new ClientDTO(clientEntity);
    }

    @Transactional
    public ClientDTO insert(ClientDTO clientDTO) {
        Client clientEntity = new Client();
        clientEntity.setName(clientDTO.getName());
        clientEntity.setCpf(clientDTO.getCpf());
        clientEntity.setIncome(clientDTO.getIncome());
        clientEntity.setBirthDate(clientDTO.getBirthDate());
        clientEntity.setChildren(clientDTO.getChildren());
        clientEntity = clientRepository.save(clientEntity);
        return new ClientDTO(clientEntity);
    }

    @Transactional
    public ClientDTO update(Long id, ClientDTO clientDTO) {
        try {
            Client clientEntity = clientRepository.getReferenceById(id);
            clientEntity.setName(clientDTO.getName());
            clientEntity.setCpf(clientDTO.getCpf());
            clientEntity.setIncome(clientDTO.getIncome());
            clientEntity.setBirthDate(clientDTO.getBirthDate());
            clientEntity.setChildren(clientDTO.getChildren());
            clientEntity = clientRepository.save(clientEntity);
            return new ClientDTO(clientEntity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    public void delete(Long id) {
        try {
            clientRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity Violation.");
        }
    }
}