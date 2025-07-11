package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.dto.AgendamentoRequestDTO;
import com.example.Agendamento_Service.dto.DocumentoPendenteResponseDTO;
import com.example.Agendamento_Service.dto.DocumentoStatusUpdateRequestDTO;
import com.example.Agendamento_Service.dto.TokenValidationDTO;
import com.example.Agendamento_Service.client.AuthServiceClient;
import com.example.Agendamento_Service.client.DocumentoCatalogoResponse;
import com.example.Agendamento_Service.client.ServicoResponse;
import com.example.Agendamento_Service.enums.StatusAgendamento;
import com.example.Agendamento_Service.enums.StatusDocumento;
import com.example.Agendamento_Service.exception.AuthServiceException;
import com.example.Agendamento_Service.exception.ComunicacaoServicoException;
import com.example.Agendamento_Service.exception.InvalidTokenException;
import com.example.Agendamento_Service.exception.RecursoNaoEncontradoException;
import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.model.DocumentoPendente;
import com.example.Agendamento_Service.repository.AgendamentoRepository;
import com.example.Agendamento_Service.repository.DocumentoPendenteRepository; // Mantido, caso seja usado indiretamente ou em futuras features
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.reactive.function.client.WebClient; // Removido, pois não está sendo usado diretamente

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Adicionado para Objects.equals no PATCH
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final DocumentoPendenteRepository documentoPendenteRepository; // Mantido, se não for usado, pode ser removido
    private final CatalogoServiceFacade catalogoServiceFacade;
    private final UsuarioServiceFacade usuarioServiceFacade;
    private final AuthServiceClient authServiceClient;

    // O WebClient foi removido do construtor, pois os Facades agora o encapsulam.
    public AgendamentoService(AgendamentoRepository repository,
                              DocumentoPendenteRepository documentoPendenteRepository,
                              CatalogoServiceFacade catalogoServiceFacade,
                              UsuarioServiceFacade usuarioServiceFacade,
                              AuthServiceClient authServiceClient) {
        this.repository = repository;
        this.documentoPendenteRepository = documentoPendenteRepository;
        this.catalogoServiceFacade = catalogoServiceFacade;
        this.usuarioServiceFacade = usuarioServiceFacade;
        this.authServiceClient = authServiceClient;
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

    public List<Agendamento> buscarPorCliente(UUID id) {
        return repository.findByUsuarioIdAndStatus(id, StatusAgendamento.AGENDADO);
    }

    @Transactional
    public Agendamento salvar(AgendamentoRequestDTO agendamentoDTO, UUID idCliente) {
        // log.info("Iniciando criação de novo agendamento para usuário ID: {} e serviço ID: {}", agendamentoDTO.usuarioId(), agendamentoDTO.servicoId());

        if (!isDataEHoraDeTrabalhoValido(agendamentoDTO.dataHora())) {
            // log.warn("Tentativa de agendamento em horário inválido: {}", agendamentoDTO.dataHora());
            throw new IllegalArgumentException("A data e hora do agendamento devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
        }

        if (repository.findByUsuarioIdAndServicoIdAndDataHora(
                idCliente,
                agendamentoDTO.servicoId(),
                agendamentoDTO.dataHora()).isPresent()) {
            // log.warn("Agendamento duplicado detectado para usuário ID: {}, serviço ID: {}, data/hora: {}",
            // agendamentoDTO.usuarioId(), agendamentoDTO.servicoId(), agendamentoDTO.dataHora());
            throw new IllegalArgumentException("Este usuário já possui um agendamento para o mesmo serviço neste horário.");
        }

        // 1. Obter informações do serviço/setor usando o Facade
        ServicoResponse setorInfo = catalogoServiceFacade.buscarSetorPorId(agendamentoDTO.servicoId());
        // A verificação de "Serviço Indisponível" deve ser feita no Facade ou GlobalExceptionHandler
        // Se o Facade já lança ComunicacaoServicoException, essa checagem pode ser redundante aqui.
        // Por simplicidade, vou manter a checagem de nome para o caso do fallback do Facade retornar um objeto com nome padrão.
        if (setorInfo == null || "Serviço Indisponível".equals(setorInfo.nome())) {
            throw new ComunicacaoServicoException("Não foi possível obter detalhes do serviço de catálogo. Tente novamente mais tarde.");
        }

        Integer tempoEstimadoServico = Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElse(0);
        if (tempoEstimadoServico <= 0) {
            // log.warn("Tempo estimado para o serviço ID {} é inválido ({} minutos).", agendamentoDTO.servicoId(), tempoEstimadoServico);
            tempoEstimadoServico = 15; // Usar um padrão para serviços com tempo não configurado, ou lançar exceção
        }

        // 2. Verificar disponibilidade do horário
        // CORREÇÃO AQUI: Passar agendamentoDTO.servicoId() em vez de tempoEstimadoServico
        if (!isHorarioDisponivel(agendamentoDTO.dataHora(), agendamentoDTO.servicoId(), null)) {
            // log.warn("Horário selecionado não tem capacidade suficiente para o serviço ID {}.", agendamentoDTO.servicoId());
            throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço.");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setUsuarioId(idCliente);
        agendamento.setAtendenteId(agendamentoDTO.atendenteId()); // atendenteId pode ser nulo agora
        agendamento.setServicoId(agendamentoDTO.servicoId());
        agendamento.setDataHora(agendamentoDTO.dataHora());
        agendamento.setCriadoEm(LocalDateTime.now()); // Garantir que está sendo setado

        // 3. Obter nome do cliente/usuário usando o Facade
        String nomeClienteSnapshot = usuarioServiceFacade.buscarNomeCliente(idCliente);
        agendamento.setNomeClienteSnapshot(nomeClienteSnapshot);

        // 4. Adicionar snapshot do nome do serviço
        agendamento.setNomeServicoSnapshot(setorInfo.nome());
        agendamento.setStatus(StatusAgendamento.AGENDADO); // Definir um status inicial, se houver

        // 5. Lógica de documentos obrigatórios: Igual à triagem
        List<UUID> documentosObrigatoriosIds = Optional.ofNullable(setorInfo.documentosObrigatoriosIds()).orElse(new ArrayList<>());

        if (!documentosObrigatoriosIds.isEmpty()) {
            // log.debug("Processando {} documentos obrigatórios para o agendamento.", documentosObrigatoriosIds.size());
            List<DocumentoCatalogoResponse> detalhesDocumentos = catalogoServiceFacade.buscarDetalhesDocumentosObrigatorios(documentosObrigatoriosIds);

            detalhesDocumentos.forEach(tipoDoc -> {
                DocumentoPendente doc = new DocumentoPendente(
                        tipoDoc.id(), // Supondo que DocumentoPendente tem construtor com ID, Nome
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
    public Agendamento atualizarAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO, UUID idCliente) {
        // log.info("Atualizando agendamento ID: {}", id);
        return repository.findById(id).map(agendamentoExistente -> {

            if (!isDataEHoraDeTrabalhoValido(agendamentoDTO.dataHora())) {
                // log.warn("Tentativa de atualização de agendamento ID {} para horário inválido: {}", id, agendamentoDTO.dataHora());
                throw new IllegalArgumentException("A data e hora do agendamento atualizado devem ser de segunda a sexta, entre 10h00 e 16h00, e em horas cheias.");
            }

            Optional<Agendamento> duplicado = repository.findByUsuarioIdAndServicoIdAndDataHora(
                    idCliente,
                    agendamentoDTO.servicoId(),
                    agendamentoDTO.dataHora());

            if (duplicado.isPresent() && !duplicado.get().getId().equals(agendamentoExistente.getId())) {
                // log.warn("Conflito na atualização do agendamento ID {}: Outro agendamento já existe com as mesmas credenciais.", id);
                throw new IllegalArgumentException("Já existe outro agendamento com as mesmas credenciais (usuário, serviço, data/hora).");
            }

            ServicoResponse setorInfo = catalogoServiceFacade.buscarSetorPorId(agendamentoDTO.servicoId());
            if (setorInfo == null || "Serviço Indisponível".equals(setorInfo.nome())) {
                // log.error("Falha ao obter detalhes do serviço de catálogo ID {} durante a atualização do agendamento {}.", agendamentoDTO.servicoId(), id);
                throw new ComunicacaoServicoException("Não foi possível obter detalhes do serviço de catálogo para validação. Tente novamente mais tarde.");
            }
            Integer tempoEstimadoServico = Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElse(0);
            if (tempoEstimadoServico <= 0) {
                tempoEstimadoServico = 15; // Usar um padrão
            }

            // CORREÇÃO AQUI: Passar agendamentoDTO.servicoId() em vez de tempoEstimadoServico
            if (!isHorarioDisponivel(agendamentoDTO.dataHora(), agendamentoDTO.servicoId(), agendamentoExistente.getId())) {
                // log.warn("Horário selecionado para atualização do agendamento ID {} não tem capacidade suficiente.", id);
                throw new IllegalStateException("O horário selecionado não tem capacidade suficiente para este serviço após a atualização.");
            }

            agendamentoExistente.setUsuarioId(idCliente);
            agendamentoExistente.setAtendenteId(agendamentoDTO.atendenteId());
            agendamentoExistente.setServicoId(agendamentoDTO.servicoId());
            agendamentoExistente.setDataHora(agendamentoDTO.dataHora());
            // Se necessário, atualizar snapshots de nome de cliente/serviço
            agendamentoExistente.setNomeClienteSnapshot(usuarioServiceFacade.buscarNomeCliente(idCliente));
            agendamentoExistente.setNomeServicoSnapshot(setorInfo.nome());


            Agendamento atualizado = repository.save(agendamentoExistente);
            // log.info("Agendamento ID: {} atualizado com sucesso.", atualizado.getId());
            return atualizado;
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado com o ID: " + id));
    }

    @Transactional
    public Agendamento atualizarParcialAgendamento(UUID id, AgendamentoRequestDTO agendamentoDTO, UUID idCliente) {
        // log.info("Atualizando parcialmente agendamento ID: {}", id);
        return repository.findById(id).map(agendamentoExistente -> {
            boolean dataHoraChanged = false;
            boolean servicoChanged = false;
            boolean usuarioChanged = false;

            if (idCliente != null && !idCliente.equals(agendamentoExistente.getUsuarioId())) {
                agendamentoExistente.setUsuarioId(idCliente);
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
            // O tempoEstimadoServico é usado para a validação de capacidade, não para ser passado ao isHorarioDisponivel
            Integer tempoEstimadoServico = Optional.ofNullable(catalogoServiceFacade.buscarSetorPorId(agendamentoExistente.getServicoId()).tempoMedioMinutos()).orElse(0);
            if (tempoEstimadoServico <= 0) {
                tempoEstimadoServico = 15; // Usar um padrão
            }

            if (dataHoraChanged || servicoChanged) { // Só verifica disponibilidade se o tempo ou o slot mudaram
                // CORREÇÃO AQUI: Passar agendamentoExistente.getServicoId() em vez de tempoEstimadoServico
                if (!isHorarioDisponivel(agendamentoExistente.getDataHora(), agendamentoExistente.getServicoId(), agendamentoExistente.getId())) {
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
        if (setorInfo == null || "Serviço Indisponível".equals(setorInfo.nome())) { // Adicionado checagem para setorInfo ser null
            throw new ComunicacaoServicoException("Não foi possível obter o tempo estimado do serviço de catálogo para ID: " + servicoId);
        }
        return Optional.ofNullable(setorInfo.tempoMedioMinutos()).orElseThrow(() ->
                new IllegalStateException("Tempo médio para o serviço ID " + servicoId + " não configurado no catálogo."));
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

    private boolean isHorarioDisponivel(LocalDateTime dataHora, UUID servicoId, UUID agendamentoIdParaIgnorar) {
        DayOfWeek diaDaSemana = dataHora.getDayOfWeek();
        LocalTime hora = dataHora.toLocalTime();

        if (diaDaSemana == DayOfWeek.SATURDAY || diaDaSemana == DayOfWeek.SUNDAY ||
                hora.isBefore(LocalTime.of(10, 0)) || hora.isAfter(LocalTime.of(15, 0)) || // Slots de 10h a 15h
                dataHora.getMinute() != 0 || dataHora.getSecond() != 0 || dataHora.getNano() != 0) {
            return false;
        }

        LocalDateTime inicioDoSlot = dataHora.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fimDoSlot = inicioDoSlot.plusMinutes(60);

        List<Agendamento> agendamentosNoSlot = repository.findByDataHoraBetween(inicioDoSlot, fimDoSlot);

        int tempoTotalOcupadoNoSlot = 0;
        for (Agendamento agendamento : agendamentosNoSlot) {
            if (agendamentoIdParaIgnorar != null && agendamento.getId().equals(agendamentoIdParaIgnorar)) {
                continue;
            }
            tempoTotalOcupadoNoSlot += getTempoEstimadoServico(agendamento.getServicoId());
        }

        int tempoEstimadoNovoServico = getTempoEstimadoServico(servicoId);
        return (tempoTotalOcupadoNoSlot + tempoEstimadoNovoServico) <= 60;
    }

    public List<LocalDateTime> getHorariosDisponiveisNoDia(LocalDate data, UUID servicoId) {
        // log.info("Buscando horários disponíveis para a data: {}", data);
        List<LocalDateTime> horariosDisponiveis = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();

        if (data.isBefore(agora.toLocalDate())) {
            return new ArrayList<>();
        }

        // Obter o tempo estimado do serviço ESCOLHIDO pelo usuário
        // Este método getTempoEstimadoServico agora usará o Facade e não o mock fixo.
        int tempoEstimadoServicoEscolhido = getTempoEstimadoServico(servicoId);

        for (int hour = 10; hour <= 15; hour++) {
            LocalDateTime slotStart = LocalDateTime.of(data, LocalTime.of(hour, 0));

            if (data.isEqual(agora.toLocalDate()) && slotStart.isBefore(agora)) {
                continue;
            }

            // isHorarioDisponivel agora usa o tempo estimado do SERVIÇO ESCOLHIDO
            if (isHorarioDisponivel(slotStart, servicoId, null)) {
                horariosDisponiveis.add(slotStart);
            }
        }
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

        repository.save(agendamento);
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

    public UUID validateTokenAndGetUserId(String authorizationHeader) throws InvalidTokenException, AuthServiceException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Cabeçalho Authorization Bearer ausente ou inválido.");
        }

        TokenValidationDTO validationResponse;
        try {
            validationResponse = authServiceClient.validateToken(authorizationHeader);
        } catch (Exception e) {
            // Logar o erro completo para depuração
            System.err.println("Erro ao comunicar com AuthServer via Feign: " + e.getMessage());
            throw new AuthServiceException("Falha na comunicação com o servidor de autenticação.", e);
        }

        boolean isValid = (validationResponse.getMessage() != null && validationResponse.getMessage().contains("válido"));

        UUID idCliente = validationResponse.getUserId();

        if (!isValid || idCliente == null) {
            String message = validationResponse.getMessage() != null ? validationResponse.getMessage() : "Token inválido ou sessão expirada.";
            throw new InvalidTokenException(message);
        }

        return idCliente;
    }
}
