package com.microsservicos.triagem.exception; // Mude para o pacote desejado, ex: com.microsservicos.triagem.handler

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para controladores REST.
 * Esta classe intercepta exceções lançadas pelos controllers e serviços,
 * retornando respostas padronizadas e amigáveis ao cliente.
 */
@ControllerAdvice // Indica que esta classe é um componente que lida com exceções de controllers globalmente
public class GlobalExceptionHandler {

    /**
     * Define uma estrutura de resposta padronizada para erros.
     */
    public record ErrorResponse(
            int status,
            String error,
            String message,
            LocalDateTime timestamp
    ) {}

    /**
     * Manipula exceções de validação de argumentos de método (@Valid).
     * Retorna um status HTTP 400 (Bad Request) com detalhes dos erros de validação.
     *
     * @param ex A exceção MethodArgumentNotValidException.
     * @return ResponseEntity contendo um mapa de erros e status 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Manipula exceções do tipo RecursoNaoEncontradoException.
     * Retorna um status HTTP 404 (Not Found).
     *
     * @param ex A exceção RecursoNaoEncontradoException.
     * @return ResponseEntity contendo a resposta de erro padronizada e status 404.
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNaoEncontradoException(RecursoNaoEncontradoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(), // "Not Found"
                ex.getMessage(), // Mensagem da exceção, ex: "Nenhuma triagem aguardando na fila."
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Manipula exceções do tipo ComunicacaoServicoException (falhas de integração).
     * Retorna um status HTTP 503 (Service Unavailable).
     *
     * @param ex A exceção ComunicacaoServicoException.
     * @return ResponseEntity contendo a resposta de erro padronizada e status 503.
     */
    @ExceptionHandler(ComunicacaoServicoException.class)
    public ResponseEntity<ErrorResponse> handleComunicacaoServicoException(ComunicacaoServicoException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), // "Service Unavailable"
                "Falha na comunicação com serviço externo: " + ex.getMessage(), // Mensagem mais detalhada
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Manipula todas as outras exceções não especificadas.
     * Retorna um status HTTP 500 (Internal Server Error) e uma mensagem genérica,
     * evitando expor detalhes internos do servidor.
     *
     * @param ex A exceção genérica.
     * @return ResponseEntity contendo a resposta de erro padronizada e status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        // Para depuração, você pode logar o stack trace completo aqui:
        // logger.error("Ocorreu um erro interno: " + ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), // "Internal Server Error"
                "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.\nErro: " + ex.getMessage(), // Mensagem genérica para o cliente
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<String> handleRegraDeNegocio(RegraDeNegocioException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}