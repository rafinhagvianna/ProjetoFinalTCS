import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
// Importe a sua interface DocumentoPendente atualizada e as outras interfaces
import { AgendamentoApiService, AgendamentoCompleto, DocumentoPendente } from '../../services/agendamento-api.service';
// Importe o novo serviço de documentação e seus DTOs/Enums
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { DocumentoUploadApiService, ValidacaoDocumentoRequestDTO, StatusDocumento } from '../../services/documento.service';

@Component({
  selector: 'app-verificar-documentos',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './verificar-documentos.component.html',
  styleUrls: ['./verificar-documentos.component.scss']
})
export class VerificarDocumentosComponent implements OnInit, OnDestroy {
  agendamentoId: string | null = null;
  agendamento: AgendamentoCompleto | null = null;
  documentosParaValidar: DocumentoPendente[] = [];

  // Exponha o enum StatusDocumento para o template usar
  StatusDocumento = StatusDocumento; // Permite usar StatusDocumento.VALIDADO no HTML

  isConfirming: boolean = false;
  validationMessage: string | null = null;
  validationSuccess: boolean = false;
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private agendamentoApiService: AgendamentoApiService,
    private documentacaoApiService: DocumentoUploadApiService // Injete o novo serviço
  ) { }

  ngOnInit(): void {
    document.body.classList.add('menu-funcionario-bg');

    this.route.paramMap.subscribe(params => {
      this.agendamentoId = params.get('id');
      if (this.agendamentoId) {
        this.carregarAgendamento(this.agendamentoId);
      } else {
        alert('ID do agendamento não fornecido na URL.');
        this.router.navigate(['/menu-funcionario/agendamentos']);
        this.isLoading = false;
      }
    });
  }

  ngOnDestroy(): void {
    document.body.classList.remove('menu-funcionario-bg');
  }

  carregarAgendamento(id: string): void {
    this.isLoading = true;
    this.agendamentoApiService.getAgendamentoPorId(id).subscribe({
      next: (data) => {
        this.agendamento = data;
        this.documentosParaValidar = data.documentosPendentes.map(doc => ({
          ...doc,
          observacao: doc.observacao || ''
        }));
        this.isLoading = false;
        console.log('Agendamento e documentos carregados:', this.agendamento);
      },
      error: (err) => {
        console.error('Erro ao carregar agendamento para validação:', err);
        alert('Erro ao carregar agendamento. Verifique o console.');
        this.isLoading = false;
        this.router.navigate(['/menu-funcionario/agendamentos']);
      }
    });
  }

  async confirmarValidacao(): Promise<void> {
    if (!this.agendamentoId) {
      alert('ID do agendamento não encontrado para validação.');
      return;
    }

    this.isConfirming = true;
    this.validationMessage = null;
    this.validationSuccess = false;

    const failedDocuments: string[] = [];
    const successfulDocuments: string[] = [];

    for (const doc of this.documentosParaValidar) {
      // Mapeia o status do frontend para o status do backend (StatusDocumento)
      let novoStatusBackend: StatusDocumento | null = null;
      if (doc.status === 'APROVADO') {
        novoStatusBackend = StatusDocumento.APROVADO;
      } else if (doc.status === 'REJEITADO') {
        novoStatusBackend = StatusDocumento.REJEITADO;
      } else {
        // Se o status não for APROVADO ou REJEITADO, não fazemos a chamada de validação
        // Ou você pode decidir enviá-lo como PENDENTE ou ENVIADO, se o backend permitir
        // Para este caso, vamos pular a validação se não for APROVADO ou REJEITADO
        successfulDocuments.push(`${doc.nomeDocumentoSnapshot} (status inalterado: ${doc.status})`);
        continue;
      }

      // Validação frontend: se rejeitado, a observação é obrigatória
      if (novoStatusBackend === StatusDocumento.REJEITADO && !doc.observacao?.trim()) {
        alert(`O motivo da rejeição é obrigatório para o documento: ${doc.nomeDocumentoSnapshot}`);
        this.isConfirming = false;
        return;
      }

      const validacaoDTO: ValidacaoDocumentoRequestDTO = {
        novoStatus: novoStatusBackend,
        observacao: doc.observacao, // A observação do frontend vai para a observação do DTO
        agendamentoId: this.agendamentoId,
        documentoCatalogoId: doc.documentoCatalogoId
      };

      try {
        // CHAMA O NOVO SERVIÇO DE DOCUMENTAÇÃO COM O ID DO DOCUMENTO
        await this.documentacaoApiService.validarDocumento(
          validacaoDTO
        ).toPromise();
        successfulDocuments.push(doc.nomeDocumentoSnapshot);
      } catch (err) {
        console.error(`Erro ao validar documento ${doc.nomeDocumentoSnapshot} (ID: ${doc.documentoCatalogoId}):`, err);
        let errorMessage = `Erro ao validar ${doc.nomeDocumentoSnapshot}.`;
        // if (err.error && err.error.message) {
        //   errorMessage += ` Detalhes: ${err.error.message}`;
        // }
        failedDocuments.push(errorMessage);
      }
    }

    // Feedback final
    if (failedDocuments.length === 0) {
      this.validationMessage = `Todos os documentos selecionados foram validados com sucesso!`;
      this.validationSuccess = true;
      // Opcional: Recarregar agendamento para refletir os novos status do backend
      this.carregarAgendamento(this.agendamentoId!);
    } else if (successfulDocuments.length > 0) {
      this.validationMessage = `Validação concluída, mas com erros nos seguintes documentos: ${failedDocuments.join('; ')}. Sucesso em: ${successfulDocuments.join(', ')}.`;
      this.validationSuccess = false;
    } else {
      this.validationMessage = `Falha na validação de todos os documentos. Erros: ${failedDocuments.join('; ')}.`;
      this.validationSuccess = false;
    }

    this.isConfirming = false;
  }

  retornar(): void {
    this.router.navigate(['/menu-funcionario/agendamentos']);
  }
}