import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpParams, HttpProgressEvent } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs'; 
import { map, delay } from 'rxjs/operators'; 
import { Agendamento, DocumentoPendente, TipoDocumentoCatalogo  } from '../models/agendamento.model'; 
import {  Triagem } from '../models/triagem.model';

export interface UploadStatus {
  status: 'progress' | 'success' | 'idle';
  message: number | any;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentoUploadApiService {
  private documentoServiceBaseUrl = 'http://localhost:8085/api/documentos'; 
  private agendamentoServiceBaseUrl = 'http://localhost:8082/api/agendamentos'; 
  private triagemServiceBaseUrl = 'http://localhost:8081/api/triagens'; 
  private catalogoServiceBaseUrl = 'http://localhost:8084/api/documentos-catalogo'; 


  constructor(private http: HttpClient) { }

  uploadDocumento(
    triagemId: string | null,
    agendamentoId: string | null,
    documentoCatalogoId: string,
    file: File
  ): Observable<UploadStatus> {
    const formData: FormData = new FormData();
    formData.append('arquivo', file);

    let params = new HttpParams();

    if (triagemId) {
      params = params.append('triagemId', triagemId);
    } else if (agendamentoId) {
      params = params.append('agendamentoId', agendamentoId);
    } else {
      return throwError(() => new Error("Um ID de triagem ou agendamento deve ser fornecido para o upload.")) as Observable<UploadStatus>;
    }

    params = params.append('documentoCatalogoId', documentoCatalogoId);

    const uploadUrl = `${this.documentoServiceBaseUrl}/upload`;

    return this.http.post(uploadUrl, formData, {
      params: params,
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (event.total !== undefined) {
                const progress = Math.round((100 * event.loaded) / event.total);
                return { status: 'progress', message: progress } as UploadStatus;
            }
            return { status: 'idle', message: '' } as UploadStatus;

          case HttpEventType.Response:
            return { status: 'success', message: event.body } as UploadStatus;

          default:
            return { status: 'idle', message: '' } as UploadStatus;
        }
      })
    );
  }


  getAgendamentosDoUsuario(clienteId: string): Observable<Agendamento[]> {
    const url = `${this.agendamentoServiceBaseUrl}/cliente/${clienteId}`;
    return this.http.get<Agendamento[]>(url);
  }

  getAgendamentoDetails(agendamentoId: string): Observable<Agendamento> {
    const url = `${this.agendamentoServiceBaseUrl}/${agendamentoId}`;
    return this.http.get<Agendamento>(url);
  }

  getTriagemDetails(triagemId: string): Observable<Triagem> {
    const url = `${this.triagemServiceBaseUrl}/${triagemId}`;
    return this.http.get<Triagem>(url);
  }

  getTiposDocumento(): Observable<TipoDocumentoCatalogo[]> {
    const url = `${this.catalogoServiceBaseUrl}`; 
    return this.http.get<TipoDocumentoCatalogo[]>(url);
  }

  getTriagensEmAbertoDoUsuario(clienteId: string): Observable<Triagem[]> {
    const url = `${this.triagemServiceBaseUrl}/cliente/${clienteId}`;
    console.log(`[DocumentoUploadApiService] Buscando triagem ÚNICA para cliente em: ${url}`);
    return this.http.get<Triagem>(url).pipe( 
      map(triagem => {
        if (triagem && (triagem.status === 'AGUARDANDO' || triagem.status === 'EM_ATENDIMENTO')) {
          return [triagem]; 
        }
        return []; 
      }),
     
    );
  }
}