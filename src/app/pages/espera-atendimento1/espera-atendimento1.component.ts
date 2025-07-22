import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TriagemApiService, Triagem } from '../../services/triagem-api.service';
import Swal from 'sweetalert2';
import { TriagemCompleta } from '../../services/triagem.interface'; 
import { StatusTriagemFrontend } from '../../services/triagem.interface';

@Component({
  selector: 'app-espera-atendimento',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterLink],
  templateUrl: './espera-atendimento1.component.html',
  styleUrls: ['./espera-atendimento1.component.scss']
})
export class EsperaAtendimentoComponent implements OnInit, OnDestroy {

  public setorSelecionado: string = '';
  public tempoRestante: number = 120; 
  public displayTempo: string = '';
  public isLoading: boolean = true; // MUDANÇA: Adicionamos o estado de loading
  private timerInterval: any;
  private horarioAlvo!: Date;
  public statusTriagem: 'AGUARDANDO' | 'EM_ATENDIMENTO' | 'FINALIZADO' | 'ERRO' = 'AGUARDANDO';

  constructor(private route: ActivatedRoute,  private router: Router,
    private triagemService: TriagemApiService) {}

  ngOnInit(): void {
    // 4. Lógica principal movida para buscar dados pela rota
    const triagemId = this.route.snapshot.paramMap.get('id');

    if (triagemId) {
      this.carregarDadosDaTriagem(triagemId);
    } else {
      console.error('Nenhum ID de triagem encontrado na URL. Redirecionando...');
      this.router.navigate(['/menu-cliente']);
    }
  }
  
  // private carregarDadosDaTriagem(id: string): void {
  //   this.triagemService.getById(id).subscribe({
  //     next: (triagem) => {
  //       this.setorSelecionado = triagem.nomeServicoSnapshot;
        
  //       if (triagem.horarioEstimadoAtendimento) {
  //         // 5. Definimos nosso horário-alvo
  //         this.horarioAlvo = new Date(triagem.horarioEstimadoAtendimento);
  //         this.startTimer(); // Inicia o contador apenas após receber os dados
  //       } else {
  //         this.displayTempo = "Aguarde"; // Fallback caso não haja horário
  //       }
  //       this.isLoading = false;
  //     },
  //     error: (err) => {
  //       console.error("Erro ao buscar dados da triagem", err);
  //       this.displayTempo = "Erro";
  //     }
  //   });
  // }

  private carregarDadosDaTriagem(id: string): void {
    this.triagemService.getById(id).subscribe({
      next: (triagem: TriagemCompleta) => {
        this.setorSelecionado = triagem.nomeServicoSnapshot;
        
        switch (triagem.status) {

          case StatusTriagemFrontend.AGUARDANDO:
            this.statusTriagem = 'AGUARDANDO';
            if (triagem.horarioEstimadoAtendimento) {
              this.horarioAlvo = new Date(triagem.horarioEstimadoAtendimento);
              this.startTimer();
            } else {
              this.displayTempo = "Aguarde";
            }
            break;

          case StatusTriagemFrontend.EM_ATENDIMENTO:
            this.statusTriagem = 'EM_ATENDIMENTO';
            this.displayTempo = "Sua vez!";
            if (this.timerInterval) {
              clearInterval(this.timerInterval);
            }
            break;

          case StatusTriagemFrontend.FINALIZADO:
            this.statusTriagem = 'FINALIZADO';
            this.mostrarMensagemDeConclusao();
            break;
            
          default:
            // Para outros status como CANCELADO, etc., apenas redireciona
            this.router.navigate(['/menu-cliente']);
            break;
        }

        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erro ao buscar dados da triagem", err);
        this.statusTriagem = 'ERRO';
        this.displayTempo = "Erro";
      }
    });
  }

  private mostrarMensagemDeConclusao(): void {
    Swal.fire({
      icon: 'success',
      title: 'Atendimento Concluído!',
      text: 'Obrigado por utilizar o sistema BankFlow!',
      timer: 3000,
      showConfirmButton: false,
      allowOutsideClick: false
    }).then(() => {
      this.router.navigate(['/menu-cliente']);
    });
  }

  private startTimer(): void {
    // Limpa qualquer timer anterior para segurança
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }

    // 6. O timer agora recalcula a diferença a cada segundo
    this.timerInterval = setInterval(() => {
      const agora = new Date();
      const segundosRestantes = (this.horarioAlvo.getTime() - agora.getTime()) / 1000;

      if (segundosRestantes > 0) {
        this.formatTime(segundosRestantes);
      } else {
        clearInterval(this.timerInterval);
        this.displayTempo = "Sua vez!";
      }
    }, 1000);
  }

  // 7. O método de formatação agora recebe os segundos como parâmetro
  private formatTime(totalSegundos: number): void {
    const minutos: number = Math.floor(totalSegundos / 60);
    const segundos: number = Math.floor(totalSegundos % 60);

    const displayMinutos = minutos < 10 ? '0' + minutos : minutos.toString();
    const displaySegundos = segundos < 10 ? '0' + segundos : segundos.toString();

    this.displayTempo = `${displayMinutos}:${displaySegundos}`;
  }

  confirmarCancelamento(): void {
    Swal.fire({
      title: 'Tem certeza?',
      text: "Sua solicitação de atendimento será removida e você sairá da fila.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Sim, cancelar!',
      cancelButtonText: 'Não'
    }).then((result) => {
      if (result.isConfirmed) {
        // Se o usuário confirmou, chama o método que realmente cancela
        this.executarCancelamento();
      }
    });
  }

  private executarCancelamento(): void {
    const triagemId = this.route.snapshot.paramMap.get('id');

    if (triagemId) {
      this.triagemService.cancelarTriagem(triagemId).subscribe({
        next: () => {
          Swal.fire(
            'Cancelado!',
            'Seu atendimento foi cancelado com sucesso.',
            'success'
          );
          // Redireciona para o menu principal após o sucesso
          this.router.navigate(['/menu-cliente']);
        },
        error: (err) => {
          console.error('Erro ao cancelar a triagem', err);
          Swal.fire(
            'Erro!',
            'Não foi possível cancelar o atendimento. Tente novamente.',
            'error'
          );
        }
      });
    } else {
      console.error('Não foi possível encontrar o ID da triagem para cancelar.');
    }
  }

  ngOnDestroy(): void {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }
}