package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.dto.ServicoResponseDTO;
import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.repository.AgendamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final WebClient webClientServicos;

    public AgendamentoService(AgendamentoRepository repository, WebClient webClientServicos) {
        this.repository = repository;
        this.webClientServicos = webClientServicos;
    }

    public List<Agendamento> listarTodos() {
        return repository.findAll();
    }

    public Optional<Agendamento> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public Agendamento salvar(Agendamento agendamento) {
        if (!isDataEHoraDeTrabalhoValido(agendamento.getDataHora())) {
            throw new IllegalArgumentException("A data e hora do agendamento devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
        }

        Integer tempoEstimadoServico = getTempoEstimadoServico(agendamento.getServicoId());
        if (tempoEstimadoServico == null) {
            throw new IllegalArgumentException("Não foi possível obter o tempo estimado para o serviço com ID: " + agendamento.getServicoId());
        }

        if (!isHorarioDisponivel(agendamento.getDataHora(), tempoEstimadoServico)) {
            throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço.");
        }

        return repository.save(agendamento);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }

    public Integer getTempoEstimadoServico(UUID servicoId) {
//        comentado para teste
//        return webClientServicos.get()
//                .uri("/{id}", servicoId)
//                .retrieve()
//                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
//                        clientResponse -> Mono.error(new IllegalArgumentException("Serviço não encontrado com o ID: " + servicoId)))
//                .bodyToMono(ServicoResponseDTO.class)
//                .map(ServicoResponseDTO::getTempoEstimadoEmMinutos)
//                .block();
        return 30;
    }

    private boolean isDataEHoraDeTrabalhoValido(LocalDateTime dataHora) {
        DayOfWeek diaDeSemana = dataHora.getDayOfWeek();
        if (diaDeSemana == DayOfWeek.SATURDAY || diaDeSemana == DayOfWeek.SUNDAY) {
            return false;
        }

        LocalTime time = dataHora.toLocalTime();
        if (time.isBefore(LocalTime.of(10, 0)) || time.isAfter(LocalTime.of(15, 0))) {
            return false;
        }

        if (dataHora.getMinute() != 0 || dataHora.getSecond() != 0 || dataHora.getNano() != 0) {
            return false;
        }

        return true;
    }

    private boolean isHorarioDisponivel(LocalDateTime dataHoraInicioSlot, Integer tempoEstimadoNovoServico) {
        LocalDateTime dataHoraFimSlot = dataHoraInicioSlot.plusMinutes(60);

        // Busca todos os agendamentos que começam dentro deste slot, independentemente do atendente
        List<Agendamento> agendamentosNoSlot = repository.findByDataHoraBetween(dataHoraInicioSlot, dataHoraFimSlot);

        int tempoTotalOcupadoNoSlot = 0;

        for (Agendamento agendamento : agendamentosNoSlot) {
            try {
                Integer tempoServicoExistente = getTempoEstimadoServico(agendamento.getServicoId());
                if (tempoServicoExistente != null) {
                    tempoTotalOcupadoNoSlot += tempoServicoExistente;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Aviso: Serviço com ID " + agendamento.getServicoId() + " não encontrado para agendamento " + agendamento.getId());
            }
        }
        return (tempoTotalOcupadoNoSlot + tempoEstimadoNovoServico) <= 60;
    }

    public List<LocalDateTime> getHorariosDisponiveisNoDia(LocalDate data) {
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();

        DayOfWeek diaDeSemana = data.getDayOfWeek();
        if (diaDeSemana == DayOfWeek.SATURDAY || diaDeSemana == DayOfWeek.SUNDAY) {
            return horariosDisponiveis; // Retorna lista vazia se for fim de semana
        }

        for (int hour = 10; hour <= 15; hour++) {
            LocalDateTime slotInicio = LocalDateTime.of(data, LocalTime.of(hour, 0));

            if (isHorarioDisponivel(slotInicio, 5)) {
                horariosDisponiveis.add(slotInicio);
            }
        }
        return horariosDisponiveis;
    }
}