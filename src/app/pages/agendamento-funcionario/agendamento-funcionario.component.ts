import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AgendamentoApiService, AgendamentoCompleto } from '../../services/agendamento-api.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-agendamento-funcionario',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent,
    RouterLink
  ],
  templateUrl: './agendamento-funcionario.component.html',
  styleUrls: ['./agendamento-funcionario.component.scss']
})
export class AgendamentoFuncionarioComponent implements OnInit, OnDestroy {
  selectedDate: string = '';
  agendamentosDoDia: AgendamentoCompleto[] = [];
  minDate: string;

  constructor(
    private agendamentoApiService: AgendamentoApiService,
    private router: Router // Certifique-se que o Router está injetado
  ) {
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {

    document.body.classList.add('menu-funcionario-bg'); 

    if (!this.selectedDate) {
      this.selectedDate = this.minDate;
      this.carregarAgendamentos();
    }
  }

   ngOnDestroy(): void {

    document.body.classList.remove('menu-funcionario-bg');

  } 

  onDateChange(): void {
    this.carregarAgendamentos();
  }

  carregarAgendamentos(): void {
    if (this.selectedDate) {
      this.agendamentoApiService.getAgendamentosPorData(this.selectedDate).subscribe({
        next: (agendamentos) => {
          this.agendamentosDoDia = agendamentos;
          this.agendamentosDoDia.sort((a, b) => {
            const dateA = new Date(a.dataHora);
            const dateB = new Date(b.dataHora);
            return dateA.getTime() - dateB.getTime();
          });
          console.log('Agendamentos carregados e ordenados para o dia:', this.selectedDate, this.agendamentosDoDia);
        },
        error: (err) => {
          console.error('Erro ao carregar agendamentos para o dia:', this.selectedDate, err);
          this.agendamentosDoDia = [];
          alert('Erro ao carregar agendamentos. Verifique o console para detalhes.');
        }
      });
    } else {
      this.agendamentosDoDia = [];
    }
  }

  /**
   * Lógica para o botão "Alterar".
   * Redireciona para a tela de edição, passando o ID do agendamento como parâmetro de caminho.
   * @param agendamento O agendamento a ser alterado.
   */
  alterarAgendamento(agendamento: AgendamentoCompleto): void {
    console.log('Redirecionando para editar agendamento com ID:', agendamento.id);
    // REMOVA A LINHA ABAIXO:
    // alert(`Funcionalidade de Alterar para agendamento ID: ${agendamento.id} será implementada.`); 
    this.router.navigate(['/menu-funcionario/agendamentos/editar', agendamento.id]); 
  }
  validarDocumentos(agendamento: AgendamentoCompleto): void {
    console.log('Redirecionando para editar agendamento com ID:', agendamento.id);
    // REMOVA A LINHA ABAIXO:
    // alert(`Funcionalidade de Alterar para agendamento ID: ${agendamento.id} será implementada.`); 
    this.router.navigate(['/menu-funcionario/agendamentos/verificar-documentos', agendamento.id]); 
  }

  // cancelarAgendamento(agendamento: AgendamentoCompleto): void {
  //   if (confirm(`Tem certeza que deseja cancelar o agendamento de ${agendamento.nomeClienteSnapshot} para ${this.getFormattedTime(agendamento.dataHora)}?`)) {
  //     this.agendamentoApiService.deletarAgendamento(agendamento.id).subscribe({
  //       next: () => {
  //         alert('Agendamento cancelado com sucesso!');
  //         console.log('Agendamento cancelado:', agendamento.id);
  //         this.carregarAgendamentos();
  //       },
  //       error: (err) => {
  //         console.error('Erro ao cancelar agendamento:', agendamento.id, err);
  //         alert('Erro ao cancelar agendamento. Verifique o console para detalhes.');
  //       }
  //     });
  //   }
  // }

  cancelarAgendamento(agendamento: AgendamentoCompleto): void {
    // 1. SUBSTITUÍMOS O 'confirm()' PADRÃO POR UM SWAL.FIRE()
    Swal.fire({
      title: 'Tem certeza?',
      // Usamos 'html' para poder usar a tag <strong>
      html: `Deseja realmente cancelar o agendamento de <strong>${agendamento.nomeClienteSnapshot}</strong> para as <strong>${this.getFormattedTime(agendamento.dataHora)}</strong>?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sim, cancelar!',
      cancelButtonText: 'Não',
      confirmButtonColor: '#d33', // Vermelho para ação destrutiva
      cancelButtonColor: '#6c757d'  // Cinza para ação secundária
    }).then((result) => {
      // A lógica de cancelamento só é executada se o usuário clicar em "Sim, cancelar!"
      if (result.isConfirmed) {
        
        this.agendamentoApiService.deletarAgendamento(agendamento.id).subscribe({
          next: () => {
            console.log('Agendamento cancelado:', agendamento.id);
            
            // 2. SUBSTITUÍMOS O 'alert()' DE SUCESSO
            Swal.fire({
              icon: 'success',
              title: 'Cancelado!',
              text: 'O agendamento foi cancelado com sucesso.',
              timer: 2000,
              showConfirmButton: false
            });
  
            // Recarrega a lista de agendamentos para refletir a remoção
            this.carregarAgendamentos(); 
          },
          error: (err) => {
            console.error('Erro ao cancelar agendamento:', agendamento.id, err);
            
            // 3. SUBSTITUÍMOS O 'alert()' DE ERRO
            Swal.fire({
              icon: 'error',
              title: 'Erro!',
              text: 'Não foi possível cancelar o agendamento. Tente novamente.',
              confirmButtonColor: '#c62828'
            });
          }
        });
      }
    });
  }

  getFormattedDate(dateTimeString: string): string {
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('pt-BR');
  }

  getFormattedTime(dateTimeString: string): string {
    const date = new Date(dateTimeString);
    return date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }
}