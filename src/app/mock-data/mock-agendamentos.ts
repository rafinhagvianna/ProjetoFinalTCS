// import { Agendamento, Triagem, DocumentoPendente } from '../models/agendamento.model'; // Assumindo DocumentoPendente está junto

// // Dados de catálogo de documentos (para referência na formação da lista)
// export const MOCK_DOCUMENTOS_CATALOGO = [
//   { id: '686c462e-11ac-4a95-a4e1-e5ddba52ebd3', nome: 'RG - Registro Geral', exigidoPor: ['SERVICO_A', 'SERVICO_C'] },
//   { id: '686C462E-11AC-4A95-A4E1-E5DDBA52EBD3', nome: 'CPF - Cadastro de Pessoa Física', exigidoPor: ['SERVICO_A', 'SERVICO_B'] },
//   { id: '02A46222-2E01-402D-B725-30998C510ADD', nome: 'Comprovante de Residência', exigidoPor: ['SERVICO_A', 'SERVICO_B', 'SERVICO_C'] },
//   { id: 'DF411365-BF33-439A-99ED-6A2DA3958B73', nome: 'Certidão de Nascimento', exigidoPor: ['SERVICO_B'] },
//   { id: '4A1B2C3D-4E5F-6789-0123-777788889999', nome: 'Comprovante de Renda', exigidoPor: ['SERVICO_C'] },
// ];

// // Documentos pendentes para um agendamento fictício
// const documentosAgendamento1: DocumentoPendente[] = [
//   { documentoCatalogoId: '686c462e-11ac-4a95-a4e1-e5ddba52ebd3', nomeDocumento: 'RG - Registro Geral', status: 'PENDENTE' },
// ];

// const documentosAgendamento2: DocumentoPendente[] = [
//   { documentoCatalogoId: '686C462E-11AC-4A95-A4E1-E5DDBA52EBD3', nomeDocumento: 'CPF - Cadastro de Pessoa Física', status: 'AGUARDANDO_VALIDACAO' },
//   { documentoCatalogoId: '4A1B2C3D-4E5F-6789-0123-777788889999', nomeDocumento: 'Comprovante de Renda', status: 'PENDENTE' },
// ];

// // Agendamentos fictícios
// export const MOCK_AGENDAMENTOS: Agendamento[] = [
//   {
//     id: 'f37fe98d-6295-40c9-bac4-a83f60369825',
//     dataHora: '2025-07-15T10:00:00',
//     servicoNome: 'Abertura de Conta',
//     documentosPendentes: documentosAgendamento1,
//   },
//   {
//     id: 'b1c2d3e4-f5a6-7890-1234-567890abcde1',
//     dataHora: '2025-07-16T14:30:00',
//     servicoNome: 'Solicitação de Empréstimo',
//     documentosPendentes: documentosAgendamento2,
//   },
//   // Mais agendamentos...
// ];

// // Triagens fictícias
// export const MOCK_TRIAGENS: Triagem[] = [
//   {
//     id: 't1d2e3f4-g5h6-7890-1234-567890abcde2',
//     documentosPendentes: [
//       { documentoCatalogoId: 'd04', nomeDocumento: 'Certidão de Nascimento', status: 'PENDENTE' },
//       { documentoCatalogoId: 'd01', nomeDocumento: 'RG - Registro Geral', status: 'APROVADO', urlVisualizacao: 'http://link.para.rg.aprovado.com' },
//     ],
//   },
// ];