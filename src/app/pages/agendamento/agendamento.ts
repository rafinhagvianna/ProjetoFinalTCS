import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgendamentoApiService } from '../../services/agendamento-api.service'; 

@Component({
  selector: 'app-agendamento',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './agendamento.html',
  styleUrl: './agendamento.scss'
})
export class AgendamentoComponent implements OnInit {
  selectedDate: string = '';
  availableTimes: string[] = [];
  minDate: string;
  selectedRadioTime: string = '';

  // IDs fictícios para teste
  readonly fictitiousUsuarioId: string = '1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d';
  readonly fictitiousServicoId: string = '2065fc22-9db6-4b6c-bbe1-af25fe8f7cc3'; 
  readonly fictitiousAtendenteId: string = '3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f';


  constructor(private agendamentoApiService: AgendamentoApiService) {
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    
    if (!this.selectedDate) {
      this.selectedDate = this.minDate;
      this.onDateChange();
    }
  }

  
  onDateChange(): void {
    if (this.selectedDate) {
      const selectedLocalDate = new Date(this.selectedDate + 'T00:00:00');
      const dayOfWeek = selectedLocalDate.getDay();

      if (dayOfWeek === 0 || dayOfWeek === 6) {
        alert('Fins de semana não estão disponíveis para agendamento. Por favor, escolha um dia de semana.');
        this.selectedDate = '';
        this.availableTimes = [];
        this.selectedRadioTime = '';
        return;
      }

      this.agendamentoApiService.getHorariosDisponiveis(this.selectedDate, this.fictitiousServicoId).subscribe({
        next: (times) => {
          const today = new Date();
          const selectedDateOnly = new Date(this.selectedDate + 'T00:00:00');

          const isToday = selectedDateOnly.getFullYear() === today.getFullYear() &&
                          selectedDateOnly.getMonth() === today.getMonth() &&
                          selectedDateOnly.getDate() === today.getDate();

          if (isToday) {
            this.availableTimes = times.filter(dateTimeString => {
              const slotDateTime = new Date(dateTimeString);
              const currentHour = today.getHours();
              const currentMinute = today.getMinutes();
              const slotHour = slotDateTime.getHours();
              const slotMinute = slotDateTime.getMinutes();

              if (slotHour > currentHour) {
                return true;
              }
              if (slotHour === currentHour) {
                return slotMinute >= currentMinute;
              }
              return false;
            });
          } else {
            this.availableTimes = times;
          }

          this.selectedRadioTime = ''; 
          console.log('Horários disponíveis (filtrados no front):', this.availableTimes);
        },
        error: (err) => {
          console.error('Erro ao buscar horários disponíveis:', err);
          this.availableTimes = [];
          this.selectedRadioTime = '';
          alert('Erro ao buscar horários disponíveis. Verifique o console para detalhes.');
        }
      });
    } else {
      this.availableTimes = [];
      this.selectedRadioTime = '';
    }
  }

  selectRow(time: string): void {
    this.selectedRadioTime = time;
    console.log('Horário selecionado:', this.selectedRadioTime);
  }

  agendarHorario(): void {
    console.log('Valor de selectedRadioTime ao clicar em Agendar:', this.selectedRadioTime);
    if (!this.selectedRadioTime) {
      alert('Por favor, selecione um horário disponível para agendar.');
      return;
    }

    const agendamentoData = {
      usuarioId: this.fictitiousUsuarioId,
      servicoId: this.fictitiousServicoId,
      atendenteId: this.fictitiousAtendenteId,
      dataHora: this.selectedRadioTime 
    };

    console.log('Tentando agendar:', agendamentoData);

    this.agendamentoApiService.salvarAgendamento(agendamentoData).subscribe({
      next: (response) => {
        alert('Agendamento realizado com sucesso!');
        console.log('Resposta do agendamento:', response);
        this.onDateChange();
        this.selectedRadioTime = ''; 
      },
      error: (error) => {
        console.error('Erro ao agendar:', error);
        let errorMessage = 'Erro ao agendar. Tente novamente.';
        if (error.error && error.error.message) {
          errorMessage = `Erro ao agendar: ${error.error.message}`;
        }
        alert(errorMessage);
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
