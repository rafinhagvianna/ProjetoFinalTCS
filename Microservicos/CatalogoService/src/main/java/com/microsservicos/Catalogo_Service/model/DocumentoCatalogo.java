// Location: src/main/java/com/microsservicos/Catalogo_Service/model/DocumentoCatalogo.java

package com.microsservicos.Catalogo_Service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;

@Document(value = "documento_catalogo") // Name your collection appropriately
public class DocumentoCatalogo {

    @Id
    private UUID id;
    private String nome;        // E.g., "RG", "CPF", "Comprovante de Residência"
    private String descricao;   // E.g., "Documento de identidade com foto"
    private boolean isObrigatorioPorPadrao; // If this document is generally mandatory
    private boolean isAtivo;    // If the document type is currently in use

    public DocumentoCatalogo() {
        this.id = UUID.randomUUID(); // Auto-generate ID for new documents
    }

    public DocumentoCatalogo(String nome, String descricao, boolean isObrigatorioPorPadrao, boolean isAtivo) {
        this(); // Call default constructor to set ID
        this.nome = nome;
        this.descricao = descricao;
        this.isObrigatorioPorPadrao = isObrigatorioPorPadrao;
        this.isAtivo = isAtivo;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isObrigatorioPorPadrao() { return isObrigatorioPorPadrao; }
    public void setObrigatorioPorPadrao(boolean obrigatorioPorPadrao) { isObrigatorioPorPadrao = obrigatorioPorPadrao; }
    public boolean isAtivo() { return isAtivo; }
    public void setAtivo(boolean ativo) { isAtivo = ativo; }
}