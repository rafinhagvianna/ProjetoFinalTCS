package com.microsservicos.triagem.mapper;

import com.microsservicos.triagem.dto.DocumentoPendenteResponseDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.model.DocumentoPendente;
import com.microsservicos.triagem.model.Triagem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class TriagemMapper {

    public TriagemResponseDTO toResponseDTO(Triagem triagem) {
        List<DocumentoPendenteResponseDTO> documentosDTO = Optional.ofNullable(triagem.getDocumentosPendentes())
                .orElseGet(Collections::emptyList) // Garante que a lista não seja nula
                .stream()
                .map(this::toDocumentoPendenteResponseDTO)
                .toList();

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
                documentosDTO
        );
    }

    public DocumentoPendenteResponseDTO toDocumentoPendenteResponseDTO(DocumentoPendente doc) {
        return new DocumentoPendenteResponseDTO(
                doc.getId(),
                doc.getDocumentoCatalogoId(),
                doc.getNomeDocumentoSnapshot(),
                doc.getStatus(),
                doc.getObservacao(),
                doc.getUrlDocumento() // Adicionado o URL do documento aqui
        );
    }
    // Métodos para converter DTO para Entidade podem ser adicionados se necessário
}