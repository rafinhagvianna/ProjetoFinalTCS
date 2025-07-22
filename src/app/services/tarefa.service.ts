import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

// 1. Adicione a propriedade 'rota' aqui
export interface Tarefa {
  nome: string;
  descricao: string;
  rota: string; // <-- ADICIONAR ESTA LINHA
}

@Injectable({
  providedIn: 'root'
})
export class TarefaService {

  // 2. Certifique-se de que os dados retornados incluam a rota
  private tarefas: Tarefa[] = [
    {
      nome: 'Cadastrar Novo Funcionário',
      descricao: 'Adicionar novos usuários do tipo funcionário ao sistema.',
      rota: '/menu-funcionario/register' 
    },
    {
      nome: 'Consultar Agendamentos',
      descricao: 'Visualizar a lista de agendamentos pendentes e confirmados.',
      rota: '/menu-funcionario/agendamentos' 
    },
    {
      nome: 'Acompanhamento de Triagens',
      descricao: 'Visualizar filas e status das triagens em andamento.',
      rota: '/menu-funcionario/triagens' // trocar essa rota para a de triagens.
    },

    // {
    //   nome: 'Verificação de Documentos',
    //   descricao: 'Analisar de validar os documentos dos clientes.',
    //    rota: '/menu-funcionario/verificar-documentos' // trocar essa rota para a de triagens.
    // },
    {
      nome: 'Gerenciar setores',
      descricao: 'Adicione novos setores ou exclua do sistema.',
      rota: '/menu-funcionario/cadastro-setor' // trocar essa rota para a de triagens.
    }

    
  ];

  getTarefas(): Observable<Tarefa[]> {
    return of(this.tarefas);
  }
}