package com.microservicos.authservice.controller; // Ajuste o pacote

import com.microservicos.authservice.dto.LoginRequestDTO;
import com.microservicos.authservice.dto.LoginResponseDTO;
import com.microservicos.authservice.dto.TokenValidationResponseDTO;
import com.microservicos.authservice.exception.InvalidCredentialsException;
import com.microservicos.authservice.security.CustomUserDetails;
import com.microservicos.authservice.service.AuthService; // Supondo que você tenha um AuthService

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth") // Prefixo para todos os endpoints deste controlador
public class AuthController {

    private final AuthService authService;

    // Construtor para injeção de dependências
    // @Autowired é opcional a partir do Spring 4.3 se houver apenas um construtor
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            // Retorna 401 Unauthorized para credenciais inválidas
            // Certifique-se que LoginResponseDTO tenha um construtor compatível com String e nulls
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDTO(e.getMessage(), null,  null, null, null));
        } catch (Exception e) {
            // Para outros erros internos
            System.err.println("Erro interno no login: " + e.getMessage()); // Loga o erro
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDTO("Erro interno no servidor.", null,   null, null, null));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponseDTO> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                TokenValidationResponseDTO response = new TokenValidationResponseDTO(
                        "Token JWT válido!",
                        userDetails.getUserId(),
                        userDetails.getFullName(),
                        userDetails.getEmail(),
                        userDetails.getRole()
                );
                return ResponseEntity.ok(response);
            } else if (principal instanceof UserDetails) {
                // Fallback se não for CustomUserDetails (mas deveria ser com a nova config)
                UserDetails userDetails = (UserDetails) principal;
                return ResponseEntity.ok(new TokenValidationResponseDTO(
                        "Token JWT válido! Usuário: " + userDetails.getUsername(),
                        null, // ID pode não estar disponível aqui
                        null, // Full name pode não estar disponível aqui
                        userDetails.getUsername(), // Assumindo email como username
                        null // Role pode não estar disponível diretamente
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponseDTO(
                        "Token JWT válido, mas detalhes do usuário não puderam ser extraídos.",
                        null, null, null, null
                ));
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenValidationResponseDTO(
                    "Token JWT inválido ou ausente.",
                    null, null, null, null
            ));
        }
    }

    // Você pode adicionar outros endpoints aqui, como /validate para validação de token, etc.
}