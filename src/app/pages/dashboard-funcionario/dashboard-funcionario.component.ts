import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

import { ChartData, ChartOptions, Chart, registerables } from 'chart.js';
import { AnalyticsService , ContagemPorItem} from '../../services/analytics.service';
import { BaseChartDirective } from 'ng2-charts'; // Necessário para standalone
import jsPDF from 'jspdf';
import ChartDataLabels from 'chartjs-plugin-datalabels';


@Component({
  selector: 'app-dashboard-funcionario',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, BaseChartDirective],
  templateUrl: './dashboard-funcionario.component.html',
  styleUrls: ['./dashboard-funcionario.component.scss']
})
export class DashboardFuncionarioComponent implements OnInit, OnDestroy {

  graficoSelecionado: 'servicos' | 'atendimentosDia' = 'servicos';
  @ViewChild('triagemChart') triagemChart?: BaseChartDirective;
  @ViewChild('agendamentoChart') agendamentoChart?: BaseChartDirective;

  public isTriagemDataLoaded = false;
  public isAgendamentoDataLoaded = false;



  public triagemChartData: ChartData<'doughnut'> = { datasets: [] };
  public agendamentoChartData: ChartData<'pie'> = { datasets: [] };

  public chartOptions: ChartOptions = { 
    responsive: true ,
    maintainAspectRatio: false,
    plugins: {
        legend:{
          position: 'top',
          labels: { color: '#f8f9fa', font: { size: 14 } }
        },
        datalabels: {
          color: '#ffffff', 
          font: {
            weight: 'bold', 
            size: 16
          },
          formatter: (value) => {
            return value > 0 ? value: '';
          },
        }
    }
    
};

  constructor(private analyticsService: AnalyticsService) { Chart.register(...registerables, ChartDataLabels);}
  ngOnInit(): void {
    document.body.classList.add('menu-funcionario-bg');
    this.carregarDadosTriagem();
    this.carregarDadosAgendamento();
  }


  ngOnDestroy(): void {
    document.body.classList.remove('menu-funcionario-bg');
  }

  private carregarDadosTriagem(): void {
    this.analyticsService.getServicosMaisUtilizadosTriagem().subscribe({
      next: (data: ContagemPorItem[]) => {
        if (data && data.length > 0) {
          this.triagemChartData = {
            labels: data.map(item => item.item),
            datasets: [{ 
              data: data.map(item => item.quantidade),
              backgroundColor: ['rgba(198, 40, 40, 0.8)', 'rgba(54, 162, 235, 0.8)', 'rgba(255, 206, 86, 0.8)'],
              borderColor: '#1e293b',
              borderWidth: 2
            }]
          };
        }
      },
      error: (err) => console.error('Erro ao carregar dados de triagem', err),
      complete: () => {
        setTimeout(() => {
          this.isTriagemDataLoaded = true; // Atualiza o estado com atraso
          console.log('Triagem carregada:', this.isTriagemDataLoaded);
        }, 500); // Adiciona um atraso de 500ms
      }
    });
  }
  
  private carregarDadosAgendamento(): void {
    this.analyticsService.getServicosMaisUtilizadosAgendamento().subscribe({
      next: (data: ContagemPorItem[]) => {
        if (data && data.length > 0) {
          this.agendamentoChartData = {
            labels: data.map(item => item.item),
            datasets: [{ 
              data: data.map(item => item.quantidade),
              backgroundColor: ['rgba(198, 40, 40, 0.8)', 'rgba(54, 162, 235, 0.8)', 'rgba(255, 206, 86, 0.8)'],
              borderColor: '#1e293b',
              borderWidth: 2
            }]
          };
        }
      },
      error: (err) => console.error('Erro ao carregar dados de agendamento', err),
      complete: () => {
        setTimeout(() => {
          this.isAgendamentoDataLoaded = true; // Atualiza o estado com atraso
          console.log('Agendamento carregado:', this.isAgendamentoDataLoaded);
        }, 500); // Adiciona um atraso de 500ms
      }
    });
  }
}