package com.microsservicos.Catalogo_Service.controller;

import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.model.Setor;
import com.microsservicos.Catalogo_Service.service.SetorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setor")
//@RequiredArgsConstructor
public class SetorController {

    private final SetorService setorService;

//    @Autowired // Or use this annotation
    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }


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
    @ResponseStatus(HttpStatus.LOCKED)
    public List<SetorResponse> buscarSetorInativo(){
        return setorService.buscarSetorInativo();
    }

    @GetMapping("/status")
    @ResponseStatus(HttpStatus.OK)
    public boolean buscarSetorStatus(@RequestParam("nome") String nome){
        return setorService.retornarStatusSetor(nome);
    }

    @PatchMapping("{id}") // Atualiza apenas os campos informados no corpo da requisição, e não o registro inteiro, como o PUT;
    @ResponseStatus(HttpStatus.OK)
    public SetorResponse atualizarSetor(@PathVariable String id, @RequestBody SetorRequest setorRequest){
        return setorService.atualizarSetor(id, setorRequest);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerSetor(@PathVariable String id){
        setorService.removerSetor(id);
    }

}
