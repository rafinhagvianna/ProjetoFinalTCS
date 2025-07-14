package com.microsservicos.TarefasFuncionario_Service.service;

import com.microsservicos.TarefasFuncionario_Service.dto.TarefaRequest;
import com.microsservicos.TarefasFuncionario_Service.dto.TarefaResponse;
import com.microsservicos.TarefasFuncionario_Service.exceptions.RecursoDuplicadoException;
import com.microsservicos.TarefasFuncionario_Service.exceptions.TarefaNaoEncontradaException;
import com.microsservicos.TarefasFuncionario_Service.model.Tarefa;
import com.microsservicos.TarefasFuncionario_Service.repository.TarefaRepository;
import jakarta.xml.bind.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    @Autowired
    public TarefaService(TarefaRepository tarefaRepository){this.tarefaRepository = tarefaRepository;}

    public TarefaResponse criarTarefa(TarefaRequest tarefaRequest) throws ValidationException {
        if(tarefaRequest.nome() == null || tarefaRequest.nome().isBlank()){
            throw new ValidationException("O nome da Tarefa não pode ser vazio ou nulo");
        }

        if (tarefaRepository.findByNome(tarefaRequest.nome()).isPresent()) {
            throw new RecursoDuplicadoException("Já existe uma tarefa com esse nome: " + tarefaRequest.nome());
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setId(UUID.randomUUID());
        tarefa.setNome(tarefaRequest.nome());
        tarefa.setDescricao(tarefaRequest.descricao());

        tarefaRepository.save(tarefa);

        return mapToTarefaResponse(tarefa);
    }

    public List<TarefaResponse> listarTarefas() {
        return tarefaRepository.findAll()
                .stream()
                .map(this::mapToTarefaResponse)
                .toList();
    }

    public TarefaResponse buscarTarefaPorId(UUID id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));
        return mapToTarefaResponse(tarefa);
    }

    public void removerTarefa(UUID id) {
        if (!tarefaRepository.existsById(id)) {
            throw new TarefaNaoEncontradaException(id);
        }
        tarefaRepository.deleteById(id);
    }

    public TarefaResponse atualizarTarefa(UUID id, TarefaRequest tarefaRequest) {
        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new TarefaNaoEncontradaException(id));

        tarefaExistente.setNome(tarefaRequest.nome());
        tarefaExistente.setDescricao(tarefaRequest.descricao());

        Tarefa tarefaAtualizada = tarefaRepository.save(tarefaExistente);

        return mapToTarefaResponse(tarefaAtualizada);
    }

    private TarefaResponse mapToTarefaResponse(Tarefa tarefa) {
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getDescricao()
        );
    }
}
