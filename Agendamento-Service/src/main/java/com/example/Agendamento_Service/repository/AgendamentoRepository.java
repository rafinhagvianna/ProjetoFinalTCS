package com.example.Agendamento_Service.repository;

import com.example.Agendamento_Service.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
}
