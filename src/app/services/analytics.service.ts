// Em: src/app/services/analytics.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG } from '../app.config';

// Interfaces para os dados dos gráficos
export interface ContagemPorItem {
  item: string;
  quantidade: number;
}
export interface ContagemPorData {
  data: string; // Vem como string
  quantidade: number;
}

@Injectable({ providedIn: 'root' })
export class AnalyticsService {
  private apiUrl = APP_CONFIG.apiUrl + '/analytics';
  private agendamentoApiUrl = APP_CONFIG.apiUrl + '/agendamentos/analytics';

  constructor(private http: HttpClient) {}

  getServicosMaisUtilizadosTriagem(): Observable<ContagemPorItem[]> {
    return this.http.get<ContagemPorItem[]>(`${this.apiUrl}/servicos-mais-utilizados`);
  }

  getAtendimentosPorDia(): Observable<ContagemPorData[]> {
    return this.http.get<ContagemPorData[]>(`${this.apiUrl}/atendimentos-por-dia`);
  }

  getServicosMaisUtilizadosAgendamento(): Observable<ContagemPorItem[]> {
    return this.http.get<ContagemPorItem[]>(`${this.agendamentoApiUrl}/atendimentos-por-servico`);
  }
}