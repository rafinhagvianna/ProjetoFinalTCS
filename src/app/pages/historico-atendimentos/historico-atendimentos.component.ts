import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { Triagem, TriagemApiService } from '../../services/triagem-api.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- 1. IMPORTAR FormsModule
import { TriagemCompleta } from '../../services/triagem.interface';
import jsPDF from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';
import autoTable from 'jspdf-autotable';

@Component({
  selector: 'app-historico-atendimentos',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FormsModule], // <-- 2. ADICIONAR FormsModule AOS IMPORTS
  templateUrl: './historico-atendimentos.component.html',
  styleUrl: './historico-atendimentos.component.scss'
})
export class HistoricoAtendimentosComponent implements OnInit, OnDestroy {

  private historicoCompleto: TriagemCompleta[] = [];
  public historicoExibido: TriagemCompleta[] = [];
  public termoBusca: string = '';
  public isLoading = true;
  public colunaOrdenada: string = 'horarioSolicitacao';
  public direcaoOrdenacao: 'asc' | 'desc' = 'desc';

  constructor(
    private triagemService: TriagemApiService, 
    private router: Router
  ) { }

  ngOnInit(): void {
    document.body.classList.add('menu-funcionario-bg');

    this.triagemService.getHistorico().subscribe({
      next: (triagensFinalizadas) => {
        this.historicoCompleto = triagensFinalizadas;
        this.historicoExibido = [...this.historicoCompleto];
        this.ordenarDados();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar histórico', err);
        this.isLoading = false;
      }
    });
  }

  filtrarHistorico(): void {
    const termo = this.termoBusca.toLowerCase().trim();

    if (!termo) {
      this.historicoExibido = [...this.historicoCompleto];
      
    }else{
      this.historicoExibido = this.historicoCompleto.filter(h => {
        const nomeCliente = (h.nomeClienteSnapshot || '').toLowerCase();
        const nomeServico = (h.nomeServicoSnapshot || '').toLowerCase();
        return nomeCliente.includes(termo) || nomeServico.includes(termo);
      });
    }
    this.ordenarDados(); 
  }
  ordenarPor(coluna: string): void {
    if (this.colunaOrdenada === coluna) {
      this.direcaoOrdenacao = this.direcaoOrdenacao === 'asc' ? 'desc' : 'asc';
    } else {
      this.colunaOrdenada = coluna;
      this.direcaoOrdenacao = coluna === 'horarioSolicitacao' ? 'desc' : 'asc';
    }
    this.ordenarDados();
  }
  
  private ordenarDados(): void {
    this.historicoExibido.sort((a, b) => {
      let valorA, valorB;

      
      if (this.colunaOrdenada === 'nomeClienteSnapshot') {
        valorA = (a.nomeClienteSnapshot || '').toLowerCase();
        valorB = (b.nomeClienteSnapshot || '').toLowerCase();
        return this.direcaoOrdenacao === 'asc' ? valorA.localeCompare(valorB) : valorB.localeCompare(valorA);
      }
      
      if (this.colunaOrdenada === 'horarioSolicitacao') {
        valorA = new Date(a.horarioSolicitacao).getTime();
        valorB = new Date(b.horarioSolicitacao).getTime();
        return this.direcaoOrdenacao === 'asc' ? valorA - valorB : valorB - valorA;
      }

      return 0;
    });
  }
  ngOnDestroy(): void {
    document.body.classList.remove('menu-funcionario-bg');
  }

  retornar(): void {
    this.router.navigate(['/menu-funcionario/triagens']);
  }
  verDetalhes(atendimento: TriagemCompleta): void {
  this.router.navigate(['/menu-funcionario/historico-atendimentos', atendimento.id]);
  }

  exportarParaExcel(): void {
    // 1. Mapeia os dados da tabela para um formato mais simples
    const dadosParaExportar = this.historicoExibido.map(h => ({
      Cliente: h.nomeClienteSnapshot,
      'Serviço Realizado': h.nomeServicoSnapshot,
      Status: h.status,
      'Data da Solicitação': new Date(h.horarioSolicitacao).toLocaleString('pt-BR')
    }));

    // 2. Cria uma "planilha" em memória com os dados
    const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(dadosParaExportar);

    // 3. Cria um "livro" (arquivo Excel) e adiciona a planilha
    const workbook: XLSX.WorkBook = { Sheets: { 'Histórico': worksheet }, SheetNames: ['Histórico'] };

    // 4. Gera o arquivo e inicia o download
    XLSX.writeFile(workbook, 'Historico_Atendimentos.xlsx');
  }

  exportarParaPdf(): void {
    const doc = new jsPDF();
    
    // Converte a tabela para o formato que a jspdf-autotable entende
    const dadosTabela = this.historicoExibido.map(h => [
      h.nomeClienteSnapshot,
      h.nomeServicoSnapshot,
      h.status,
      new Date(h.horarioSolicitacao).toLocaleString('pt-BR')
    ]);

    autoTable(doc, {
      head: [['Cliente', 'Serviço Realizado', 'Status', 'Data da Solicitação']],
      body: dadosTabela,
      startY: 20, // Posição inicial da tabela
      didDrawPage: (data: any) => {
        // Adiciona um título ao PDF
        doc.text('Histórico de Atendimentos - BankFlow', data.settings.margin.left, 15);
      }
    });

    // Inicia o download do arquivo PDF
    doc.save('Historico_Atendimentos.pdf');
  }
}