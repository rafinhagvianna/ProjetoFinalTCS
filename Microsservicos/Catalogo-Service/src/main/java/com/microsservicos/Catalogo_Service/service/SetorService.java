package com.microsservicos.Catalogo_Service.service;

import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.model.Setor;
import com.microsservicos.Catalogo_Service.repository.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.rmi.server.LogStream.log;

@Service
public class SetorService {

    private final SetorRepository setorRepository;

    @Autowired
    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public SetorResponse criarSetor(SetorRequest setorRequest) {
        Setor setor = new Setor();
        setor.setNome(setorRequest.nome());
        setor.setDescricao(setorRequest.descricao());
        setor.setAtivo(setorRequest.isAtivo());
        setor.setPrioridade(setorRequest.prioridade());

        setorRepository.save(setor);
        log("Setor criado com sucesso!");

        return new SetorResponse(setor.getId(), setor.getNome(), setor.getDescricao(), setor.isAtivo(), setor.getPrioridade());
    }

    public List<SetorResponse> listarSetores() {
        return setorRepository.findAll()
                .stream()
                .map(setor -> new SetorResponse(
                        setor.getId(),
                        setor.getNome(),
                        setor.getDescricao(),
                        setor.isAtivo(),
                        setor.getPrioridade()))
                .toList();
    }

    public List<SetorResponse> buscarSetorAtivo() {
        List<Setor> setoresAtivos = setorRepository.findByIsAtivo(true);

        return setoresAtivos.stream()
                .map(setor -> new SetorResponse(
                        setor.getId(),
                        setor.getNome(),
                        setor.getDescricao(),
                        setor.isAtivo(),
                        setor.getPrioridade()))
                .toList();

    }

    public List<SetorResponse> buscarSetorInativo() {
        List<Setor> setoresInativos = setorRepository.findByIsAtivo(false);

        return setoresInativos.stream()
                .map(setor -> new SetorResponse(
                        setor.getId(),
                        setor.getNome(),
                        setor.getDescricao(),
                        setor.isAtivo(),
                        setor.getPrioridade()))
                .toList();
    }

    public boolean retornarStatusSetor(String nomeSetor) {

        Optional<Setor> setor = setorRepository.findByNome(nomeSetor);

        return setor
                .map(Setor::isAtivo)
                .orElse(false);
    }

    public SetorResponse atualizarSetor(String id, SetorRequest setorRequest) {
        Setor setorExistente = setorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setor com ID '" + id + "' não encontrado."));


        setorExistente.setNome(setorRequest.nome());
        setorExistente.setDescricao(setorRequest.descricao());
        setorExistente.setAtivo(setorRequest.isAtivo());
        setorExistente.setPrioridade(setorRequest.prioridade());

        setorRepository.save(setorExistente);

        return new SetorResponse(
                setorExistente.getId(),
                setorExistente.getNome(),
                setorExistente.getDescricao(),
                setorExistente.isAtivo(),
                setorExistente.getPrioridade()
        );
    }

    public void removerSetor(String id){
        if((!setorRepository.existsById(id))){
            throw new RuntimeException("Setor com ID '" + id + "' não encontrado para remoção.");
        }
        setorRepository.deleteById(id);
    }
}
