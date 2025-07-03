package com.microsservicos.triagem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "catalogo-service", url = "${clients.catalogo-service.url:}")
public interface CatalogoServiceClient {

    /**
     * Busca a lista de tipos de documentos necessários para um serviço específico.
     * Ex: GET http://catalogo-service/catalogo/servicos/{servicoId}/documentos-necessarios
     *
     * @param servicoId O ID do serviço.
     * @return Uma lista de TipoDocumentoResponse (ID e nome do documento).
     */
    @GetMapping("/catalogo/servicos/{servicoId}/documentos-necessarios")
    List<TipoDocumentoResponse> getDocumentosNecessariosParaServico(@PathVariable("servicoId") Long servicoId);

    /**
     * Busca o nome de um serviço específico.
     * Ex: GET http://catalogo-service/catalogo/servicos/{servicoId}/nome
     * (Este endpoint deve retornar apenas uma String no Catalogo-Service)
     *
     * @param servicoId O ID do serviço.
     * @return O nome do serviço como String.
     */
    @GetMapping("/catalogo/servicos/{servicoId}/nome")
    String getNomeServico(@PathVariable("servicoId") Long servicoId);

    /*
     * Opcional: Se o seu Catalogo-Service tiver um endpoint que retorna o objeto completo
     * ServicoResponse para o ID do serviço.
     * @GetMapping("/catalogo/servicos/{servicoId}")
     * ServicoResponse getServicoById(@PathVariable("servicoId") Long servicoId);
     */
}