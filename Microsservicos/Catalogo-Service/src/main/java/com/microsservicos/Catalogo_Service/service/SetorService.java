package com.microsservicos.Catalogo_Service.service;

import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.exceptions.RecursoDuplicadoException;
import com.microsservicos.Catalogo_Service.exceptions.SetorNaoEncontradoException;
import com.microsservicos.Catalogo_Service.exceptions.ValidacaoException;
import com.microsservicos.Catalogo_Service.model.Setor;
import com.microsservicos.Catalogo_Service.repository.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.rmi.server.LogStream.log;

@Service
public class SetorService {

    private final SetorRepository setorRepository;

    @Autowired
    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public SetorResponse criarSetor(SetorRequest setorRequest) {

        if (setorRepository.findByNome(setorRequest.nome()).isPresent()) {
            throw new RecursoDuplicadoException("Já existe um setor com o nome: " + setorRequest.nome());
        }

        if (setorRequest.nome() == null || setorRequest.nome().isBlank()) {
            throw new ValidacaoException("O nome do setor não pode ser nulo ou vazio.");
        }

        if (setorRepository.findByNome(setorRequest.nome()).isPresent()) {
            throw new RuntimeException("Já existe um setor com o nome: " + setorRequest.nome());
        }

        Setor setor = new Setor();
        setor.setNome(setorRequest.nome());
        setor.setId(UUID.randomUUID());
        setor.setDescricao(setorRequest.descricao());
        setor.setAtivo(setorRequest.isAtivo());
        setor.setPrioridade(setorRequest.prioridade());
        setor.setTempoMedioMinutos(setorRequest.tempoMedioMinutos());

        setorRepository.save(setor);
        log("Setor criado com sucesso!");

        return mapToSetorResponse(setor);
    }

    public List<SetorResponse> listarSetores() {
        return setorRepository.findAll()
                .stream()
                .map(this::mapToSetorResponse)
                .toList();
    }

    public List<SetorResponse> buscarSetorAtivo() {
        List<Setor> setoresAtivos = setorRepository.findByIsAtivo(true);

        return setoresAtivos.stream()
                // Usa o método helper aqui também
                .map(this::mapToSetorResponse)
                .toList();
    }

    public List<SetorResponse> buscarSetorInativo() {
        List<Setor> setoresInativos = setorRepository.findByIsAtivo(false);

        return setoresInativos.stream()

                .map(this::mapToSetorResponse)
                .toList();
    }

    public SetorResponse buscarSetorPorId(UUID id) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setor com ID '" + id + "' não encontrado."));

        return mapToSetorResponse(setor);
    }

    public boolean retornarStatusSetor(String nomeSetor) {

        Optional<Setor> setor = setorRepository.findByNome(nomeSetor);

        return setor
                .map(Setor::isAtivo)
                .orElse(false);
    }

    public SetorResponse atualizarSetor(UUID id, SetorRequest setorRequest) {

        Setor setorExistente = setorRepository.findById(id)
                .orElseThrow(() -> new SetorNaoEncontradoException(id));


        setorExistente.setNome(setorRequest.nome());
        setorExistente.setDescricao(setorRequest.descricao());
        setorExistente.setAtivo(setorRequest.isAtivo());
        setorExistente.setPrioridade(setorRequest.prioridade());
        setorExistente.setTempoMedioMinutos(setorRequest.tempoMedioMinutos());

        setorRepository.save(setorExistente);
        return mapToSetorResponse(setorExistente);
    }

    public void removerSetor(UUID id){
        if((!setorRepository.existsById(id))){
            throw new RuntimeException("Setor com ID '" + id + "' não encontrado para remoção.");
        }
        setorRepository.deleteById(id);
    }

    private SetorResponse mapToSetorResponse(Setor setor) {
        return new SetorResponse(
                setor.getId(),
                setor.getNome(),
                setor.getDescricao(),
                setor.isAtivo(),
                setor.getPrioridade(),
                setor.getTempoMedioMinutos()
        );
    }

    public SetorResponse alterarStatus(UUID id, boolean novoStatus) {

        Setor setorExistente = setorRepository.findById(id)
                .orElseThrow(() -> new SetorNaoEncontradoException(id));


        setorExistente.setAtivo(novoStatus);

        Setor setorSalvo = setorRepository.save(setorExistente);


        return mapToSetorResponse(setorSalvo);
    }
}
