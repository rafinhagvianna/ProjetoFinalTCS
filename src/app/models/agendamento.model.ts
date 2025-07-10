export interface Agendamento {
  id: string;
  usuarioId: string;
  nomeClienteSnapshot: string;
  atendenteId: string | null;
  servicoId: string;
  nomeServicoSnapshot: string;
  dataHora: string; 
  atendidoEm: string | null;
  observacoes: string | null;
  criadoEm: string;
  status: 'AGENDADO' | 'CANCELADO' | 'ATENDIDO' | 'AUSENTE'; 
  documentosPendentes: DocumentoPendente[]; 
}

export interface DocumentoPendente {
  id: string;
  documentoCatalogoId: string;
  nomeDocumentoSnapshot: string; 
  status: 'PENDENTE' | 'APROVADO' | 'REJEITADO' | 'ENVIADO' | 'AGUARDANDO_VALIDACAO'; 
  observacao: string | null;
  urlDocumento: string | null; 
}


export interface TipoDocumentoCatalogo {
  id: string; 
  nome: string;
  descricao: string; 
  isObrigatorioPorPadrao: boolean; 
  isAtivo: boolean; 
}
