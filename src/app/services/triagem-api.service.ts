import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TriagemCompleta } from './triagem.interface'; 
import { APP_CONFIG } from '../app.config';


export interface TriagemResponse {
  disponibilidade: Date;
}

export interface Triagem {
  id: string;
  clienteId: string;
  servicoId: string;
  horarioSolicitacao: string;
  horarioEstimadoAtendimento?: string;
  status: string;
  tempoEstimadoMinutos?: number;
  prioridade?: number;
  nomeClienteSnapshot: string;
  nomeServicoSnapshot: string;
}

@Injectable({
  providedIn: 'root'
})
export class TriagemApiService {

  
  // private triagemUrl = 'http://localhost:9000/api/triagens'; 
  private triagemUrl = APP_CONFIG.apiUrl+'/triagens'; 

  constructor(private http: HttpClient) { }

  getAll(): Observable<Triagem[]> {
    return this.http.get<Triagem[]>(this.triagemUrl);
  }

  buscarProximaTriagem(): Observable<Triagem> {
    return this.http.get<Triagem>(`${this.triagemUrl}/proxima`);
  }

  getHorarioDisponivel(): Observable<TriagemResponse> {
      return this.http.get<TriagemResponse>(`${this.triagemUrl}/disponibilidade`); 
    }

    getHistorico(): Observable<TriagemCompleta[]> {
    const url = `${this.triagemUrl}/historico`; // <-- Aponta para o novo endpoint
    const params = new HttpParams().set('cacheBuster', new Date().getTime().toString());
    return this.http.get<TriagemCompleta[]>(url, { params });
  }

  salvarTriagem(triagemData: any): Observable<any> {
    return this.http.post<any>(this.triagemUrl, triagemData);
  }

  getByClienteId(clienteId: string): Observable<Triagem[]> {
    return this.http.get<Triagem[]>(`${this.triagemUrl}/cliente/${clienteId}`);
  }

  getById(id: string): Observable<TriagemCompleta> {
    return this.http.get<TriagemCompleta>(`${this.triagemUrl}/${id}`);
  }

  atualizarStatus(id: string, novoStatus: string): Observable<any>{
    const url = `${this.triagemUrl}/${id}/status`;
    const body = { novoStatus: novoStatus };
    return this.http.patch(url, body);

  }

  cancelarTriagem(id: string): Observable<void> {
    // Envia uma requisição DELETE para a URL: '.../api/triagens/{id}'
    return this.http.delete<void>(`${this.triagemUrl}/${id}`);
  }

  verificarTriagemAtiva(): Observable<Triagem | null> {
    const url = `${this.triagemUrl}/cliente/ativa`;
    return this.http.get<Triagem>(url).pipe(
      catchError(error => {
        // Se o erro for 404 (Not Found), é um cenário esperado. Retornamos null.
        if (error.status === 404) {
          return of(null);
        }
        // Para outros erros, nós os relançamos.
        throw error;
      })
    );
  }

}

