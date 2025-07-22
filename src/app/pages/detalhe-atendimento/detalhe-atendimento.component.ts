import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { TriagemApiService } from '../../services/triagem-api.service';
import { TriagemCompleta } from '../../services/triagem.interface';

@Component({
  selector: 'app-detalhe-atendimento',
  standalone: true,
  imports: [CommonModule, NavbarComponent, RouterLink],
  templateUrl: './detalhe-atendimento.component.html',
  styleUrls: ['./detalhe-atendimento.component.scss']
})
export class DetalheAtendimentoComponent implements OnInit, OnDestroy {
  
  triagem: TriagemCompleta | null = null;
  isLoading = true;
  errorMsg: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private triagemService: TriagemApiService
  ) {}

  ngOnInit(): void {
    // Adiciona a classe ao body para o tema escuro
    document.body.classList.add('menu-funcionario-bg');

    const triagemId = this.route.snapshot.paramMap.get('id');

    if (triagemId) {
      this.triagemService.getById(triagemId).subscribe({
        next: (data) => {
          this.triagem = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Falha ao buscar detalhes do atendimento', err);
          this.errorMsg = 'Não foi possível carregar os detalhes deste atendimento.';
          this.isLoading = false;
        }
      });
    } else {
      this.errorMsg = 'Nenhum ID de atendimento foi fornecido.';
      this.isLoading = false;
    }
  }

  ngOnDestroy(): void {
    // Limpa a classe do body ao sair
    document.body.classList.remove('menu-funcionario-bg');
  }

  // Navega de volta para a lista de histórico
  voltar(): void {
    this.router.navigate(['/menu-funcionario/historico-atendimentos']);
  }

  // Helper para formatar datas (opcional, mas útil)
  getFormattedDateTime(dateTimeString?: string | null): string {
    if (!dateTimeString) return 'N/A';
    const date = new Date(dateTimeString);
    return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  }
}