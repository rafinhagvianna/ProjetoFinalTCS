import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { CatalogoApiService, ServicoBackend } from '../../services/catalogo-api.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-gerenciar-setores',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent],
  templateUrl: './gerenciar-setores.component.html',
  styleUrls: ['./gerenciar-setores.component.scss']
})
export class GerenciarSetoresComponent implements OnInit, OnDestroy {
  setores: ServicoBackend[] = [];
  isLoading = true;

  constructor(
    private catalogoService: CatalogoApiService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Aplica o tema escuro
    document.body.classList.add('menu-funcionario-bg');
    this.carregarSetores();
  }

  ngOnDestroy(): void {
    // Limpa o tema ao sair da página
    document.body.classList.remove('menu-funcionario-bg');
  }

  carregarSetores(): void {
    this.isLoading = true;
    this.catalogoService.getTodosOsServicos().subscribe({
      next: (data) => {
        this.setores = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar setores', err);
        this.isLoading = false;
        Swal.fire('Erro!', 'Não foi possível carregar a lista de setores.', 'error');
      }
    });
  }

  confirmarExclusao(setor: ServicoBackend): void {
    Swal.fire({
      title: 'Tem certeza?',
      html: `Deseja realmente excluir o setor <strong>${setor.nome}</strong>?<br>Esta ação não pode ser desfeita.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sim, excluir!',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.excluirSetor(setor.id);
      }
    });
  }

  private excluirSetor(id: string): void {
    // Lembre-se de criar o método 'deletarSetor' no seu CatalogoApiService
    this.catalogoService.deletarSetor(id).subscribe({
      next: () => {
        Swal.fire('Excluído!', 'O setor foi removido com sucesso.', 'success');
        this.carregarSetores(); // Recarrega a lista após a exclusão
      },
      error: (err) => {
        console.error('Erro ao excluir setor', err);
        Swal.fire('Erro!', 'Não foi possível excluir o setor. Verifique se ele não está em uso.', 'error');
      }
    });
  }
}