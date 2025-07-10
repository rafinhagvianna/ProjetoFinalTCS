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


export const routes: Routes = [

  {path: '', redirectTo: 'home', pathMatch: 'full'},

  {path: 'home', component: LandingComponent},

  { path: '', redirectTo: 'login', pathMatch: 'full' },
  
  { path: 'login', component: LoginComponent },

  {path: 'register', component: RegisterComponent},

  {path: 'teste', component: TesteComponent},

  {
    path: 'menu-cliente',
    component: MenuClienteComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'espera/:setorNome',
    component: EsperaAtendimentoComponent,
    canActivate: [AuthGuard]
  },

  {path: 'agendamento', component: AgendamentoComponent},

  { path: 'documentos/upload', component: DocumentoUploadPageComponent },

  
];
