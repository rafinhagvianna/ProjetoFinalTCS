package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.dto.AgendamentoRequestDTO;
import com.example.Agendamento_Service.dto.DocumentoPendenteResponseDTO;
import com.example.Agendamento_Service.dto.DocumentoStatusUpdateRequestDTO;
import com.example.Agendamento_Service.client.DocumentoCatalogoResponse;
// import com.example.Agendamento_Service.dto.ServicoResponseDTO; // Não será mais usado diretamente
import com.example.Agendamento_Service.client.ServicoResponse; // DTO do Catalogo-Service
import com.example.Agendamento_Service.client.TipoDocumentoResponse;
import com.example.Agendamento_Service.enums.StatusAgendamento;
import com.example.Agendamento_Service.enums.StatusDocumento; // Supondo que este enum esteja aqui
import com.example.Agendamento_Service.exception.ComunicacaoServicoException;
import com.example.Agendamento_Service.exception.RecursoNaoEncontradoException;
import com.example.Agendamento_Service.service.CatalogoServiceFacade; // Importe o Facade
import com.example.Agendamento_Service.service.UsuarioServiceFacade; // Importe o Facade
import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.model.DocumentoPendente;
import com.example.Agendamento_Service.repository.AgendamentoRepository;
import com.example.Agendamento_Service.repository.DocumentoPendenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient; // Manter se houver outras chamadas WebClient

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors; // Para coletar streams

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final DocumentoPendenteRepository documentoPendenteRepository;
    private final CatalogoServiceFacade catalogoServiceFacade; // Injetando o Facade
    private final UsuarioServiceFacade usuarioServiceFacade;   // Injetando o Facade

    // Remover WebClient se não for mais usado, ou manter se for para outros serviços não coberto pelos Facades
    // private final WebClient webClientServicos;

    public AgendamentoService(AgendamentoRepository repository,
                              DocumentoPendenteRepository documentoPendenteRepository,
                              CatalogoServiceFacade catalogoServiceFacade, 
                              UsuarioServiceFacade usuarioServiceFacade ){
        this.repository =repository;
        this.documentoPendenteRepository = documentoPendenteRepository;
        this.catalogoServiceFacade = catalogoServiceFacade;
        this.usuarioServiceFacade = usuarioServiceFacade;
    }


    public List<Agendamento> listarTodos() {
        // log.info("Listando todos os agendamentos.");
        return repository.findAll();
    }

    public Agendamento buscarPorId(UUID id) {
        // log.info("Buscando agendamento por ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado com o ID: " + id));
    }

    @Transactional
    public Agendamento salvar(AgendamentoRequestDTO agendamentoDTO) {
        // log.info("Iniciando criação de novo agendamento para usuário ID: {} e serviço ID: {}", agendamentoDTO.usuarioId(), agendamentoDTO.servicoId());

        if (!isDataEHoraDeTrabalhoValido(agendamentoDTO.dataHora())) {
            // log.warn("Tentativa de agendamento em horário inválido: {}", agendamentoDTO.dataHora());
            throw new IllegalArgumentException("A data e hora do agendamento devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
        }

        if (repository.findByUsuarioIdAndServicoIdAndDataHora(
                agendamentoDTO.usuarioId(),
                agendamentoDTO.servicoId(),
                agendamentoDTO.dataHora()).isPresent()) {
            // log.warn("Agendamento duplicado detectado para usuário ID: {}, serviço ID: {}, data/hora: {}",
                    // agendamentoDTO.usuarioId(), agendamentoDTO.servicoId(), agendamentoDTO.dataHora());
            throw new IllegalArgumentException("Este usuário já possui um agendamento para o mesmo serviço neste horário.");
        }

        // 1. Obter informações do serviço/setor usando o Facade
        ServicoResponse setorInfo = catalogoServiceFacade.buscarSetorPorId(agendamentoDTO.servicoId());
        if ("Serviço Indisponível".equals(setorInfo.nome())) {
            // log.error("Falha ao obter detalhes do serviço de catálogo ID {}. Abortando agendamento.", agendamentoDTO.servicoId());
            throw new ComunicacaoServicoException("Não foi possível obter detalhes do serviço de catálogo. Tente novamente mais tarde.", null);
        }
        Integer tempoEstimadoServico = Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElse(0);
        if (tempoEstimadoServico <= 0) {
            // log.warn("Tempo estimado para o serviço ID {} é inválido ({} minutos).", agendamentoDTO.servicoId(), tempoEstimadoServico);
            // Decide se quer lançar uma exceção ou usar um valor padrão aqui
            tempoEstimadoServico = 15; // Usar um padrão para serviços com tempo não configurado
        }


        // 2. Verificar disponibilidade do horário
        if (!isHorarioDisponivel(agendamentoDTO.dataHora(), tempoEstimadoServico, null)) {
            // log.warn("Horário selecionado não tem capacidade suficiente para o serviço ID {}.", agendamentoDTO.servicoId());
            throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setUsuarioId(agendamentoDTO.usuarioId());
        // agendamento.setAtendenteId(agendamentoDTO.atendenteId()); // atendenteId pode ser nulo agora
        agendamento.setServicoId(agendamentoDTO.servicoId());
        agendamento.setDataHora(agendamentoDTO.dataHora());
        agendamento.setCriadoEm(LocalDateTime.now()); // Garantir que está sendo setado

        // 3. Obter nome do cliente/usuário usando o Facade
        String nomeClienteSnapshot = usuarioServiceFacade.buscarNomeCliente(agendamentoDTO.usuarioId());
        agendamento.setNomeClienteSnapshot(nomeClienteSnapshot); // Adicionar no model Agendamento

        // 4. Adicionar snapshot do nome do serviço
        agendamento.setNomeServicoSnapshot(setorInfo.nome()); // Adicionar no model Agendamento
        agendamento.setStatus(StatusAgendamento.AGENDADO); // Definir um status inicial, se houver

        // 5. Lógica de documentos obrigatórios: Igual à triagem
        List<UUID> documentosObrigatoriosIds = Optional.ofNullable(setorInfo.documentosObrigatoriosIds()).orElse(new ArrayList<>());

        if (!documentosObrigatoriosIds.isEmpty()) {
            // log.debug("Processando {} documentos obrigatórios para o agendamento.", documentosObrigatoriosIds.size());
            List<DocumentoCatalogoResponse> detalhesDocumentos = catalogoServiceFacade.buscarDetalhesDocumentosObrigatorios(documentosObrigatoriosIds);

            detalhesDocumentos.forEach(tipoDoc -> {
                DocumentoPendente doc = new DocumentoPendente(
                    tipoDoc.id(),
                    tipoDoc.nome(),
                    StatusDocumento.PENDENTE // Status inicial para documentos de agendamento
                );
                agendamento.addDocumentoPendente(doc);
                // log.debug("Documento '{}' (ID: {}) adicionado como pendente para o agendamento.", tipoDoc.nome(), tipoDoc.id());
            });
        } else {
            // log.info("Nenhum documento obrigatório configurado para o serviço ID: {}.", agendamentoDTO.servicoId());
        }

        Agendamento agendamentoSalvo = repository.save(agendamento);
        // log.info("Agendamento ID: {} criado com sucesso.", agendamentoSalvo.getId());
        return agendamentoSalvo;
    }

    @Transactional
    public Agendamento atualizarAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO) {
        // log.info("Atualizando agendamento ID: {}", id);
        return repository.findById(id).map(agendamentoExistente -> {

            if (!isDataEHoraDeTrabalhoValido(agendamentoDTO.dataHora())) {
                // log.warn("Tentativa de atualização de agendamento ID {} para horário inválido: {}", id, agendamentoDTO.dataHora());
                throw new IllegalArgumentException("A data e hora do agendamento atualizado devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
            }

            Optional<Agendamento> duplicado = repository.findByUsuarioIdAndServicoIdAndDataHora(
                    agendamentoDTO.usuarioId(),
                    agendamentoDTO.servicoId(),
                    agendamentoDTO.dataHora());

            if (duplicado.isPresent() && !duplicado.get().getId().equals(agendamentoExistente.getId())) {
                // log.warn("Conflito na atualização do agendamento ID {}: Outro agendamento já existe com as mesmas credenciais.", id);
                throw new IllegalArgumentException("Já existe outro agendamento com as mesmas credenciais (usuário, serviço, data/hora).");
            }

            ServicoResponse setorInfo = catalogoServiceFacade.buscarSetorPorId(agendamentoDTO.servicoId());
            if ("Serviço Indisponível".equals(setorInfo.nome())) {
                // log.error("Falha ao obter detalhes do serviço de catálogo ID {} durante a atualização do agendamento {}.", agendamentoDTO.servicoId(), id);
                throw new ComunicacaoServicoException("Não foi possível obter detalhes do serviço de catálogo para validação. Tente novamente mais tarde.");
            }
            Integer tempoEstimadoServico = Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElse(0);
            if (tempoEstimadoServico <= 0) {
                tempoEstimadoServico = 15; // Usar um padrão
            }

            if (!isHorarioDisponivel(agendamentoDTO.dataHora(), tempoEstimadoServico, agendamentoExistente.getId())) {
                // log.warn("Horário selecionado para atualização do agendamento ID {} não tem capacidade suficiente.", id);
                throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço após a atualização.");
            }

            agendamentoExistente.setUsuarioId(agendamentoDTO.usuarioId());
            agendamentoExistente.setAtendenteId(agendamentoDTO.atendenteId());
            agendamentoExistente.setServicoId(agendamentoDTO.servicoId());
            agendamentoExistente.setDataHora(agendamentoDTO.dataHora());
            // Se necessário, atualizar snapshots de nome de cliente/serviço
            agendamentoExistente.setNomeClienteSnapshot(usuarioServiceFacade.buscarNomeCliente(agendamentoDTO.usuarioId()));
            agendamentoExistente.setNomeServicoSnapshot(setorInfo.nome());


            Agendamento atualizado = repository.save(agendamentoExistente);
            // log.info("Agendamento ID: {} atualizado com sucesso.", atualizado.getId());
            return atualizado;
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado com o ID: " + id));
    }

    @Transactional
    public Agendamento atualizarParcialAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO) {
        // log.info("Atualizando parcialmente agendamento ID: {}", id);
        return repository.findById(id).map(agendamentoExistente -> {
            boolean dataHoraChanged = false;
            boolean servicoChanged = false;
            boolean usuarioChanged = false;

            if (agendamentoDTO.usuarioId() != null && !agendamentoDTO.usuarioId().equals(agendamentoExistente.getUsuarioId())) {
                agendamentoExistente.setUsuarioId(agendamentoDTO.usuarioId());
                usuarioChanged = true;
            }
            if (agendamentoDTO.atendenteId() != null && !Objects.equals(agendamentoDTO.atendenteId(), agendamentoExistente.getAtendenteId())) {
                agendamentoExistente.setAtendenteId(agendamentoDTO.atendenteId());
            }
            if (agendamentoDTO.servicoId() != null && !agendamentoDTO.servicoId().equals(agendamentoExistente.getServicoId())) {
                agendamentoExistente.setServicoId(agendamentoDTO.servicoId());
                servicoChanged = true;
            }
            if (agendamentoDTO.dataHora() != null && !agendamentoDTO.dataHora().equals(agendamentoExistente.getDataHora())) {
                agendamentoExistente.setDataHora(agendamentoDTO.dataHora());
                dataHoraChanged = true;
            }

            if (dataHoraChanged && !isDataEHoraDeTrabalhoValido(agendamentoExistente.getDataHora())) {
                // log.warn("Tentativa de atualização parcial do agendamento ID {} para horário inválido: {}", id, agendamentoExistente.getDataHora());
                throw new IllegalArgumentException("A data e hora do agendamento atualizado (parcialmente) devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
            }

            Optional<Agendamento> duplicado = repository.findByUsuarioIdAndServicoIdAndDataHora(
                    agendamentoExistente.getUsuarioId(),
                    agendamentoExistente.getServicoId(),
                    agendamentoExistente.getDataHora());

            if (duplicado.isPresent() && !duplicado.get().getId().equals(agendamentoExistente.getId())) {
                // log.warn("Conflito na atualização parcial do agendamento ID {}: Outro agendamento já existe com as mesmas credenciais.", id);
                throw new IllegalArgumentException("Já existe outro agendamento com as mesmas credenciais (usuário, serviço, data/hora).");
            }

            // Recalcular tempo estimado e verificar disponibilidade apenas se serviço ou data/hora mudaram
            Integer tempoEstimadoServico = Optional.ofNullable(catalogoServiceFacade.buscarSetorPorId(agendamentoExistente.getServicoId()).tempoMedioMinutos()).orElse(0);
            if (tempoEstimadoServico <= 0) {
                tempoEstimadoServico = 15; // Usar um padrão
            }

            if (dataHoraChanged || servicoChanged) { // Só verifica disponibilidade se o tempo ou o slot mudaram
                if (!isHorarioDisponivel(agendamentoExistente.getDataHora(), tempoEstimadoServico, agendamentoExistente.getId())) {
                    // log.warn("Horário selecionado para atualização parcial do agendamento ID {} não tem capacidade suficiente.", id);
                    throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço após a atualização parcial.");
                }
            }

            // Atualizar snapshots de nome de cliente/serviço se os IDs correspondentes foram alterados
            if (usuarioChanged) {
                agendamentoExistente.setNomeClienteSnapshot(usuarioServiceFacade.buscarNomeCliente(agendamentoExistente.getUsuarioId()));
            }
            if (servicoChanged) {
                 agendamentoExistente.setNomeServicoSnapshot(catalogoServiceFacade.buscarSetorPorId(agendamentoExistente.getServicoId()).nome());
            }

            Agendamento atualizado = repository.save(agendamentoExistente);
            // log.info("Agendamento ID: {} atualizado parcialmente com sucesso.", atualizado.getId());
            return atualizado;
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado com o ID: " + id));
    }

    @Transactional
    public void deletar(UUID id) {
        // log.info("Deletando agendamento por ID: {}", id);
        if (!repository.existsById(id)) {
            // log.warn("Tentativa de deletar agendamento inexistente com ID: {}", id);
            throw new RecursoNaoEncontradoException("Agendamento não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
        // log.info("Agendamento ID: {} deletado com sucesso.", id);
    }

    // Método auxiliar para obter tempo estimado do serviço via Facade
    private Integer getTempoEstimadoServico(UUID servicoId) {
        ServicoResponse setorInfo = catalogoServiceFacade.buscarSetorPorId(servicoId);
        // Se o fallback for ativado, o nome será "Serviço Indisponível" e o tempo médio será 0.
        // A lógica de negócio deve decidir se 0 é aceitável ou se deve lançar uma exceção.
        if ("Serviço Indisponível".equals(setorInfo.nome())) {
            throw new ComunicacaoServicoException("Não foi possível obter o tempo estimado do serviço de catálogo para ID: " + servicoId);
        }
        return Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElseThrow(() ->
                new IllegalStateException("Tempo médio para o serviço ID " + servicoId + " não configurado no catálogo."));
    }


    private boolean isDataEHoraDeTrabalhoValido(LocalDateTime dataHora) {
        // ... (lógica existente, sem alterações significativas)
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
        LocalDateTime dataHoraFimSlot = dataHoraInicioSlot.plusMinutes(60); // Slot de 1 hora

        List<Agendamento> agendamentosNoSlot = repository.findByDataHoraBetween(dataHoraInicioSlot, dataHoraFimSlot);

        int tempoTotalOcupadoNoSlot = 0;

        for (Agendamento agendamento : agendamentosNoSlot) {
            if (agendamentoIdParaIgnorar != null && agendamento.getId().equals(agendamentoIdParaIgnorar)) {
                continue;
            }

            try {
                // Obter o tempo estimado do serviço para agendamentos existentes usando o Facade
                Integer tempoServicoExistente = getTempoEstimadoServico(agendamento.getServicoId());
                if (tempoServicoExistente != null) {
                    tempoTotalOcupadoNoSlot += tempoServicoExistente;
                }
            } catch (ComunicacaoServicoException | IllegalStateException e) {
                // log.warn("Aviso: Falha ao obter tempo estimado para serviço ID {} do agendamento {}. Ignorando este agendamento no cálculo de disponibilidade. Erro: {}",
                        // agendamento.getServicoId(), agendamento.getId(), e.getMessage());
                // Poderia também decidir falhar a validação se a informação de um serviço existente está indisponível
            }
        }
        return (tempoTotalOcupadoNoSlot + tempoEstimadoNovoServico) <= 60;
    }

    public List<LocalDateTime> getHorariosDisponiveisNoDia(LocalDate data) {
        // log.info("Buscando horários disponíveis para a data: {}", data);
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();

        DayOfWeek diaDeSemana = data.getDayOfWeek();
        if (diaDeSemana == DayOfWeek.SATURDAY || diaDeSemana == DayOfWeek.SUNDAY) {
            // log.info("Nenhum horário disponível para fins de semana.");
            return horariosDisponiveis;
        }

        // Para cada hora cheia entre 10h e 15h
        for (int hour = 10; hour <= 15; hour++) {
            LocalDateTime slotInicio = LocalDateTime.of(data, LocalTime.of(hour, 0));

            // Aqui, o '5' é um tempo estimado de serviço mínimo.
            // Poderíamos torná-lo configurável ou permitir que o cliente especifique o serviço para verificar disponibilidade exata.
            // Para "listar horários disponíveis", um valor pequeno é razoável para indicar que há algum espaço.
            if (isHorarioDisponivel(slotInicio, 5, null)) {
                horariosDisponiveis.add(slotInicio);
            }
        }
        // log.info("Encontrados {} horários disponíveis para a data {}.", horariosDisponiveis.size(), data);
        return horariosDisponiveis;
    }

    @Transactional
    public DocumentoPendenteResponseDTO atualizarStatusDocumentoAgendamento(
            UUID agendamentoId,
            UUID documentoCatalogoId,
            DocumentoStatusUpdateRequestDTO requestDTO) {

        // log.info("Atualizando status do documento para agendamento ID: {} e documento de catálogo ID: {}", agendamentoId, documentoCatalogoId);

        Agendamento agendamento = repository.findById(agendamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento com ID " + agendamentoId + " não encontrado."));

        DocumentoPendente documento = agendamento.getDocumentosPendentes().stream()
                .filter(d -> d.getDocumentoCatalogoId().equals(documentoCatalogoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Documento com ID de catálogo " + documentoCatalogoId + " não encontrado para o agendamento " + agendamentoId
                ));

        documento.setStatus(requestDTO.status());
        documento.setObservacao(requestDTO.observacaoValidacao());
        if (requestDTO.urlVisualizacao() != null && !requestDTO.urlVisualizacao().isBlank()) {
            documento.setUrlDocumento(requestDTO.urlVisualizacao());
        }

        repository.save(agendamento); // Persiste a mudança no documento através do relacionamento com Agendamento
        // log.info("Documento '{}' (ID Catálogo: {}) do agendamento ID: {} atualizado para status: {}.",
                // documento.getNomeDocumentoSnapshot(), documentoCatalogoId, agendamentoId, requestDTO.status());

        return toDocumentoPendenteResponseDTO(documento);
    }

    private DocumentoPendenteResponseDTO toDocumentoPendenteResponseDTO(DocumentoPendente doc) {
        return new DocumentoPendenteResponseDTO(
                doc.getId(),
                doc.getDocumentoCatalogoId(),
                doc.getNomeDocumentoSnapshot(),
                doc.getStatus(),
                doc.getObservacao(),
                doc.getUrlDocumento()
        );
    }

    // Este método foi consolidado com atualizarStatusDocumentoAgendamento
    // @Transactional
    // public void atualizarStatusDocumentoTriagem(UUID agendamentoId, UUID documentoCatalogoId, DocumentoStatusUpdateRequestDTO requestDTO) {
    //     // ... (lógica anterior)
    // }
}