package com.microsservicos.Catalogo_Service.controller;

import com.microsservicos.Catalogo_Service.dto.AlterarStatusRequest;
import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.service.SetorService;
import lombok.RequiredArgsConstructor; // Certifique-se de ter o Lombok no seu pom.xml
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/setor")
 // Usando Lombok para injeção de dependência via construtor
public class SetorController {

    private final SetorService setorService;

    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    // Se estiver usando @RequiredArgsConstructor, você não precisa mais de um construtor explícito
    // Mas se não usar Lombok, mantenha o @Autowired ou o construtor manual como você tinha.
    // public SetorController(SetorService setorService) {
    //     this.setorService = setorService;
    // }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SetorResponse criarSetor(@RequestBody SetorRequest setorRequest){
        return setorService.criarSetor(setorRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SetorResponse> listarSetores(){
        return setorService.listarSetores();
    }

    @GetMapping("/ativos")
    @ResponseStatus(HttpStatus.OK)
    public List<SetorResponse> buscarSetorAtivo(){
        return setorService.buscarSetorAtivo();
    }

    @GetMapping("/inativos")
    @ResponseStatus(HttpStatus.OK) // Alterado para OK, 200 é mais comum para retorno de lista, mesmo que inativa. LOCKED (423) é mais para recurso bloqueado.
    public List<SetorResponse> buscarSetorInativo(){
        return setorService.buscarSetorInativo();
    }

    @GetMapping("/{id}") // Adicione este método para buscar setor por ID
    @ResponseStatus(HttpStatus.OK)
    public SetorResponse buscarSetorPorId(@PathVariable UUID id) {
        return setorService.buscarSetorPorId(id);
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public boolean buscarSetorStatus(@RequestParam("nome") String nome){
        return setorService.retornarStatusSetor(nome);
    }

    @PutMapping("/{id}") // Recomendo PUT se o DTO sempre enviar todos os campos para atualização completa
    @ResponseStatus(HttpStatus.OK)
    public SetorResponse atualizarSetor(@PathVariable UUID id, @RequestBody SetorRequest setorRequest){
        return setorService.atualizarSetor(id, setorRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSetor(@PathVariable UUID id){
        setorService.removerSetor(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SetorResponse> alterarStatusSetor(
            @PathVariable UUID id,
            @RequestBody AlterarStatusRequest.AlteraStatusRequest request // Ensure this DTO only contains `isAtivo`
    ) {
        SetorResponse setorAtualizado = setorService.alterarStatus(id, request.isAtivo());
        return ResponseEntity.ok(setorAtualizado);
    }
}