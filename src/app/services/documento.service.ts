import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpParams, HttpProgressEvent } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs'; 
import { map, delay } from 'rxjs/operators'; 
import { Agendamento, TipoDocumentoCatalogo  } from '../models/agendamento.model'; 
import {  Triagem } from '../models/triagem.model';
import { APP_CONFIG } from '../app.config';

export interface UploadStatus {
  status: 'progress' | 'success' | 'idle';
  message: number | any;
}

@Injectable({
  providedIn: 'root'
})
// export class DocumentoUploadApiService {
//   private documentoServiceBaseUrl = 'http://localhost:9000/api/documentacao'; 
//   private agendamentoServiceBaseUrl = 'http://localhost:9000/api/agendamentos'; 
//   private triagemServiceBaseUrl = 'http://localhost:9000/api/triagens'; 
//   private catalogoServiceBaseUrl = 'http://localhost:9000/api/documentos'; 
export class DocumentoUploadApiService {
  private documentoServiceBaseUrl = APP_CONFIG.apiUrl+'/documentacao'; 
  private agendamentoServiceBaseUrl = APP_CONFIG.apiUrl+'/agendamentos'; 
  private triagemServiceBaseUrl = APP_CONFIG.apiUrl+'/triagens'; 
  private catalogoServiceBaseUrl = APP_CONFIG.apiUrl+'/documentos'; 


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
    const url = `${this.agendamentoServiceBaseUrl}/cliente`;
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
    const url = `${this.triagemServiceBaseUrl}/cliente`;
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

  validarDocumento(validacaoDTO: ValidacaoDocumentoRequestDTO): Observable<DocumentoResponseDTO> {
    return this.http.put<DocumentoResponseDTO>(`${this.documentoServiceBaseUrl}/validar`, validacaoDTO);
  }
}

// src/app/shared/enums/status-documento.enum.ts (crie este arquivo)
export enum StatusDocumento {
  APROVADO = 'APROVADO',
  REJEITADO = 'REJEITADO',
  PENDENTE = 'PENDENTE',
  ENVIADO = 'ENVIADO',
  // Adicione outros status se existirem no seu backend
}

// src/app/services/documentacao-api.service.ts (ou onde você centraliza suas interfaces)

// Esta é a interface que seu backend espera no body do PUT /documentos/{id}/validar
export interface ValidacaoDocumentoRequestDTO {
  novoStatus: StatusDocumento; // Ex: VALIDADO, REJEITADO
  observacao?: string | null; // Motivo da validação/rejeição (opcional para VALIDADO, obrigatório para REJEITADO)
  agendamentoId: string;
  documentoCatalogoId: string;
}

// A interface DocumentoPendente (response do AgendamentoService) permanece como você forneceu:
export interface DocumentoPendente {
  id: string; // ID do documento no documentacao-service
  documentoCatalogoId: string;
  nomeDocumentoSnapshot: string;
  status: 'PENDENTE' | 'APROVADO' | 'REJEITADO' | 'ENVIADO'; // Status atual do documento no AgendamentoService
  observacao: string | null; // Observação existente
  urlDocumento: string | null;
}

// DocumentoResponseDTO - Supondo que seja o retorno após a validação bem-sucedida
// (Pode ser simples, só para tipagem do Observable)
export interface DocumentoResponseDTO {
  id: string;
  status: StatusDocumento;
  observacaoValidacao?: string;
  urlVisualizacao?: string;
  // Outros campos relevantes após a validação
}