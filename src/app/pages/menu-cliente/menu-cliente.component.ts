import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Router, RouterLink } from '@angular/router';
import { CatalogoApiService, ServicoBackend, TriagemResponse } from '../../services/catalogo-api.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../components/confirmationmodal/confirmationmodal';
import { Triagem, TriagemApiService } from '../../services/triagem-api.service';
import Swal from 'sweetalert2';
import { AgendamentoCompleto, AgendamentoApiService } from '../../services/agendamento-api.service';
interface ServicoDisplay extends ServicoBackend {
  rota: string;
}

@Component({
  selector: 'app-menu-cliente',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterLink],
  templateUrl: './menu-cliente.component.html',
  styleUrls: ['./menu-cliente.component.scss']
})
export class MenuClienteComponent implements OnInit {
  setores: ServicoDisplay[] = [];
  private setorParaConfirmar: ServicoDisplay | null = null;

  public triagemAtiva: Triagem | null = null;
  public agendamentoAtivo: AgendamentoCompleto | null = null;

  constructor(
    private router: Router,
    private catalogoApiService: CatalogoApiService,
    private triagemApiService: TriagemApiService,
    private agendamentoApiService: AgendamentoApiService,
    private modalService: NgbModal
  ) { }

  ngOnInit(): void {
    // 👇 5. ATUALIZE O ngOnInit PARA VERIFICAR A TRIAGEM PRIMEIRO
    // Primeiro, verifica se há uma triagem ativa
    this.triagemApiService.verificarTriagemAtiva().subscribe(triagem => {
      this.triagemAtiva = triagem;
      // Depois de verificar, carrega os serviços normais
      if (!this.triagemAtiva) {
        this.agendamentoApiService.verificarAgendamentoAtivo().subscribe(agendamento => {
          this.agendamentoAtivo = agendamento;
        });
      }
      this.carregarServicos();
    });
  }

  verDetalhesAgendamento(): void {
    if (this.agendamentoAtivo) {
        // Leva para a tela de upload, que já mostra os detalhes do agendamento
        this.router.navigate(['/documentos/upload'], { 
            queryParams: { agendamentoId: this.agendamentoAtivo.id }
        });
    }
  }

  carregarServicos(): void {
    this.catalogoApiService.getTodosOsServicos().subscribe({
      next: (servicosBackend: ServicoBackend[]) => {
        this.setores = servicosBackend.map(servico => this.mapServicoToDisplay(servico));
        console.log('Serviços carregados do backend e mapeados para exibição:', this.setores);
      },
      error: (err) => {
        console.error('Erro ao carregar serviços do backend:', err);
        alert('Não foi possível carregar os serviços. Por favor, tente novamente mais tarde.');
        this.setores = [];
      }
    });
  }

  private mapServicoToDisplay(servico: ServicoBackend): ServicoDisplay {
    let rota = '/espera';

    if (servico.tempoMedioMinutos >= 15) {
      rota = '/agendamento';
    }
    return {
      ...servico,
      rota: rota
    };
  }

  confirmarSelecao(setor: ServicoDisplay): void {
    
    if (this.triagemAtiva) {
      // Em vez de abrir o modal de confirmação, abrimos um alerta informativo.
      Swal.fire({
        title: 'Você já está em uma Fila!',
        html: `Você já tem um atendimento em andamento para <strong>${this.triagemAtiva.nomeServicoSnapshot}</strong>.<br><br>Cancele o atendimento atual antes de iniciar um novo.`,
        icon: 'info',
        showCancelButton: true,
        confirmButtonText: 'Ir para meu Atendimento',
        cancelButtonText: 'Entendi',
        confirmButtonColor: '#0d6efd',
        cancelButtonColor: '#6c757d'
      }).then((result) => {
        // Se o cliente clicar em "Ir para meu Atendimento", nós o redirecionamos.
        if (result.isConfirmed) {
          this.retornarParaEspera(); // Reutilizamos a função que já navega para a tela de espera!
        }
      });

      // Importante: paramos a execução da função aqui para não continuar com o fluxo de criação.
      return; 
    }

    this.setorParaConfirmar = setor;
    let horario: TriagemResponse;
    this.triagemApiService.getHorarioDisponivel().subscribe((response: TriagemResponse) => {
      horario = response;
      const horaFormatada = new Intl.DateTimeFormat('pt-BR', {
        hour: '2-digit',
        minute: '2-digit',
      }).format(new Date(horario.disponibilidade));

      const modalRef = this.modalService.open(ConfirmationModalComponent, { centered: true });

      modalRef.componentInstance.title = 'Confirmação de Ação';
      modalRef.componentInstance.message = `Você deseja realmente solicitar atendimento para: <strong>${setor.nome}</strong>?`;
      if(setor.rota =='/espera'){
        modalRef.componentInstance.message += `<br>Provável horario de atendimento às: <strong>${horaFormatada}</strong>`;
      }
      modalRef.componentInstance.confirmButtonText = 'Sim, continuar';

      modalRef.result.then(
        (result) => {
          if (result === true && this.setorParaConfirmar) {
            console.log(`Usuário confirmou a seleção do setor: ${this.setorParaConfirmar.nome}`);
            this.executarSelecaoSetor(this.setorParaConfirmar);
          } else {
            console.log(`Usuário cancelou a seleção do setor: ${this.setorParaConfirmar?.nome || 'Nenhum setor'}`);
          }
          this.setorParaConfirmar = null;
        },
        (reason) => {
          console.log(`Modal fechado por: ${reason}. Ação cancelada.`);
          this.setorParaConfirmar = null;
        }
      );
    });
  }

  private executarSelecaoSetor(setor: ServicoDisplay): void {
  if (setor.rota === '/agendamento') {
    this.router.navigate([setor.rota, setor.id], { queryParams: { tempo: setor.tempoMedioMinutos } });
  } else {
    const triagemData = {
      servicoId: setor.id,
      prioridade: 1
    };
    this.triagemApiService.salvarTriagem(triagemData).subscribe({
      // MUDANÇA AQUI: Renomeie 'response' para 'triagemCriada' para clareza
      next: (triagemCriada) => {
        console.log('Triagem criada, redirecionando para a tela de espera com o ID:', triagemCriada.id);

        // A NAVEGAÇÃO AGORA USA O ID DA TRIAGEM CRIADA
        // O Angular vai gerar uma URL como: /espera/SEU_ID_AQUI
        this.router.navigate(['/espera', triagemCriada.id]);
      },
      error: error => {
        console.error('Erro ao criar triagem:', error);
        const msg = error.error?.message
          ? `Erro ao entrar na fila: ${error.error.message}`
          : 'Erro ao entrar na fila. Tente novamente.';
        alert(msg);
      }
    });
  }
}

retornarParaEspera(): void {
  if (this.triagemAtiva) {
    // Navega para a rota de espera usando o ID da triagem ativa
    this.router.navigate(['/espera', this.triagemAtiva.id]);
  }
}
}