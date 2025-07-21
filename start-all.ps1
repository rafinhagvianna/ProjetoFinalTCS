function Start-ServiceAsync {
    param (
        [string]$ServicePath,
        [string]$ServiceName
    )
    Write-Host "Iniciando $ServiceName..."
    # Usa Start-Process para rodar o comando em uma nova janela ou em segundo plano
    # O -NoNewWindow pode ser removido se você quiser ver a saída em uma nova janela de console para cada serviço
    Start-Process -FilePath "$env:M2_HOME\bin\mvn.cmd" -ArgumentList "spring-boot:run" -WorkingDirectory $ServicePath -NoNewWindow
    Start-Sleep -Seconds 5
}

# Iniciar Eureka Server (Naming-Server)
Start-ServiceAsync ".\Naming-Server" "Eureka Server"

# Aguardar Eureka estar totalmente ativo (delay básico)
Write-Host "Aguardando Eureka iniciar completamente..."
Start-Sleep -Seconds 20

# Iniciar API Gateway
Start-ServiceAsync ".\API-Gateway" "API Gateway"

# Iniciar os demais microsserviços
Start-ServiceAsync ".\Microservicos\AgendamentoService" "AgendamentoService"
Start-ServiceAsync ".\Microservicos\AuthService" "AuthService"
Start-ServiceAsync ".\Microservicos\CadastroClienteService" "CadastroClienteService"
Start-ServiceAsync ".\Microservicos\CadastroFuncionarioService" "CadastroFuncionarioService"
Start-ServiceAsync ".\Microservicos\CatalogoService" "CatalogoService"
Start-ServiceAsync ".\Microservicos\DocumentacaoService" "DocumentacaoService"
Start-ServiceAsync ".\Microservicos\TarefasFuncionario-Service" "TarefasFuncionario-Service"
Start-ServiceAsync ".\Microservicos\TriagemService" "TriagemService"

Write-Host "Todos os serviços foram iniciados."