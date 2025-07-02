package com.microsservicos.triagem.service;

import com.microsservicos.triagem.dto.TriagemRequestDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.enums.StatusTriagem;
import com.microsservicos.triagem.model.Triagem;
import com.microsservicos.triagem.repository.TriagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TriagemService {

    @Autowired
    private TriagemRepository triagemRepository;

    public TriagemResponseDTO criarTriagem(TriagemRequestDTO dto) {
        Triagem triagem = new Triagem();
        triagem.setClienteId(dto.clienteId());
        triagem.setServicoId(dto.servicoId());
        triagem.setStatus(StatusTriagem.AGUARDANDO);
        triagem.setPrioridade(dto.prioridade());
        triagem.setHorarioSolicitacao(LocalDateTime.now());

        // Lógica para buscar estimativa de tempo com base nas triagens em fila
        Integer tempoEstimado = calcularTempoEstimadoFila();
        triagem.setTempoEstimadoMinutos(tempoEstimado);
        triagem.setHorarioEstimadoAtendimento(LocalDateTime.now().plusMinutes(tempoEstimado));

        // Snapshot dos dados externos (ex: nome do cliente e serviço)
        triagem.setNomeClienteSnapshot("Fulano da Silva"); // mockado, mas viria do CadastroCliente-Service
        triagem.setNomeServicoSnapshot("Cartão Roubado"); // viria do Catalogo-Service

        Triagem salva = triagemRepository.save(triagem);
        return toResponseDTO(salva);
    }

    public TriagemResponseDTO buscarProximaTriagem() {
        return triagemRepository.findFirstByStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem.AGUARDANDO)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RuntimeException("Nenhuma triagem na fila"));
    }

    private Integer calcularTempoEstimadoFila() {
        List<Triagem> emFila = triagemRepository.findByStatus(StatusTriagem.AGUARDANDO);
        return emFila.stream().mapToInt(Triagem::getTempoEstimadoMinutos).sum();
    }

    private TriagemResponseDTO toResponseDTO(Triagem triagem) {
        return new TriagemResponseDTO(
                triagem.getId(),
                triagem.getClienteId(),
                triagem.getServicoId(),
                triagem.getNomeClienteSnapshot(),
                triagem.getNomeServicoSnapshot(),
                triagem.getStatus(),
                triagem.getHorarioSolicitacao(),
                triagem.getHorarioEstimadoAtendimento(),
                triagem.getTempoEstimadoMinutos(),
                triagem.getPrioridade()
        );
    }
}
