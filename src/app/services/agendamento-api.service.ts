import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AgendamentoApiService {
  private apiUrl = 'http://localhost:9000/api/agendamentos'; 

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
}
