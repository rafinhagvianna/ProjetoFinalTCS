// import {
//   HttpRequest,
//   HttpHandlerFn,
//   HttpEvent,
//   HttpInterceptorFn,
//   HttpErrorResponse
// } from '@angular/common/http';
// import { Observable, throwError } from 'rxjs';
// import { catchError } from 'rxjs/operators';
// import { ClienteService } from '../services/cliente.service';
// import { inject } from '@angular/core';
// import { Router } from '@angular/router';


// export const authInterceptor: HttpInterceptorFn = (request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
//   console.log('AuthInterceptor: Iniciando para URL:', request.url);

//   const clienteService = inject(ClienteService);
//   const router = inject(Router);
//   const authToken = clienteService.getJwtToken();
//   //const authToken = 'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQ0xJRU5URSIsImZ1bGxOYW1lIjoidGVzdGUiLCJlbWFpbCI6InRlc3RlZUBnbWFpbC5jb20iLCJzdWIiOiI3YTFmNDFkOS01NWRlLTQ4ZWItOGJhYS0yNmEwNWM5MWRhZWYiLCJpYXQiOjE3NTI2NzI1MDIsImV4cCI6MTc1Mjc1ODkwMn0.lDWirlgUBIB1JXZfCzzhIagrIV1ayLDT-ZJ44jZ4-w4';

//   if (authToken) {
//     console.log('AuthInterceptor: Token JWT encontrado, adicionando cabeçalho Authorization.');
//     request = request.clone({
//       setHeaders: {
//         Authorization: `Bearer ${authToken}`
//       }
//     });
//   } else {
//     console.log('AuthInterceptor: Nenhum token JWT encontrado.');
//   }

//   return next(request).pipe(
//     catchError((error: HttpErrorResponse) => {
//       console.error('AuthInterceptor: Erro capturado no catchError. Status:', error.status, 'Erro completo:', error);

//       // Modificado para incluir status 0 na condição de redirecionamento
//       if (error.status === 401 || error.status === 403 || error.status === 0) {
//         // Você pode adicionar uma condição extra aqui se quiser diferenciar o status 0
//         // Por exemplo, se (error.status === 0 && error.url === null) para um erro de rede genérico
//         // Mas para seu caso, a intenção é redirecionar quando não há token e a requisição falha.

//         console.log('AuthInterceptor: Status 0/401/403 detectado! Iniciando logout e redirecionamento.');

//         clienteService.logout(); // Limpa os tokens e o estado de login
//         router.navigate(['/login']); // Redireciona para a página de login
        
//         // Mensagem mais específica dependendo do status (opcional, mas melhor para UX)
//         if (error.status === 0) {
//             alert('Erro de conexão ou sessão inválida. Por favor, faça login novamente.');
//         } else {
//             alert('Sua sessão expirou ou você não tem permissão. Por favor, faça login novamente.');
//         }
//       } else {
//         console.log('AuthInterceptor: Erro capturado, mas não é 0, 401 nem 403. Status:', error.status);
//       }
//       // Re-lança o erro para que o componente que fez a requisição original possa lidar com ele também
//       return throwError(() => error);
//     })
//   );
// };

import {
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpInterceptorFn,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { LoginComponent } from '../pages/login/login.component';
import { LoginService } from '../services/login.service';

export const authInterceptor: HttpInterceptorFn = (request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
  console.log('AuthInterceptor: Iniciando para URL:', request.url);

  const loginSer = inject(LoginService);
  const router = inject(Router);
  const authToken = loginSer.getJwtToken();

  if (authToken) {
    console.log('AuthInterceptor: Token JWT encontrado, adicionando cabeçalho Authorization.');
    request = request.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });
  } else {
    console.log('AuthInterceptor: Nenhum token JWT encontrado.');
  }

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('AuthInterceptor: Erro capturado no catchError. Status:', error.status, 'Erro completo:', error);

      if (error.status === 401 || error.status === 403 || error.status === 0) {


        loginSer.logout(); // Limpa os tokens e o estado de login
        router.navigate(['/login']); // Redireciona para a página de login
        
        if (error.status === 0) {
            // alert('Erro de conexão ou sessão inválida. Por favor, faça login novamente.');
        } else {
            // alert('Sua sessão expirou ou você não tem permissão. Por favor, faça login novamente.');
        }
      } 
      return throwError(() => error);
    })
  );
};