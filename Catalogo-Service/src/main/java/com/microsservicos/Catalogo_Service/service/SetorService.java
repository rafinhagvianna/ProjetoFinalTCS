package com.microsservicos.Catalogo_Service.service;

import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.model.Setor;
import com.microsservicos.Catalogo_Service.repository.SetorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        System.out.println("Setor criado com sucesso!");

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
}
