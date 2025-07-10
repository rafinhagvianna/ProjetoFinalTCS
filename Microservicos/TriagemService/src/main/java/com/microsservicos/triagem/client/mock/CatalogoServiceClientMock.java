package com.microsservicos.triagem.client.mock;

import com.microsservicos.triagem.client.CatalogoServiceClient;
import com.microsservicos.triagem.client.DocumentoCatalogoResponse;
import com.microsservicos.triagem.client.SetorResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.UUID;

@Component
// @Profile("dev")
public class CatalogoServiceClientMock {//implements CatalogoServiceClient {

    // // 1. Defina UUIDs fixos para seus serviços mockados - CORRIGIDOS PARA 12 DÍGITOS NO ÚLTIMO SEGMENTO
    // public static final UUID SERVICO_CONTA_CORRENTE_ID = UUID.fromString("c0a1b2c3-d4e5-6789-0123-456789abcdef");
    // public static final UUID SERVICO_EMPRESTIMO_ID = UUID.fromString("e0a1b2c3-d4e5-6789-0123-abcdeffedcba"); // Ajustado
    // public static final UUID SERVICO_CADASTRO_CLIENTE_ID = UUID.fromString("f0a1b2c3-d4e5-6789-0123-1234567890ab"); // Ajustado
    // public static final UUID SERVICO_ATUALIZACAO_CADASTRO_ID = UUID.fromString("0a1b2c3d-4e5f-6789-0123-fedcba987654"); // Ajustado
    // public static final UUID SERVICO_SEM_DOCUMENTOS_ID = UUID.fromString("1a1b2c3d-4e5f-6789-0123-abcdef123456"); // Ajustado

    // // 2. Defina UUIDs fixos para seus documentos mockados - CORRIGIDOS PARA 12 DÍGITOS NO ÚLTIMO SEGMENTO
    // public static final UUID DOC_RG_ID = UUID.fromString("2a1b2c3d-4e5f-6789-0123-111122223333"); // Ajustado
    // public static final UUID DOC_CPF_ID = UUID.fromString("3a1b2c3d-4e5f-6789-0123-444455556666"); // Ajustado
    // public static final UUID DOC_COMPROVANTE_RESIDENCIA_ID = UUID.fromString("4a1b2c3d-4e5f-6789-0123-777788889999"); // Ajustado
    // public static final UUID DOC_COMPROVANTE_RENDA_ID = UUID.fromString("5a1b2c3d-4e5f-6789-0123-aaaabbbbcccc"); // Ajustado


    // private final Map<UUID, String> servicoNomes = new HashMap<>();
    // private final Map<UUID, Integer> servicoDuracoes = new HashMap<>();
    // private final Map<UUID, List<DocumentoCatalogoResponse>> documentosPorServico = new HashMap<>();

    // public CatalogoServiceClientMock() {
    //     servicoNomes.put(SERVICO_CONTA_CORRENTE_ID, "Abertura de Conta Corrente");
    //     servicoNomes.put(SERVICO_EMPRESTIMO_ID, "Solicitação de Empréstimo");
    //     servicoNomes.put(SERVICO_CADASTRO_CLIENTE_ID, "Cadastro de Cliente");
    //     servicoNomes.put(SERVICO_ATUALIZACAO_CADASTRO_ID, "Atualização de Cadastro");

    //     servicoDuracoes.put(SERVICO_CONTA_CORRENTE_ID, 30);
    //     servicoDuracoes.put(SERVICO_EMPRESTIMO_ID, 45);
    //     servicoDuracoes.put(SERVICO_CADASTRO_CLIENTE_ID, 20);
    //     servicoDuracoes.put(SERVICO_ATUALIZACAO_CADASTRO_ID, 15);

    //     documentosPorServico.put(SERVICO_CONTA_CORRENTE_ID, Arrays.asList(
    //             new DocumentoCatalogoResponse(DOC_RG_ID, "RG"),
    //             new DocumentoCatalogoResponse(DOC_CPF_ID, "CPF"),
    //             new DocumentoCatalogoResponse(DOC_COMPROVANTE_RESIDENCIA_ID, "Comprovante de Residência")
    //     ));
    //     documentosPorServico.put(SERVICO_EMPRESTIMO_ID, Arrays.asList(
    //             new DocumentoCatalogoResponse(DOC_RG_ID, "RG"),
    //             new DocumentoCatalogoResponse(DOC_CPF_ID, "CPF"),
    //             new DocumentoCatalogoResponse(DOC_COMPROVANTE_RENDA_ID, "Comprovante de Renda")
    //     ));
    //     documentosPorServico.put(SERVICO_CADASTRO_CLIENTE_ID, Arrays.asList(
    //             new DocumentoCatalogoResponse(DOC_RG_ID, "RG"),
    //             new DocumentoCatalogoResponse(DOC_CPF_ID, "CPF")
    //     ));
    //     documentosPorServico.put(SERVICO_ATUALIZACAO_CADASTRO_ID, Arrays.asList(
    //             new DocumentoCatalogoResponse(DOC_RG_ID, "RG"),
    //             new DocumentoCatalogoResponse(DOC_CPF_ID, "CPF"),
    //             new DocumentoCatalogoResponse(DOC_COMPROVANTE_RESIDENCIA_ID, "Comprovante de Residência"),
    //             new DocumentoCatalogoResponse(DOC_COMPROVANTE_RENDA_ID, "Comprovante de Renda")
    //     ));
    //     documentosPorServico.put(SERVICO_SEM_DOCUMENTOS_ID, Arrays.asList());
    // }

    // @Override
    // public List<DocumentoCatalogoResponse> getDocumentosNecessariosParaServico(UUID servicoId) {
    //     System.out.println("DEBUG: Usando CatalogoServiceClientMock - getDocumentosNecessariosParaServico para ID: " + servicoId);
    //     return documentosPorServico.getOrDefault(servicoId, Collections.emptyList());
    // }

    // @Override
    // public String getNomeServico(UUID servicoId) {
    //     System.out.println("DEBUG: Usando CatalogoServiceClientMock - getNomeServico para ID: " + servicoId);
    //     return servicoNomes.getOrDefault(servicoId, "Serviço Desconhecido [MOCK]");
    // }

    // @Override
    // public SetorResponse getServicoById(UUID servicoId) {
    //     System.out.println("DEBUG: Usando CatalogoServiceClientMock - getServicoById para ID: " + servicoId);
    //     String nome = servicoNomes.getOrDefault(servicoId, "Serviço Desconhecido [MOCK]");
    //     Integer duracao = servicoDuracoes.getOrDefault(servicoId, 0);
    //     return new SetorResponse(servicoId, nome, duracao);
    // }
}