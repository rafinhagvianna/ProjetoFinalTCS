import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators'; 
import { APP_CONFIG } from '../app.config';

export interface LoginResponse{
  id: string;
  role: string;
  nome: string;
  email: string;
  accessToken: string;
}


export interface LoginRequest {
    email: string;
    senha: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private readonly authUrl = APP_CONFIG.apiUrl+'/auth';
  
  constructor(private http: HttpClient) { }

  // login(credenciais: LoginRequest): Observable<LoginResponse> {
  //   return this.http.post<LoginResponse>(`${this.authUrl}/login`, credenciais)
  //     .pipe(
  //       tap(response => {
  //         localStorage.setItem('isLoggedIn', 'true');
  //         localStorage.setItem('userName', response.nome);
  //         localStorage.setItem('userEmail', response.email);
  //         localStorage.setItem('jwtToken', response.accessToken);
  //       })
  //     );
  // }

  login(credenciais: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.authUrl}/login`, credenciais)
      .pipe(
        tap(response => {
          // 👇 TROCAR 'localStorage' POR 'sessionStorage' AQUI 👇
          sessionStorage.setItem('isLoggedIn', 'true');
          sessionStorage.setItem('userName', response.nome);
          sessionStorage.setItem('userEmail', response.email);
          sessionStorage.setItem('jwtToken', response.accessToken);
          sessionStorage.setItem('userId', response.id);
          sessionStorage.setItem('userRole', response.role);
        })
      );
  }
  
  getJwtToken(): string | null {
    // return localStorage.getItem('jwtToken');
    return sessionStorage.getItem('jwtToken');
  }

  isLoggedIn(): boolean {
    // return localStorage.getItem('isLoggedIn') === 'true';
    return sessionStorage.getItem('isLoggedIn') === 'true';
  }

  getId(): string | null {
    // return localStorage.getItem('userId');
    return sessionStorage.getItem('userId');
  }
  logout(): void {
    // localStorage.removeItem('isLoggedIn');
    // localStorage.removeItem('userName');
    // localStorage.removeItem('userEmail');
    // localStorage.removeItem('jwtToken');

    sessionStorage.removeItem('isLoggedIn');
    sessionStorage.removeItem('userName');
    sessionStorage.removeItem('userEmail');
    sessionStorage.removeItem('jwtToken');
    sessionStorage.removeItem('userId');
    sessionStorage.removeItem('userRole');
  }
  
}