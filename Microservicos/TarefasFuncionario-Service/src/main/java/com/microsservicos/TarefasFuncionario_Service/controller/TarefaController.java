package com.microsservicos.TarefasFuncionario_Service.controller;

import com.microsservicos.TarefasFuncionario_Service.dto.TarefaRequest;
import com.microsservicos.TarefasFuncionario_Service.dto.TarefaResponse;
import com.microsservicos.TarefasFuncionario_Service.service.TarefaService;
import jakarta.xml.bind.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tarefa")
public class TarefaController {
    
    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TarefaResponse criartarefa(@RequestBody TarefaRequest TarefaRequest) throws ValidationException {
        return tarefaService.criarTarefa(TarefaRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TarefaResponse> listartarefaes(){
        return tarefaService.listarTarefas();
    }


    @GetMapping("/{id}") // Adicione este método para buscar tarefa por ID
    @ResponseStatus(HttpStatus.OK)
    public TarefaResponse buscarTarefaPorId(@PathVariable UUID id) {
        return tarefaService.buscarTarefaPorId(id);
    }


    @PutMapping("/{id}") // Recomendo PUT se o DTO sempre enviar todos os campos para atualização completa
    @ResponseStatus(HttpStatus.OK)
    public TarefaResponse atualizarTarefa(@PathVariable UUID id, @RequestBody TarefaRequest TarefaRequest){
        return tarefaService.atualizarTarefa(id, TarefaRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerTarefa(@PathVariable UUID id){
        tarefaService.removerTarefa(id);
    }
}
