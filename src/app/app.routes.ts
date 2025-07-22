import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { MenuClienteComponent } from './pages/menu-cliente/menu-cliente.component';
import { AuthGuard } from './guards/auth.guard';
import { TesteComponent } from './pages/teste/teste';
import { AgendamentoComponent } from './pages/agendamento/agendamento';
import { LandingComponent } from './pages/landing-page/landing.component';
import { EsperaAtendimentoComponent } from './pages/espera-atendimento1/espera-atendimento1.component';
import { DocumentoUploadPageComponent } from './pages/documentos-upload/documento-upload-page.component';
import { MenuFuncionarioComponent } from './pages/menu-funcionario/menu-funcionario.component';
import { AgendamentoFuncionarioComponent } from './pages/agendamento-funcionario/agendamento-funcionario.component';
import { AgendamentoEditarComponent } from './pages/agendamento-editar.component/agendamento-editar.component';
import { RegisterFuncionarioComponent } from './pages/register/funcionario/registerFuncionario.component';
import { TriagensFuncionarioComponent } from './pages/triagens-funcionario/triagens-funcionario.component';
import { ClienteAtualComponent } from './pages/cliente-atual.component/cliente-atual.component';
import { HistoricoAtendimentosComponent } from './pages/historico-atendimentos/historico-atendimentos.component';
import { VerificarDocumentosComponent } from './pages/verificar-documentos/verificar-documentos.component';
import { CadastroSetorComponent } from './pages/cadastro-setor.component/cadastro-setor.component'; 
import { DetalheAtendimentoComponent } from './pages/detalhe-atendimento/detalhe-atendimento.component';
import { GerenciarSetoresComponent } from './pages/gerenciar-setores/gerenciar-setores.component'; // 1. IMPORTE O NOVO COMPONENTE
import { DashboardFuncionarioComponent } from './pages/dashboard-funcionario/dashboard-funcionario.component'; // 1. IMPORTE O NOVO COMPONENTE

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: LandingComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'teste', component: TesteComponent },
  {
    path: 'menu-cliente',
    component: MenuClienteComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'menu-funcionario',
    component: MenuFuncionarioComponent,
    //canActivate: [AuthGuard] // trocar depois para o token do JWT
  },
  // {
  //   path: 'espera/:setorNome',
  //   component: EsperaAtendimentoComponent,
  //   canActivate: [AuthGuard]
  // },

  // {path: 'agendamento', component: AgendamentoComponent},

  // ALTERAÇÃO 1: A rota de espera agora espera um 'id'
  {
    path: 'espera/:id',
    component: EsperaAtendimentoComponent,
    canActivate: [AuthGuard]
  },
  // ALTERAÇÃO 2: A rota de agendamento agora também espera um 'id'
  {
    path: 'agendamento/:id',
    component: AgendamentoComponent,
    canActivate: [AuthGuard] // É uma boa prática proteger esta rota também
  },

  { path: 'documentos/upload', component: DocumentoUploadPageComponent },

  {
    path: 'menu-funcionario/agendamentos',
    component: AgendamentoFuncionarioComponent,
    // canActivate: [AuthGuard]
  },

  {
    path: 'menu-funcionario/agendamentos/editar/:id',
    component: AgendamentoEditarComponent,
    //canActivate: [AuthGuard]
  },

  {
    path: 'menu-funcionario/register',
    component: RegisterFuncionarioComponent,
    // canActivate: [AuthGuard] // Proteja esta rota se necessário
  },
  {

    path: 'menu-funcionario/triagens',
    component: TriagensFuncionarioComponent,
    // canActivate: [AuthGuard]
  },

  //   path: 'menu-funcionario/cliente-atual/:id', // Esta é a rota fixa que você mencionou
  //   component: ClienteAtualComponent
  //   // Opcional: canActivate: [AuthGuard]
  // }
  {
    path: 'menu-funcionario/cliente-atual/:id', // Esta é a rota fixa que você mencionou
    component: ClienteAtualComponent
    // canActivate: [AuthGuard]
  },

{
  path: 'menu-funcionario/historico-atendimentos',
  component: HistoricoAtendimentosComponent
  // canActivate: [AuthGuard]
},

{
  // Adicionamos uma nova rota que aceita o ID
  path: 'menu-funcionario/cliente-atual/:id',
  component: HistoricoAtendimentosComponent
  // canActivate: [AuthGuard]
},

{
  path: 'menu-funcionario/agendamentos/verificar-documentos/:id',
  component: VerificarDocumentosComponent
},
{
  path: 'menu-funcionario/cadastro-setor',
  component: CadastroSetorComponent
  //canActivate: [AuthGuard]
},

{
    path: 'menu-funcionario/historico-atendimentos/:id',
    component: DetalheAtendimentoComponent
    // canActivate: [AuthGuard] // Adicione o guard se necessário
  },

  {
    path: 'menu-funcionario/gerenciar-setores',
    component: GerenciarSetoresComponent
  },

  {
    path: 'menu-funcionario/dashboard', // 2. ADICIONE A ROTA AQUI
    component: DashboardFuncionarioComponent,
    // canActivate: [AuthGuard]
  }

];
