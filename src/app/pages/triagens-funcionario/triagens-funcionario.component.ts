// // src/app/pages/triagens/triagens-funcionario.component.ts

// import { Component, OnInit, OnDestroy }            from '@angular/core';
// import { CommonModule }                 from '@angular/common';
// import { Triagem, TriagemApiService}      from '../../services/triagem-api.service';
// import { NavbarComponent }              from '../../components/navbar/navbar.component';


// @Component({
//   selector: 'app-triagens-funcionario',
//   standalone: true,
//   imports: [CommonModule, NavbarComponent],
//   templateUrl: './triagens-funcionario.component.html',
//   styleUrls: ['./triagens-funcionario.component.scss']
// })
// export class TriagensFuncionarioComponent implements OnInit {
//   triagens: Triagem[] = [];
//   isLoading = false;
//   errorMsg: string | null = null;

//   constructor(private triagemService: TriagemApiService) {}

//   ngOnInit(): void {
//     // 1. Marca o body para tema de funcionário
//     document.body.classList.add('menu-funcionario-bg');

//     this.isLoading = true;
//     this.triagemService.getAll().subscribe({
//       next: (list: Triagem[]) => {
//         this.triagens = list;
//         this.isLoading = false;
//       },
//       error: (err: any) => {
//         console.error('Erro ao carregar triagens', err);
//         this.errorMsg = 'Falha ao carregar triagens. Tente novamente.';
//         this.isLoading = false;
//       }
//     });
//   }

//   ngOnDestroy(): void{
//     // Remove a classe ao sair da tela
//     document.body.classList.remove('menu-funcionario-bg');
//   }
// }

// src/app/pages/triagens/triagens-funcionario.component.ts

import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule }                  from '@angular/common';
import { Triagem, TriagemApiService }    from '../../services/triagem-api.service';
import { NavbarComponent }               from '../../components/navbar/navbar.component';
import { Router } from '@angular/router'; 
import Swal from 'sweetalert2';

@Component({
  selector: 'app-triagens-funcionario',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './triagens-funcionario.component.html',
  styleUrls: ['./triagens-funcionario.component.scss']
})
export class TriagensFuncionarioComponent implements OnInit, OnDestroy {
  triagens: Triagem[]      = [];
  isLoading = false;
  isChamando = false;
  errorMsg: string | null  = null;

  constructor(private triagemService: TriagemApiService, private router: Router) {}

  ngOnInit(): void {
    document.body.classList.add('menu-funcionario-bg');
    this.loadTriagens();
  }

  ngOnDestroy(): void {
    document.body.classList.remove('menu-funcionario-bg');
  }

  /** Carrega todas as triagens do back-end */
  private loadTriagens(): void {
  this.isLoading = true;
  this.triagemService.getAll().subscribe({
    next: list => {
      // AQUI A MUDANÇA: Filtramos a lista para mostrar apenas os status relevantes
      this.triagens = list.filter(t => t.status === 'AGUARDANDO' || t.status === 'EM_ATENDIMENTO');
      this.isLoading = false;
    },
    error: err => {
      console.error('Erro ao carregar triagens', err);
      this.errorMsg = 'Falha ao carregar triagens. Tente novamente.';
      this.isLoading = false;
    }
  });
}

  /** Chama o próximo cliente AGUARDANDO */
//   onChamarProximo(): void {
//     this.isChamando = true;
//     this.triagemService.buscarProximaTriagem().subscribe({
//       next: updated => {
//         // 1. Atualiza em memória o status daquele item
//         const idx = this.triagens.findIndex(t => t.id === updated.id);
//         if (idx > -1) {
//           this.triagens[idx] = updated;
//         }
//         // 2. (Opcional) Recarrega toda a lista
//         // this.loadTriagens();
//         this.isChamando = false;
//       },
//       error: err => {
//         console.error('Erro ao chamar próximo cliente', err);
//         alert('Não foi possível chamar o próximo. Tente novamente.');
//         this.isChamando = false;
//       }
//     });
//   }
// }

 /** Chama o próximo cliente AGUARDANDO e navega para a tela dele */
 onChamarProximo(): void {
  this.isChamando = true;
  this.triagemService.buscarProximaTriagem().subscribe({
    next: updatedTriagem => {
      this.isChamando = false;
      
      // A triagem foi atualizada no backend, agora navegamos para a tela de atendimento
      // passando o ID da triagem que acabamos de chamar.
      console.log(`Navegando para o atendimento da triagem: ${updatedTriagem.id}`);
      this.router.navigate(['/menu-funcionario/cliente-atual', updatedTriagem.id]);
    },
    error: err => {
        this.isChamando = false;
        console.error('Erro ao chamar próximo cliente', err);

        // Mensagem padrão para erros genéricos
        let titulo = 'Erro ao Chamar';
        let texto = 'Ocorreu um problema inesperado. Tente novamente.';
        let icone: 'error' | 'info' = 'error';

        if (err.status === 404) {
          titulo = 'Fila Vazia';
          icone = 'info';

          if (typeof err.error === 'object' && err.error.message) {
            texto = err.error.message;
          } 
          // Se for uma string simples, usamos diretamente
          else if (typeof err.error === 'string') {
            texto = err.error;
          } 
          // Se for qualquer outra coisa, usamos uma mensagem padrão
          else {
            texto = 'Não há mais clientes aguardando atendimento.';
          }
        
        }

        Swal.fire({
          icon: icone,
          title: titulo,
          text: texto,
          confirmButtonColor: '#c62828' // Cor do botão do seu tema
        });
      }
    });
}

irHistorico(): void{
  this.router.navigate(['/menu-funcionario/historico-atendimentos']);
}

retornar(): void{
  this.router.navigate(['/menu-funcionario']);
}

verDetalhesCliente(triagem: Triagem): void{
  console.log(`Navegando para detalhes da triagem ID: ${triagem.id}`);
  this.router.navigate(['/menu-funcionario/cliente-atual', triagem.id]);

}

}
