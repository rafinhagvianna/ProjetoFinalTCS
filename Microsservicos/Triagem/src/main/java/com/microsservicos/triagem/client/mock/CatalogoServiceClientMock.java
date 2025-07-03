package com.microsservicos.triagem.client.mock;

import com.microsservicos.triagem.client.CatalogoServiceClient;
import com.microsservicos.triagem.client.TipoDocumentoResponse;
import com.microsservicos.triagem.client.ServicoResponse; // Se estiver usando

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Component // Torna esta classe um componente Spring
@Profile("dev") // Ativa este mock apenas quando o perfil 'dev' estiver ativo
public class CatalogoServiceClientMock implements CatalogoServiceClient {

    // Dados mockados para simular o catálogo de serviços e documentos
    private final Map<Long, String> servicoNomes = new HashMap<>();
    private final Map<Long, List<TipoDocumentoResponse>> documentosPorServico = new HashMap<>();

    public CatalogoServiceClientMock() {
        // Mock de nomes de serviços
        servicoNomes.put(101L, "Abertura de Conta Corrente");
        servicoNomes.put(102L, "Solicitação de Empréstimo");
        servicoNomes.put(103L, "Cadastro de Cliente");
        servicoNomes.put(104L, "Atualização de Cadastro");


        // Mock de documentos necessários por serviço
        documentosPorServico.put(101L, Arrays.asList(
                new TipoDocumentoResponse(1L, "RG"),
                new TipoDocumentoResponse(2L, "CPF"),
                new TipoDocumentoResponse(3L, "Comprovante de Residência")
        ));
        documentosPorServico.put(102L, Arrays.asList(
                new TipoDocumentoResponse(1L, "RG"),
                new TipoDocumentoResponse(2L, "CPF"),
                new TipoDocumentoResponse(4L, "Comprovante de Renda")
        ));
        documentosPorServico.put(103L, Arrays.asList(
                new TipoDocumentoResponse(1L, "RG"),
                new TipoDocumentoResponse(2L, "CPF")
        ));
        // Para serviços sem documentos ou outros
        documentosPorServico.put(104L, Arrays.asList(
                new TipoDocumentoResponse(1L, "RG"),
                new TipoDocumentoResponse(2L, "CPF"),
                new TipoDocumentoResponse(3L, "Comprovante de Residência"),
                new TipoDocumentoResponse(4L, "Comprovante de Renda")
        ));
        documentosPorServico.put(999L, Arrays.asList()); // Exemplo de serviço sem documentos
    }

    @Override
    public List<TipoDocumentoResponse> getDocumentosNecessariosParaServico(Long servicoId) {
        System.out.println("DEBUG: Usando CatalogoServiceClientMock - getDocumentosNecessariosParaServico para ID: " + servicoId);
        return documentosPorServico.getOrDefault(servicoId, Arrays.asList());
    }

    @Override
    public String getNomeServico(Long servicoId) {
        System.out.println("DEBUG: Usando CatalogoServiceClientMock - getNomeServico para ID: " + servicoId);
        return servicoNomes.getOrDefault(servicoId, "Serviço Desconhecido [MOCK]");
    }

    // Se você tiver outros métodos na interface CatalogoServiceClient, implemente-os aqui
    // Ex: @Override
    // public ServicoResponse getServicoById(Long servicoId) {
    //     System.out.println("DEBUG: Usando CatalogoServiceClientMock - getServicoById para ID: " + servicoId);
    //     String nome = servicoNomes.getOrDefault(servicoId, "Serviço Desconhecido [MOCK]");
    //     return new ServicoResponse(servicoId, nome, "Descrição Mockada");
    // }
}