import { DocumentoPendente } from "./agendamento.model";

export interface Triagem {
  id: string;
  clienteId: string;
  servicoId: string;
  nomeClienteSnapshot: string;
  nomeServicoSnapshot: string; 
  status: string; 
  horarioSolicitacao: string;
  horarioEstimadoAtendimento: string;
  tempoEstimadoMinutos: number;
  prioridade: number;
  documentosPendentes: DocumentoPendente[];
}