import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router'; // ActivatedRoute para ler parâmetros
import { AgendamentoApiService } from '../../services/agendamento-api.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../components/confirmationmodal/confirmationmodal';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-agendamento',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './agendamento.html',
  styleUrl: './agendamento.scss'
})
export class AgendamentoComponent implements OnInit {
  selectedDate: string = '';
  availableTimes: string[] = [];
  minDate: string;
  selectedRadioTime: string = '';

  servicoId: string | null = null;
  tempoEstimadoServico: number | null = null;

  // private clienteService = inject(ClienteService);
  // private usuarioId = this.clienteService.getId();

  private loginService = inject(LoginService);
  private usuarioId = this.loginService.getId();

  constructor(
    private agendamentoApiService: AgendamentoApiService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal
  ) {
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.servicoId = params.get('id');
      console.log('AgendamentoComponent: servicoId lido do paramMap:', this.servicoId);

      if (!this.servicoId) {
        this.showInfoModal(
          'Serviço Não Encontrado',
          'O ID do serviço não foi especificado na URL. Por favor, selecione um serviço na tela anterior para continuar.',
          'Entendi'
        ).then(() => {
          this.router.navigate(['/menu-cliente']);
        });
        return;
      }

      if (!this.selectedDate) {
        this.selectedDate = this.minDate;
        this.onDateChange(); // Chama onDateChange para carregar os horários iniciais
      }
    });

    this.route.queryParams.subscribe(params => {
      this.tempoEstimadoServico = params['tempo'] ? +params['tempo'] : null;
      console.log('AgendamentoComponent: tempoEstimadoServico lido do queryParams:', this.tempoEstimadoServico);
    });
  }

  onDateChange(): void {
    if (!this.servicoId || !this.selectedDate) {
      this.availableTimes = [];
      this.selectedRadioTime = '';
      return;
    }

    const selectedLocalDate = new Date(this.selectedDate + 'T00:00:00');
    const dayOfWeek = selectedLocalDate.getDay();
    if (dayOfWeek === 0 || dayOfWeek === 6) {
      this.showInfoModal(
        'Dia Inválido',
        'Fins de semana não estão disponíveis para agendamento. Escolha um dia de semana.',
        'Ok'
      );
      this.selectedDate = '';
      this.availableTimes = [];
      this.selectedRadioTime = '';
      return;
    }

    this.agendamentoApiService
      .getHorariosDisponiveis(this.selectedDate, this.servicoId)
      .subscribe({
        next: times => {
          const today = new Date();
          const isToday =
            selectedLocalDate.getFullYear() === today.getFullYear() &&
            selectedLocalDate.getMonth() === today.getMonth() &&
            selectedLocalDate.getDate() === today.getDate();

          this.availableTimes = isToday
            ? times.filter(slot => {
                const slotDateTime = new Date(slot);
                const h = slotDateTime.getHours(), m = slotDateTime.getMinutes();
                if (h > today.getHours()) return true;
                if (h === today.getHours()) return m >= today.getMinutes();
                return false;
              })
            : times;

          this.selectedRadioTime = '';
        },
        error: err => {
          console.error('Erro ao buscar horários:', err);
          this.availableTimes = [];
          this.selectedRadioTime = '';
          this.showInfoModal(
            'Erro ao Buscar Horários',
            'Ocorreu um erro ao buscar horários disponíveis. Por favor, tente novamente mais tarde.',
            'Fechar'
          );
        }
      });
  }

  selectRow(time: string): void {
    this.selectedRadioTime = time;
  }

  agendarHorario(): void {
    if (!this.selectedRadioTime || !this.servicoId ) {
      this.showInfoModal(
        'Dados Incompletos',
        'Por favor, selecione um horário e um serviço antes de continuar.',
        'Ok'
      );
      return;
    }

    const modalRef = this.modalService.open(ConfirmationModalComponent, { centered: true });
    modalRef.componentInstance.title = 'Confirmar Agendamento';
    modalRef.componentInstance.message = `Você deseja <strong>confirmar</strong> o agendamento para o dia ${this.getFormattedDate(this.selectedRadioTime)} às ${this.getFormattedTime(this.selectedRadioTime)}?`;
    modalRef.componentInstance.confirmButtonText = 'Sim, Agendar';
    modalRef.componentInstance.cancelButtonText = 'Cancelar'; 
    modalRef.componentInstance.showCancelButton = true; 

    modalRef.result.then(
      (result) => {
        if (result === true) { 
          const agendamentoData = {
            usuarioId: this.usuarioId,
            servicoId: this.servicoId,
            atendenteId: '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f',
            dataHora: this.selectedRadioTime
          };

          this.agendamentoApiService.salvarAgendamento(agendamentoData).subscribe({
            next: response => {
              this.showInfoModal(
                'Agendamento Confirmado',
                'Seu agendamento foi realizado com sucesso!',
                'Continuar'
              ).then(() => {
                this.router.navigate(['/documentos/upload'], {
                  queryParams: { agendamentoId: response.id }
                });
              });
            },
            error: error => {
              console.error('Erro ao agendar:', error);
              const msg = error.error?.message
                ? `Erro ao agendar: ${error.error.message}`
                : 'Erro ao agendar. Tente novamente.';
              this.showInfoModal(
                'Falha no Agendamento',
                msg,
                'Fechar'
              );
            }
          });
        } else {
          console.log('Agendamento cancelado pelo usuário.');
          this.showInfoModal(
            'Agendamento Cancelado',
            'O agendamento foi cancelado.',
            'Ok'
          );
        }
      },
      (reason) => {
        console.log(`Modal de agendamento fechada por: ${reason}. Ação não confirmada.`);
        this.showInfoModal(
          'Agendamento Cancelado',
          'O agendamento foi cancelado ou não confirmado.',
          'Ok'
        );
      }
    );
  }

  voltar(): void{
    this.router.navigate(['/menu-cliente']); 
  }

  getFormattedDate(slot: string): string {
    return new Date(slot).toLocaleDateString('pt-BR');
  }

  getFormattedTime(slot: string): string {
    return new Date(slot).toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private showInfoModal(title: string, message: string, buttonText: string): Promise<any> {
    const modalRef = this.modalService.open(ConfirmationModalComponent, { centered: true });
    modalRef.componentInstance.title = title;
    modalRef.componentInstance.message = message;
    modalRef.componentInstance.confirmButtonText = buttonText;
    if (modalRef.componentInstance.hasOwnProperty('showCancelButton')) {
      modalRef.componentInstance.showCancelButton = false;
    }
    return modalRef.result;
  }
}