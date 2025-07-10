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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class SetorService {

    private static final Logger log = LoggerFactory.getLogger(SetorService.class);

    private final SetorRepository setorRepository;

    @Autowired
    public SetorService(SetorRepository setorRepository) {
        this.setorRepository = setorRepository;
    }

    public SetorResponse criarSetor(SetorRequest setorRequest) {

        // Validação inicial: nome não pode ser nulo ou vazio
        if (setorRequest.nome() == null || setorRequest.nome().isBlank()) {
            throw new ValidacaoException("O nome do setor não pode ser nulo ou vazio.");
        }

        // Verifica se já existe um setor com o nome antes de criar
        if (setorRepository.findByNome(setorRequest.nome()).isPresent()) {
            throw new RecursoDuplicadoException("Já existe um setor com o nome: " + setorRequest.nome());
        }

        Setor setor = new Setor();
        setor.setNome(setorRequest.nome());
        setor.setId(UUID.randomUUID()); // Mantido, assuming you want to generate UUIDs in the service
        setor.setDescricao(setorRequest.descricao());
        setor.setAtivo(setorRequest.isAtivo());
        setor.setPrioridade(setorRequest.prioridade());
        setor.setTempoMedioMinutos(setorRequest.tempoMedioMinutos());
        // NEW: Set the list of required document IDs
        setor.setDocumentosObrigatoriosIds(setorRequest.documentosObrigatoriosIds()); // Use the new DTO field
        setor.setIcone(setorRequest.icone());

        setorRepository.save(setor);
        log.info("Setor '{}' (ID: {}) criado com sucesso!", setor.getNome(), setor.getId());

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
                .orElseThrow(() -> new SetorNaoEncontradoException(id));
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

        // Considerar adicionar uma verificação de nome duplicado aqui também,
        // mas permitindo que o setor atual mantenha seu nome.
        // if (!setorExistente.getNome().equals(setorRequest.nome()) && setorRepository.findByNome(setorRequest.nome()).isPresent()) {
        //     throw new RecursoDuplicadoException("Já existe outro setor com o nome: " + setorRequest.nome());
        // }

        setorExistente.setNome(setorRequest.nome());
        setorExistente.setDescricao(setorRequest.descricao());
        setorExistente.setAtivo(setorRequest.isAtivo());
        setorExistente.setPrioridade(setorRequest.prioridade());
        setorExistente.setTempoMedioMinutos(setorRequest.tempoMedioMinutos());
        // NEW: Update the list of required document IDs
        setorExistente.setDocumentosObrigatoriosIds(setorRequest.documentosObrigatoriosIds()); // Use the new DTO field

        Setor setorAtualizado = setorRepository.save(setorExistente);
        log.info("Setor '{}' (ID: {}) atualizado com sucesso!", setorAtualizado.getNome(), setorAtualizado.getId());
        return mapToSetorResponse(setorAtualizado);
    }

    public void removerSetor(UUID id) {
        if (!setorRepository.existsById(id)) {
            throw new SetorNaoEncontradoException(id);
        }
        setorRepository.deleteById(id);
        log.info("Setor com ID {} removido com sucesso.", id);
    }

    private SetorResponse mapToSetorResponse(Setor setor) {
        return new SetorResponse(
                setor.getId(),
                setor.getNome(),
                setor.getDescricao(),
                setor.isAtivo(),
                setor.getPrioridade(),
                setor.getTempoMedioMinutos(),
                setor.getDocumentosObrigatoriosIds(),// NEW: Include the list of document IDs
                setor.getIcone()
        );
    }

    public SetorResponse alterarStatus(UUID id, boolean novoStatus) {
        Setor setorExistente = setorRepository.findById(id)
                .orElseThrow(() -> new SetorNaoEncontradoException(id));

        setorExistente.setAtivo(novoStatus);

        Setor setorSalvo = setorRepository.save(setorExistente);
        log.info("Status do setor '{}' (ID: {}) alterado para {}.", setorSalvo.getNome(), setorSalvo.getId(), novoStatus ? "ATIVO" : "INATIVO");
        return mapToSetorResponse(setorSalvo);
    }
}