
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { TriagemApiService } from '../../services/triagem-api.service';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TriagemCompleta } from '../../services/triagem.interface';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-cliente-atual',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './cliente-atual.component.html',
  styleUrls: ['./cliente-atual.component.scss']
})
export class ClienteAtualComponent implements OnInit {
  triagem: TriagemCompleta | null = null;
  isLoading = true; 

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private triagemService: TriagemApiService,
    private modalService: NgbModal
  ) {}

  // ngOnInit(): void {
  //   // 3. APAGAMOS OS DADOS FICTÍCIOS E BUSCAMOS OS DADOS REAIS
  //   const triagemId = this.route.snapshot.paramMap.get('id');

  //   if (triagemId) {
  //     this.triagemService.getById(triagemId).subscribe({
  //       next: (dadosDaTriagem) => {
  //         this.triagem = dadosDaTriagem;
  //         this.isLoading = false;
  //         console.log('Dados da triagem carregados:', this.triagem);
  //       },
  //       error: (err) => {
  //         console.error('Falha ao buscar dados da triagem', err);
  //         this.isLoading = false;
  //         // Opcional: redirecionar para uma página de erro ou mostrar mensagem
  //       }
  //     });
  //   } else {
  //     console.error('Nenhum ID de triagem foi fornecido na rota.');
  //     this.isLoading = false;
  //     // Opcional: redirecionar para a página de triagens
  //     this.router.navigate(['/menu-funcionario/triagens']);
  //   }
  // }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const triagemId = params.get('id'); 

      if (triagemId) {
        this.isLoading = true; // Mostra o "Carregando..." sempre que navegamos para cá
        this.triagem = null;   // Limpa dados antigos para evitar exibi-los brevemente

        this.triagemService.getById(triagemId).subscribe({
          next: (dadosDaTriagem) => {
            this.triagem = dadosDaTriagem;
            this.isLoading = false;
            console.log('Dados da triagem recarregados com sucesso:', this.triagem);
          },
          error: (err) => {
            console.error('Falha ao buscar dados da triagem', err);
            this.isLoading = false;
            // Opcional: mostrar mensagem de erro na tela
          }
        });
      } else {
        console.error('Nenhum ID de triagem foi fornecido na rota.');
        this.isLoading = false;
        // Se não houver ID, faz sentido redirecionar para a lista de triagens.
        this.router.navigate(['/menu-funcionario/triagens']);
      }
    });
  }

    retornar(): void {
     this.router.navigate(['/menu-funcionario']); 
   }

  avancar(): void { // Renomeado de irParaTriagens
    this.router.navigate(['/menu-funcionario/triagens']);
  }

  encerrarAtendimento(): void {
    if (!this.triagem) return; // Trava de segurança

    Swal.fire({
      title: 'Finalizar Atendimento',
      html: `Tem certeza que deseja finalizar o atendimento para <strong>${this.triagem.nomeClienteSnapshot}</strong>?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sim, finalizar!',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#28a745',
      cancelButtonColor: '#6c757d',
    }).then((result) => {
      // A lógica agora só verifica se o botão de confirmação foi clicado
      if (result.isConfirmed && this.triagem) {
        
        // Chamada de API simples, sem o campo de observação
        this.triagemService.atualizarStatus(this.triagem.id, 'FINALIZADO').subscribe({
          next: () => {
            Swal.fire(
              'Sucesso!',
              'Atendimento finalizado com sucesso.',
              'success'
            );
            this.router.navigate(['/menu-funcionario/historico-atendimentos']);
          },
          error: (err) => {
            console.error('Erro ao finalizar atendimento:', err);
            Swal.fire(
              'Erro!',
              'Falha ao finalizar o atendimento. Tente novamente.',
              'error'
            );
          }
        });
      }
    });
  


  // const modalRef = this.modalService.open(ConfirmationModalComponent, { centered: true });
  // modalRef.componentInstance.title = 'Finalizar Atendimento';
  // modalRef.componentInstance.message = `Tem certeza que deseja finalizar o atendimento para <strong>${this.triagem.nomeClienteSnapshot}</strong>?`;
  // modalRef.componentInstance.confirmButtonText = 'Sim, finalizar';

  // modalRef.result.then((result) => {
  //   if (result === true && this.triagem) {
  //     // Usamos 'FINALIZADO' aqui, pois é o que seu backend espera no enum.
  //     this.triagemService.atualizarStatus(this.triagem.id, 'FINALIZADO').subscribe({
  //       next: () => {
  //         alert('Atendimento finalizado com sucesso!');
  //         // AQUI A MUDANÇA: Navegar para a tela de histórico
  //         this.router.navigate(['/menu-funcionario/historico-atendimentos']);
  //       },
  //       error: (err: any) => {
  //         console.error('Erro ao finalizar atendimento:', err);
  //         alert('Falha ao finalizar o atendimento. Verifique o console do backend.');
  //       }
  //       });
  //     }
  //   }).catch(() => {
  //       console.log('Modal de finalização fechado sem confirmação.');
  //   });
  }

  // Em: cliente-atual.component.ts

  historicoAtendimentos(): void{
    this.router.navigate(['/menu-funcionario/historico-atendimentos']);
  }

  // O seu método getFormattedDateTime continua útil
  getFormattedDateTime(dateTimeString: string | undefined | null): string {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }
}