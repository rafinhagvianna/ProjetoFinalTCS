import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable } from 'rxjs';
import { APP_CONFIG } from '../app.config'; 
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgendamentoApiService {
  private apiUrl = APP_CONFIG.apiUrl+'/agendamentos'; 

  constructor(private http: HttpClient) { }

  getHorariosDisponiveis(data: string, servicoId: string): Observable<string[]> {
    const params = new HttpParams()
      .set('data', data)
      .set('servicoId', servicoId); 

    return this.http.get<string[]>(`${this.apiUrl}/disponibilidade`, { params });
  }

  salvarAgendamento(agendamentoData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, agendamentoData);
  }

  getAgendamentosPorData(data: string): Observable<AgendamentoCompleto[]> {
    const params = new HttpParams().set('data', data);
    return this.http.get<AgendamentoCompleto[]>(`${this.apiUrl}/data`, { params });
  }

  deletarAgendamento(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAgendamentoPorId(id: string): Observable<AgendamentoCompleto> {
    return this.http.get<AgendamentoCompleto>(`${this.apiUrl}/${id}`);
  }

  atualizarAgendamento(id: string, agendamentoData: AgendamentoRequest): Observable<AgendamentoCompleto> {
    return this.http.put<AgendamentoCompleto>(`${this.apiUrl}/${id}`, agendamentoData);
  }

  atualizarStatusDocumentoAgendamento(
    agendamentoId: string,
    documentoCatalogoId: string,
    requestDTO: DocumentoPendente
  ): Observable<DocumentoPendente> {
    return this.http.put<DocumentoPendente>(
      `${this.apiUrl}/${agendamentoId}/documentos/${documentoCatalogoId}/status`,
      requestDTO
    );
  }
  verificarAgendamentoAtivo(): Observable<AgendamentoCompleto | null> {
    const url = `${this.apiUrl}/cliente/ativo`;
    return this.http.get<AgendamentoCompleto>(url).pipe(
      catchError(error => {
        // Se o erro for 404 (Not Found), significa que não há agendamento ativo.
        // Retornamos null para não quebrar a aplicação.
        if (error.status === 404) {
          return of(null);
        }
        throw error;
      })
    );
  }
}

export interface AgendamentoCompleto {
  id: string;
  usuarioId: string;
  nomeClienteSnapshot: string;
  atendenteId: string;
  servicoId: string;
  nomeServicoSnapshot: string;
  dataHora: string; // Formato ISO 8601 (ex: "2025-07-09T10:00:00")
  atendidoEm: string | null; // Pode ser nulo
  observacoes: string; // Pode ser nulo
  criadoEm: string;
  status: string; // Ou um enum se você tiver um para status no frontend
  documentosPendentes: DocumentoPendente[]; // Adapte esta interface também se necessário
}

export interface DocumentoPendente {
  id: string;
  documentoCatalogoId: string;
  nomeDocumentoSnapshot: string;
  status: 'PENDENTE' | 'APROVADO' | 'REJEITADO' | 'ENVIADO'; 
  observacao: string | null;
  urlDocumento: string | null;
}

export interface AgendamentoRequest {
  usuarioId: string;
  servicoId: string;
  atendenteId: string | null;
  dataHora: string;
  observacoes: string;
  status: string;
  atendidoEm: string | null;
}
