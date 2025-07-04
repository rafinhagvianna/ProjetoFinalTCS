package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.dto.AgendamentoRequestDTO;
import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public Agendamento salvar(AgendamentoRequestDTO agendamentoDTO) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuarioId(agendamentoDTO.getUsuarioId());
        agendamento.setAtendenteId(agendamentoDTO.getAtendenteId());
        agendamento.setServicoId(agendamentoDTO.getServicoId());
        agendamento.setDataHora(agendamentoDTO.getDataHora());
        // agendamento.setCriadoEm() será populado automaticamente se você tiver @CreationTimestamp ou similar na entidade

        if (!isDataEHoraDeTrabalhoValido(agendamento.getDataHora())) {
            throw new IllegalArgumentException("A data e hora do agendamento devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
        }

        if (repository.findByUsuarioIdAndServicoIdAndDataHora(
                agendamento.getUsuarioId(),
                agendamento.getServicoId(),
                agendamento.getDataHora()).isPresent()) {
            throw new IllegalArgumentException("Este usuário já possui um agendamento para o mesmo serviço neste horário.");
        }

        Integer tempoEstimadoServico = getTempoEstimadoServico(agendamento.getServicoId());
        if (tempoEstimadoServico == null) {
            throw new IllegalArgumentException("Não foi possível obter o tempo estimado para o serviço com ID: " + agendamento.getServicoId());
        }

        if (!isHorarioDisponivel(agendamento.getDataHora(), tempoEstimadoServico, null)) {
            throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço.");
        }

        return repository.save(agendamento);
    }

    public Agendamento atualizarAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO) {
        return repository.findById(id).map(agendamentoExistente -> {

            agendamentoExistente.setUsuarioId(agendamentoDTO.getUsuarioId());
            agendamentoExistente.setAtendenteId(agendamentoDTO.getAtendenteId());
            agendamentoExistente.setServicoId(agendamentoDTO.getServicoId());
            agendamentoExistente.setDataHora(agendamentoDTO.getDataHora());

            if (!isDataEHoraDeTrabalhoValido(agendamentoExistente.getDataHora())) {
                throw new IllegalArgumentException("A data e hora do agendamento atualizado devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
            }

            Optional<Agendamento> duplicado = repository.findByUsuarioIdAndServicoIdAndDataHora(
                    agendamentoExistente.getUsuarioId(),
                    agendamentoExistente.getServicoId(),
                    agendamentoExistente.getDataHora());

            if (duplicado.isPresent() && !duplicado.get().getId().equals(agendamentoExistente.getId())) {
                throw new IllegalArgumentException("Já existe outro agendamento com as mesmas credenciais (usuário, serviço, data/hora).");
            }

            Integer tempoEstimadoServico = getTempoEstimadoServico(agendamentoExistente.getServicoId());
            if (tempoEstimadoServico == null) {
                throw new IllegalArgumentException("Não foi possível obter o tempo estimado para o serviço com ID: " + agendamentoExistente.getServicoId());
            }

            if (!isHorarioDisponivel(agendamentoExistente.getDataHora(), tempoEstimadoServico, agendamentoExistente.getId())) {
                throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço após a atualização.");
            }

            return repository.save(agendamentoExistente);
        }).orElseThrow(() -> new RuntimeException("Agendamento não encontrado com o ID: " + id)); // Posteriormente, uma NotFoundException
    }

    public Agendamento atualizarParcialAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO) {
        return repository.findById(id).map(agendamentoExistente -> {
            boolean dataHoraChanged = false;

            if (agendamentoDTO.getUsuarioId() != null) agendamentoExistente.setUsuarioId(agendamentoDTO.getUsuarioId());
            if (agendamentoDTO.getAtendenteId() != null) agendamentoExistente.setAtendenteId(agendamentoDTO.getAtendenteId());
            if (agendamentoDTO.getServicoId() != null) agendamentoExistente.setServicoId(agendamentoDTO.getServicoId());
            if (agendamentoDTO.getDataHora() != null) {
                agendamentoExistente.setDataHora(agendamentoDTO.getDataHora());
                dataHoraChanged = true;
            }
            // campos que podem ser atualizados via PATCH (ex: atendidoEm, observacoes)
            // if (agendamentoDTO.getAtendidoEm() != null) agendamentoExistente.setAtendidoEm(agendamentoDTO.getAtendidoEm());
            // if (agendamentoDTO.getObservacoes() != null) agendamentoExistente.setObservacoes(agendamentoDTO.getObservacoes());


            if (dataHoraChanged && !isDataEHoraDeTrabalhoValido(agendamentoExistente.getDataHora())) {
                throw new IllegalArgumentException("A data e hora do agendamento atualizado (parcialmente) devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
            }

            Optional<Agendamento> duplicado = repository.findByUsuarioIdAndServicoIdAndDataHora(
                    agendamentoExistente.getUsuarioId(),
                    agendamentoExistente.getServicoId(),
                    agendamentoExistente.getDataHora());

            if (duplicado.isPresent() && !duplicado.get().getId().equals(agendamentoExistente.getId())) {
                throw new IllegalArgumentException("Já existe outro agendamento com as mesmas credenciais (usuário, serviço, data/hora).");
            }

            Integer tempoEstimadoServico = getTempoEstimadoServico(agendamentoExistente.getServicoId());
            if (tempoEstimadoServico == null) {
                throw new IllegalArgumentException("Não foi possível obter o tempo estimado para o serviço com ID: " + agendamentoExistente.getServicoId());
            }

            if (!isHorarioDisponivel(agendamentoExistente.getDataHora(), tempoEstimadoServico, agendamentoExistente.getId())) {
                throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço após a atualização parcial.");
            }

            return repository.save(agendamentoExistente);
        }).orElseThrow(() -> new RuntimeException("Agendamento não encontrado com o ID: " + id)); // Posteriormente, uma NotFoundException
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }

    // getTempoEstimadoServico (continua mockado temporariamente)
    public Integer getTempoEstimadoServico(UUID servicoId) {
//        return webClientServicos.get()
//                .uri("/{id}", servicoId)
//                .retrieve()
//                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
//                        clientResponse -> Mono.error(new IllegalArgumentException("Serviço não encontrado com o ID: " + servicoId)))
//                .bodyToMono(ServicoResponseDTO.class)
//                .map(ServicoResponseDTO::getTempoEstimadoEmMinutos)
//                .block();
        System.out.println("DEBUG: Retornando tempo estimado fixo de 15 minutos para servicoId: " + servicoId); // Mudei para 15min para facilitar testes de 60min com 4 serviços
        return 15;
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

    private boolean isHorarioDisponivel(LocalDateTime dataHoraInicioSlot, Integer tempoEstimadoNovoServico, UUID agendamentoIdParaIgnorar) {
        LocalDateTime dataHoraFimSlot = dataHoraInicioSlot.plusMinutes(60);

        List<Agendamento> agendamentosNoSlot = repository.findByDataHoraBetween(dataHoraInicioSlot, dataHoraFimSlot);

        int tempoTotalOcupadoNoSlot = 0;

        for (Agendamento agendamento : agendamentosNoSlot) {
            if (agendamentoIdParaIgnorar != null && agendamento.getId().equals(agendamentoIdParaIgnorar)) {
                continue;
            }

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
            return horariosDisponiveis;
        }

        for (int hour = 10; hour <= 15; hour++) {
            LocalDateTime slotInicio = LocalDateTime.of(data, LocalTime.of(hour, 0));

            if (isHorarioDisponivel(slotInicio, 5, null)) {
                horariosDisponiveis.add(slotInicio);
            }
        }
        return horariosDisponiveis;
    }
}