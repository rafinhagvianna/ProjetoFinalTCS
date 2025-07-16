package com.microsservicos.triagem.service;

import com.microsservicos.triagem.client.AuthServiceClient;
import com.microsservicos.triagem.client.CadastroClienteServiceClient;
import com.microsservicos.triagem.client.CatalogoServiceClient;
import com.microsservicos.triagem.client.SetorResponse;
import com.microsservicos.triagem.client.DocumentoCatalogoResponse;
import com.microsservicos.triagem.dto.*;
import com.microsservicos.triagem.enums.StatusDocumento;
import com.microsservicos.triagem.enums.StatusTriagem;
import com.microsservicos.triagem.exception.AuthServiceException;
import com.microsservicos.triagem.exception.ComunicacaoServicoException;
import com.microsservicos.triagem.exception.InvalidTokenException;
import com.microsservicos.triagem.exception.RecursoNaoEncontradoException;
import com.microsservicos.triagem.mapper.TriagemMapper;
import com.microsservicos.triagem.model.DocumentoPendente;
import com.microsservicos.triagem.model.Triagem;
import com.microsservicos.triagem.repository.DocumentoPendenteRepository;
import com.microsservicos.triagem.repository.TriagemRepository;

import feign.FeignException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Certifique-se de que Optional está importado
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final DocumentoPendenteRepository documentoPendenteRepository;
    private final CatalogoServiceFacade catalogoServiceClient;
    private final CadastroClienteServiceClient cadastroClienteServiceClient;
    private final TriagemMapper triagemMapper;
    private final AuthServiceClient authServiceClient;

    public TriagemService(TriagemRepository triagemRepository,
                          DocumentoPendenteRepository documentoPendenteRepository,
                          CatalogoServiceFacade catalogoServiceClient,
                          CadastroClienteServiceClient cadastroClienteServiceClient,
                          TriagemMapper triagemMapper,
                          AuthServiceClient authServiceClient) {
        this.triagemRepository = triagemRepository;
        this.documentoPendenteRepository = documentoPendenteRepository;
        this.catalogoServiceClient = catalogoServiceClient;
        this.cadastroClienteServiceClient = cadastroClienteServiceClient;
        this.triagemMapper = triagemMapper;
        this.authServiceClient = authServiceClient;
    }

    @Transactional
    public TriagemResponseDTO criarTriagem(TriagemRequestDTO dto, UUID clienteId) {
        Triagem triagem = new Triagem();
        triagem.setClienteId(clienteId);
        triagem.setServicoId(dto.servicoId());
        triagem.setStatus(StatusTriagem.AGUARDANDO);
        triagem.setPrioridade(dto.prioridade());
        triagem.setHorarioSolicitacao(LocalDateTime.now());

        SetorResponse servicoInfo = catalogoServiceClient.buscarSetorPorId(dto.servicoId());
        triagem.setNomeClienteSnapshot(cadastroClienteServiceClient.getNomeCliente(clienteId));
        triagem.setNomeServicoSnapshot(servicoInfo.nome());

        triagem.setTempoEstimadoMinutos(servicoInfo.tempoMedioMinutos());
        
        LocalDateTime horarioInicioEstimado = calcularHorarioInicioEstimado(triagem);
        triagem.setHorarioEstimadoAtendimento(horarioInicioEstimado.plusMinutes(servicoInfo.tempoMedioMinutos())); // O horário estimado é o início + duração

        List<UUID> documentosObrigatoriosIds = Optional.ofNullable(servicoInfo.documentosObrigatoriosIds()).orElse(new ArrayList<>());

        if (!documentosObrigatoriosIds.isEmpty()) {
            documentosObrigatoriosIds.forEach(documentoId -> {
                try {
                    // Para cada UUID, chame o serviço de catálogo para obter os detalhes do documento
                    DocumentoCatalogoResponse tipoDoc = catalogoServiceClient.buscarDocumentoCatalogoPorId(documentoId);

                    DocumentoPendente doc = new DocumentoPendente();
                    doc.setDocumentoCatalogoId(tipoDoc.id());
                    doc.setNomeDocumentoSnapshot(tipoDoc.nome()); // Use o nome real do documento do catálogo
                    doc.setStatus(StatusDocumento.PENDENTE);
                    triagem.addDocumentoPendente(doc);
                } catch (FeignException e) {
                    // log.warn("Falha ao obter detalhes do documento de catálogo ID {} para a triagem do serviço {}. O documento não será adicionado. Erro: {}", documentoId, dto.servicoId(), e.getMessage());
                    // Decida aqui se a falha em buscar um documento deve impedir a criação da triagem
                    // ou se apenas o documento específico deve ser ignorado.
                    // Por enquanto, apenas logamos o aviso.
                } catch (Exception e) {
                    // log.error("Erro inesperado ao processar documento de catálogo ID {} para triagem do serviço {}: {}", documentoId, dto.servicoId(), e.getMessage(), e);
                }
            });
        } else {
            // log.info("Nenhum documento obrigatório configurado para o serviço ID: {}.", dto.servicoId());
        }

        Triagem salva = triagemRepository.save(triagem);

        // DISPARAR O RECÁLCULO GLOBAL ASSÍNCRONO APÓS A CRIAÇÃO DE UMA NOVA TRIAGEM
        recalcularHorariosEstimadosDaFila();

        return triagemMapper.toResponseDTO(salva);
    }

    @Transactional
    public TriagemResponseDTO buscarProximaTriagem() {
        Triagem triagem = triagemRepository
                .findFirstByStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem.AGUARDANDO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma triagem aguardando na fila."));

        triagem.setStatus(StatusTriagem.EM_ATENDIMENTO);
        Triagem atualizada = triagemRepository.save(triagem);

        recalcularHorariosEstimadosDaFila();

        return triagemMapper.toResponseDTO(atualizada);
    }
    
    @Transactional
    public TriagemResponseDTO buscarPorCliente(UUID id) {
        Triagem triagem = triagemRepository.findByClienteIdAndStatus(id, StatusTriagem.AGUARDANDO);

        return triagemMapper.toResponseDTO(triagem);
    }

    @Transactional
    public TriagemResponseDTO buscarPorId(UUID id) {
        Triagem triagem = triagemRepository.findById(id).orElseThrow();

        return triagemMapper.toResponseDTO(triagem);
    }


    @Transactional
    public TriagemResponseDTO atualizarStatus(UUID id, StatusTriagem novoStatus) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Triagem com ID " + id + " não encontrada."));

        StatusTriagem statusAnterior = triagem.getStatus(); // Captura o status anterior para decidir se recalcula

        triagem.setStatus(novoStatus);
        Triagem atualizada = triagemRepository.save(triagem);

        // DISPARAR O RECÁLCULO GLOBAL SE A MUDANÇA DE STATUS AFETA A FILA
        if ((statusAnterior == StatusTriagem.EM_ATENDIMENTO && (novoStatus == StatusTriagem.FINALIZADO || novoStatus == StatusTriagem.CANCELADO)) ||
                (statusAnterior == StatusTriagem.AGUARDANDO && novoStatus == StatusTriagem.CANCELADO)) {
            recalcularHorariosEstimadosDaFila();
        }

        return triagemMapper.toResponseDTO(atualizada);
    }

    @Transactional
    public TriagemResponseDTO atualizarStatusDocumento(
            UUID triagemId,
            UUID documentoCatalogoId,
            StatusDocumento status,
            String urlDocumento,
            String observacao) {

        Triagem triagem = triagemRepository.findById(triagemId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Triagem com ID " + triagemId + " não encontrada."));

        DocumentoPendente documento = triagem.getDocumentosPendentes().stream()
                .filter(d -> d.getDocumentoCatalogoId().equals(documentoCatalogoId))
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Documento com ID de catálogo " + documentoCatalogoId + " não encontrado para a triagem " + triagemId
                ));

        documento.setStatus(status);
        documento.setObservacao(observacao);
        if (urlDocumento != null && !urlDocumento.isBlank()) {
            documento.setUrlDocumento(urlDocumento);
        }

        Triagem atualizada = triagemRepository.save(triagem);
        return triagemMapper.toResponseDTO(atualizada);
    }

    public LocalDateTime calcularHorarioInicioEstimado(Triagem newTriagem) {
        List<Triagem> aguardandoTriagens = triagemRepository.findByStatus(StatusTriagem.AGUARDANDO);
        List<Triagem> filaSimulada = new ArrayList<>(aguardandoTriagens);
        filaSimulada.add(newTriagem); // Adiciona a triagem real que está sendo criada

        filaSimulada.sort((t1, t2) -> {
            // Null checks para prioridade (embora @NotNull na entidade ajude, defensivo aqui)
            Integer p1 = t1.getPrioridade() != null ? t1.getPrioridade() : Integer.MAX_VALUE;
            Integer p2 = t2.getPrioridade() != null ? t2.getPrioridade() : Integer.MAX_VALUE;
            int priorityComparison = Integer.compare(p1, p2);
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            // Null checks para horarioSolicitacao
            LocalDateTime hs1 = t1.getHorarioSolicitacao();
            LocalDateTime hs2 = t2.getHorarioSolicitacao();

            if (hs1 == null && hs2 == null) return 0;
            if (hs1 == null) return 1; // Coloca nulos no final da ordem
            if (hs2 == null) return -1; // Coloca nulos no final da ordem

            return hs1.compareTo(hs2);
        });

        LocalDateTime horarioAtualDeProcessamento = LocalDateTime.now();

        for (Triagem t : filaSimulada) {
            // Null check para horarioSolicitacao
            LocalDateTime solicitacaoParaCalculo = t.getHorarioSolicitacao();
            if (solicitacaoParaCalculo == null) {
                solicitacaoParaCalculo = LocalDateTime.now(); // Assume "agora" se for nulo
            }

            LocalDateTime inicioDesteItem = horarioAtualDeProcessamento.isBefore(solicitacaoParaCalculo) ? solicitacaoParaCalculo : horarioAtualDeProcessamento;

            // COMPARA COM A TRIAGEM REAL PASSADA COMO PARÂMETRO
            if (t.equals(newTriagem)) {
                return inicioDesteItem;
            }

            // Null check para tempoEstimadoMinutos
            Integer duracaoParaCalculo = t.getTempoEstimadoMinutos();
            if (duracaoParaCalculo == null) {
                duracaoParaCalculo = 0; // Assume 0 se for nulo
            }
            horarioAtualDeProcessamento = inicioDesteItem.plusMinutes(duracaoParaCalculo);
        }

        return LocalDateTime.now(); // Fallback
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

    @Async // Executa em um thread separado
    @Transactional // Garante que as operações de banco de dados são atômicas
    public void recalcularHorariosEstimadosDaFila() {
        System.out.println("Iniciando recálculo assíncrono da fila...");

        List<Triagem> aguardandoTriagens = triagemRepository.findByStatus(StatusTriagem.AGUARDANDO);

        aguardandoTriagens.sort((t1, t2) -> {
            // Null checks para prioridade
            Integer p1 = t1.getPrioridade() != null ? t1.getPrioridade() : Integer.MAX_VALUE;
            Integer p2 = t2.getPrioridade() != null ? t2.getPrioridade() : Integer.MAX_VALUE;
            int priorityComparison = Integer.compare(p1, p2);
            if (priorityComparison != 0) {
                return priorityComparison;
            }

            // Null checks para horarioSolicitacao
            LocalDateTime hs1 = t1.getHorarioSolicitacao();
            LocalDateTime hs2 = t2.getHorarioSolicitacao();

            if (hs1 == null && hs2 == null) return 0;
            if (hs1 == null) return 1;
            if (hs2 == null) return -1;

            return hs1.compareTo(hs2);
        });

        LocalDateTime horarioAtualDeProcessamento = LocalDateTime.now();
        List<Triagem> triagensParaAtualizar = new ArrayList<>();

        for (Triagem triagem : aguardandoTriagens) {
            // Null check para horarioSolicitacao
            LocalDateTime solicitacaoParaCalculo = triagem.getHorarioSolicitacao();
            if (solicitacaoParaCalculo == null) {
                solicitacaoParaCalculo = LocalDateTime.now();
            }

            LocalDateTime inicioDesteItem = horarioAtualDeProcessamento.isBefore(solicitacaoParaCalculo) ? solicitacaoParaCalculo : horarioAtualDeProcessamento;

            // Null check para tempoEstimadoMinutos
            Integer duracaoParaCalculo = triagem.getTempoEstimadoMinutos();
            if (duracaoParaCalculo == null) {
                duracaoParaCalculo = 0;
            }

            LocalDateTime novoHorarioEstimadoAtendimento = inicioDesteItem.plusMinutes(duracaoParaCalculo);

            // Null check para triagem.getHorarioEstimadoAtendimento() antes de comparar
            LocalDateTime currentEstimated = triagem.getHorarioEstimadoAtendimento();

            if (currentEstimated == null || !currentEstimated.equals(novoHorarioEstimadoAtendimento)) {
                triagem.setHorarioEstimadoAtendimento(novoHorarioEstimadoAtendimento);
                triagensParaAtualizar.add(triagem);
            }

            horarioAtualDeProcessamento = novoHorarioEstimadoAtendimento;
        }

        if (!triagensParaAtualizar.isEmpty()) {
            System.out.println("Salvando " + triagensParaAtualizar.size() + " triagens com horários atualizados.");
            triagemRepository.saveAll(triagensParaAtualizar);
        } else {
            System.out.println("Nenhum horário de triagem alterado, nada a salvar.");
        }
        System.out.println("Recálculo assíncrono da fila concluído.");
    }


    @Transactional
    public void atualizarStatusDocumentoTriagem(UUID triagemId, UUID documentoCatalogoId, DocumentoStatusUpdateRequestDTO requestDTO) {
        // 1. Encontrar o DocumentoPendente específico para esta triagem e tipo de documento
        DocumentoPendente documentoPendente = documentoPendenteRepository
            .findByTriagem_IdAndDocumentoCatalogoId(triagemId, documentoCatalogoId) // Usando findByTriagem_IdAndDocumentoCatalogoId
            .orElseThrow(() -> new RuntimeException("Documento pendente não encontrado para Triagem ID: " + triagemId + " e Documento Catalogo ID: " + documentoCatalogoId));

        // 2. Atualizar os campos do DocumentoPendente
        documentoPendente.setStatus(requestDTO.status());
        documentoPendente.setUrlDocumento(requestDTO.urlVisualizacao()); // Usando setUrlVisualizacao
        documentoPendente.setObservacao(requestDTO.observacaoValidacao());
        documentoPendenteRepository.save(documentoPendente);
    }

    public List<TriagemResponseDTO> listarTodasTriagens() {
        return triagemRepository.findAll()
                .stream()
                .map(this::converteParaDTO)
                .collect(Collectors.toList());
    }

    private TriagemResponseDTO converteParaDTO(Triagem triagem) {
        List<DocumentoPendenteResponseDTO> documentos = triagem.getDocumentosPendentes()
                .stream()
                .map(doc -> new DocumentoPendenteResponseDTO(
                        doc.getId(),
                        doc.getDocumentoCatalogoId(),
                        doc.getNomeDocumentoSnapshot(),
                        doc.getStatus(),
                        doc.getObservacao(),
                        doc.getUrlDocumento()
                        ))
                .collect(Collectors.toList());

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
                triagem.getPrioridade(),
                documentos
        );
    }
}