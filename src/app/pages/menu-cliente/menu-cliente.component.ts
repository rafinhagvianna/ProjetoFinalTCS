import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Router } from '@angular/router';

interface Setor {
  nome: string;
  descricao: string;
  iconClass: string;
  tempo: number; 
  rota: string;  
}

@Component({
  selector: 'app-menu-cliente',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './menu-cliente.component.html',
  styleUrls: ['./menu-cliente.component.scss']
})
export class MenuClienteComponent {

  constructor(private router: Router) {}

  setores: Setor[] = [
    {
      nome: 'Informações Gerais',
      descricao: 'Dúvidas sobre produtos, taxas, horários, etc.',
      iconClass: 'fa fa-info-circle',
      tempo: 300, 
      rota: '/espera'
    },
    {
      nome: 'Abertura e Encerramento de Conta',
      descricao: 'Procedimentos para novos e antigos clientes.',
      iconClass: 'fa fa-university',
      tempo: 1200, 
      rota: '/agendamento'
    },
    {
      nome: 'Bloqueio e Desbloqueio de Cartão',
      descricao: 'Procedimentos de segurança para seu cartão.',
      iconClass: 'fa fa-credit-card',
      tempo: 30, 
      rota: '/espera'
    },
    {
      nome: 'Análise de Fraude',
      descricao: 'Investigação de transações não reconhecidas.',
      iconClass: 'fa fa-search',
      tempo: 1800,
      rota: '/agendamento'
    },
    {
      nome: 'Renegociação de Dívidas',
      descricao: 'Negociação de débitos, parcelamentos e descontos.',
      iconClass: 'fa fa-money-bill-alt',
      tempo: 300,
      rota: '/espera'
    },
    {
      nome: 'Suporte ao App/Internet Banking',
      descricao: 'Ajuda com acesso, funcionalidades ou erros no sistema.',
      iconClass: 'fa fa-headset',
      tempo: 300, 
      rota: '/espera'
    }
  ];

  selecionarSetor(setor: Setor): void {
    const rotaDestino = [setor.rota, setor.nome];
    const queryParams = { tempo: setor.tempo };

    this.router.navigate(rotaDestino, { queryParams });
  }
}
