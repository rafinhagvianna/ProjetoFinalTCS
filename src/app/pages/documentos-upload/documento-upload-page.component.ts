import { Component, OnInit } from '@angular/core';
import { CommonModule, NgIf, NgFor, DatePipe, TitleCasePipe } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DocumentoUploadApiService, UploadStatus } from '../../services/documento.service';
import { Agendamento, DocumentoPendente, TipoDocumentoCatalogo } from '../../models/agendamento.model'; 
import { Triagem } from '../../models/triagem.model'; 
import { NavbarComponent } from '../../components/navbar/navbar.component';
import Swal from 'sweetalert2'
import { Router } from '@angular/router';

@Component({
  selector: 'app-documento-upload-page',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, TitleCasePipe,NavbarComponent], 
  templateUrl: './documento-upload-page.component.html',
  styleUrls: ['./documento-upload-page.component.scss']
})
export class DocumentoUploadPageComponent implements OnInit {

  selectedFile: File | null = null;
  agendamentoId: string | null = null;
  triagemId: string | null = null;
  documentoCatalogoId: string | null = null;

  agendamentoDetails: Agendamento | null = null;
  triagemDetails: Triagem | null = null;
  documentosNecessarios: DocumentoPendente[] = []; 

  agendamentosDisponiveis: Agendamento[] = []; 
  triagensDisponiveis: Triagem[] = [];
  tiposDocumentoDisponiveisParaUpload: TipoDocumentoCatalogo[] = []; 
  todosOsTiposDeDocumentoDoCatalogo: TipoDocumentoCatalogo[] = []; 

  uploadProgress: number = 0;
  uploadMessage: string = '';
  isUploading: boolean = false;
  uploadError: string = '';

  

  currentUserId: string = 'AD6AB5B0-D306-4F53-AEF5-E966971E89D9'; 

  constructor(
    private uploadService: DocumentoUploadApiService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {

    this.loadTiposDocumento();

    this.route.queryParams.subscribe(params => {
      this.agendamentoId = params['agendamentoId'] || null;
      this.triagemId = params['triagemId'] || null;

      if (this.agendamentoId) {
        this.loadAgendamentoDetails(this.agendamentoId);
      } else if (this.triagemId) {
        this.loadTriagemDetails(this.triagemId);
      } else {
        this.loadAgendamentosDoUsuario();
        this.loadTriagensDoUsuario();
        this.documentosNecessarios = []; 
        this.uploadError = 'Por favor, selecione um agendamento ou triagem para fazer o upload de documentos.';
      }
    });
  }

  loadAgendamentosDoUsuario(): void {
    if (!this.agendamentoId && !this.triagemId) {
      this.uploadService.getAgendamentosDoUsuario(this.currentUserId).subscribe({
        next: (data: Agendamento[]) => {
          this.agendamentosDisponiveis = data;
          if (data.length === 0) {
            this.uploadError = 'Não há agendamentos disponíveis para este usuário.';
          } else {
            this.uploadError = 'Selecione um agendamento da lista abaixo.';
          }
        },
        error: (err: any) => {
          console.error('Erro ao carregar agendamentos do usuário:', err);
          this.uploadError = 'Erro ao carregar agendamentos. Verifique o backend e a rede.';
        }
      });
    }
  }

  loadTriagensDoUsuario(): void {
    if (!this.agendamentoId && !this.triagemId) {
      this.uploadService.getTriagensEmAbertoDoUsuario(this.currentUserId).subscribe({
        next: (data: Triagem[]) => {
          this.triagensDisponiveis = data;
        },
        error: (err: any) => {
          console.error('Erro ao carregar triagens do usuário:', err);
        }
      });
    }
  }

  loadAgendamentoDetails(id: string): void {
    this.uploadService.getAgendamentoDetails(id).subscribe({
      next: (data: Agendamento) => {
        this.agendamentoDetails = data;
        this.documentosNecessarios = data.documentosPendentes || []; 
        this.filterAvailableDocumentTypes(); 
        this.uploadError = '';
      },
      error: (err: any) => {
        console.error('Erro ao carregar detalhes do agendamento:', err);
        this.agendamentoDetails = null;
        this.documentosNecessarios = [];
        this.uploadError = 'Erro ao carregar detalhes do agendamento. Pode não existir ou o servidor está fora.';
        this.filterAvailableDocumentTypes(); 
      }
    });
  }

 
  loadTriagemDetails(id: string): void {
    this.uploadService.getTriagemDetails(id).subscribe({
      next: (data: Triagem) => {
        this.triagemDetails = data;
        this.documentosNecessarios = data.documentosPendentes || []; 
        this.filterAvailableDocumentTypes(); 
        this.uploadError = '';
      },
      error: (err: any) => {
        console.error('Erro ao carregar detalhes da triagem:', err);
        this.triagemDetails = null;
        this.documentosNecessarios = [];
        this.uploadError = 'Erro ao carregar detalhes da triagem. Pode não existir ou o servidor está fora.';
        this.filterAvailableDocumentTypes(); 
      }
    });
  }

  loadTiposDocumento(): void {
    
    this.uploadService.getTiposDocumento().subscribe({
      next: (data: TipoDocumentoCatalogo[]) => {
        this.todosOsTiposDeDocumentoDoCatalogo = data.filter(tipo => tipo.isAtivo);
        if (!this.agendamentoId && !this.triagemId) {
          this.filterAvailableDocumentTypes();
        }
      },
      error: (err: any) => {
        console.error('Erro ao carregar tipos de documento do catálogo:', err);
        this.uploadError = 'Erro ao carregar tipos de documento. Verifique o backend e a rede.';
        this.tiposDocumentoDisponiveisParaUpload = []; 
      }
    });
  }

  
  filterAvailableDocumentTypes(): void {


    if (!this.todosOsTiposDeDocumentoDoCatalogo || this.todosOsTiposDeDocumentoDoCatalogo.length === 0) {
      this.tiposDocumentoDisponiveisParaUpload = [];
      console.warn('[filterAvailableDocumentTypes] Catálogo de tipos de documento ainda não carregado ou vazio. Dropdown de tipos de upload ficará vazio.');
      return; 
    }

    if (!this.agendamentoDetails && !this.triagemDetails) {
      this.tiposDocumentoDisponiveisParaUpload = [...this.todosOsTiposDeDocumentoDoCatalogo];
      return;
    }

    if (this.documentosNecessarios && this.documentosNecessarios.length > 0) {
      const tiposPendentesIds = new Set(
        this.documentosNecessarios
          .filter(doc => doc.status !== 'APROVADO')
          .map(doc => doc.documentoCatalogoId)
      );

      this.tiposDocumentoDisponiveisParaUpload = this.todosOsTiposDeDocumentoDoCatalogo.filter(
        catalogoTipo => catalogoTipo.isAtivo && tiposPendentesIds.has(catalogoTipo.id)
      );

    } else {
      this.tiposDocumentoDisponiveisParaUpload = []; 
    }
  }
  
  onAgendamentoSelected(agendamentoId: string): void {
    console.log('[onAgendamentoSelected] Agendamento selecionado:', agendamentoId);
    this.triagemId = null;
    this.agendamentoId = agendamentoId;

    if (agendamentoId) {
      this.loadAgendamentoDetails(agendamentoId);
    } else {
      this.agendamentoDetails = null;
      this.documentosNecessarios = [];
      this.filterAvailableDocumentTypes();
      this.uploadError = 'Por favor, selecione um agendamento ou triagem para fazer o upload de documentos.';
    }
  }

  onTriagemSelectedDropdown(triagemId: string): void { 
    console.log('[onTriagemSelectedDropdown] Triagem selecionada (dropdown):', triagemId);
    this.agendamentoId = null; 
    this.triagemId = triagemId;

    if (triagemId) {
      this.loadTriagemDetails(triagemId);
    } else {
      this.triagemDetails = null;
      this.documentosNecessarios = [];
      this.filterAvailableDocumentTypes();
      this.uploadError = 'Por favor, selecione um agendamento ou triagem para fazer o upload de documentos.';
    }
  }

  onTriagemSelected(triagemId: string): void {
    this.agendamentoId = null; 
    this.triagemId = triagemId;

    if (triagemId) {
      this.loadTriagemDetails(triagemId);
    } else {
      this.triagemDetails = null;
      this.documentosNecessarios = [];
      this.filterAvailableDocumentTypes(); 
      this.uploadError = 'Por favor, selecione um agendamento ou triagem para fazer o upload de documentos.';
    }
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] ?? null;
    this.uploadMessage = '';
    this.uploadError = '';
  }

  onUpload(): void {
    this.uploadMessage = '';
    this.uploadError = '';
    this.uploadProgress = 0;

    // if (!this.selectedFile) {
    //   this.uploadError = 'Por favor, selecione um arquivo para upload.';
    //   console.warn('[onUpload] Falha: Nenhum arquivo selecionado.');
    //   return;
    // }
    if (!this.selectedFile) {
      Swal.fire('Atenção', 'Por favor, selecione um arquivo para upload.', 'warning');
      return;
    }
    // if (!this.documentoCatalogoId) {
    //   this.uploadError = 'Por favor, selecione o tipo de documento.';
    //   console.warn('[onUpload] Falha: Tipo de documento não selecionado.');
    //   return;
    // }
    if (!this.documentoCatalogoId) {
      Swal.fire('Atenção', 'Por favor, selecione o tipo de documento.', 'warning');
      return;
    }
    // if (!this.agendamentoId && !this.triagemId) {
    //   this.uploadError = 'Selecione um agendamento ou triagem para fazer o upload.';
    //   console.warn('[onUpload] Falha: Nenhum agendamento ou triagem associado.');
    //   return;
    // }
    if (!this.agendamentoId && !this.triagemId) {
      Swal.fire('Atenção', 'Selecione um agendamento ou triagem para associar o documento.', 'warning');
      return;
    }
    if (this.agendamentoId && this.triagemId) {
        this.uploadError = 'O documento não pode estar associado a um ID de Triagem E a um ID de Agendamento simultaneamente.';
        console.warn('[onUpload] Falha: Agendamento e Triagem IDs preenchidos simultaneamente.');
        return;
    }

    this.isUploading = true;
    this.uploadService.uploadDocumento(
      this.triagemId,
      this.agendamentoId,
      this.documentoCatalogoId,
      this.selectedFile
    ).subscribe({
      // next: (event: UploadStatus) => {
      //   if (event.status === 'progress') {
      //     this.uploadProgress = event.message as number;
      //     this.uploadMessage = `Enviando: ${event.message}%`;
      //   } else if (event.status === 'success') {
      //     this.uploadMessage = 'Upload realizado com sucesso!';
      //     this.uploadError = '';
      //     this.isUploading = false;
      //     this.resetForm();
      //     if (this.agendamentoId) {
      //       this.loadAgendamentoDetails(this.agendamentoId);
      //     } else if (this.triagemId) {
      //       this.loadTriagemDetails(this.triagemId);
      //     }
      //   }
      // },
      next: (event: UploadStatus) => {
        if (event.status === 'progress') {
          this.uploadProgress = event.message as number;
          
        } else if (event.status === 'success') {
          this.isUploading = false;
          this.uploadProgress = 100; // Garante que a barra chegue a 100%
  
          // 👇 POP-UP DE SUCESSO 👇
          Swal.fire({
            icon: 'success',
            title: 'Upload Realizado!',
            text: 'Seu documento foi enviado com sucesso.',
            timer: 2000,
            showConfirmButton: false
          }).then(() => {
              // Após o pop-up fechar, reseta o formulário e recarrega os detalhes
              this.resetForm();
              if (this.agendamentoId) {
                this.loadAgendamentoDetails(this.agendamentoId);
              } else if (this.triagemId) {
                this.loadTriagemDetails(this.triagemId);
              }
          });
        }
      },
  //     error: (error: any) => {
  //       console.error('Erro no upload:', error);
  //       this.uploadError = `Erro ao fazer upload: ${error.message || 'Verifique o console para mais detalhes.'}`;
  //       this.uploadMessage = '';
  //       this.isUploading = false;
  //     }
  //   });
  // }
  error: (error: any) => {
    this.isUploading = false;
    console.error('Erro no upload:', error);
    
    // 👇 POP-UP DE ERRO 👇
    Swal.fire({
      icon: 'error',
      title: 'Falha no Upload',
      text: error.error?.message || 'Não foi possível enviar o seu documento. Tente novamente.',
      confirmButtonColor: '#c62828'
    });
  }
});
}

  resetForm(): void {
    this.selectedFile = null;
    this.documentoCatalogoId = null;
    this.uploadProgress = 0;
    this.uploadMessage = '';
    this.uploadError = '';
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    if (fileInput) {
        fileInput.value = ''; 
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'APROVADO': return 'status-aprovado';
      case 'REJEITADO': return 'status-rejeitado';
      case 'AGUARDANDO_VALIDACAO': return 'status-aguardando';
      case 'PENDENTE': return 'status-pendente';
      case 'ENVIADO': return 'status-enviado';
      default: return '';
    }
  }

  voltarParaMenu(): void {
    this.router.navigate(['/menu-cliente']);
  }
}