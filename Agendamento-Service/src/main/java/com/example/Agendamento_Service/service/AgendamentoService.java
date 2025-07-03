package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    public AgendamentoService(AgendamentoRepository repository) {
        this.repository = repository;
    }

    public List<Agendamento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Agendamento> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public Agendamento salvar(Agendamento agendamento) {
        return repository.save(agendamento);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}