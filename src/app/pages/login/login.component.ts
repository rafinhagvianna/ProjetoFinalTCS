import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteService, LoginRequest } from '../../services/cliente.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup; 
  hidePassword = true;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched(); 
      return;
    }

    const credenciais: LoginRequest = this.loginForm.value;

    this.clienteService.login(credenciais).subscribe({
next: (response) => {
  console.log('Resposta do servidor:', response);
  
  Swal.fire({
    icon: 'success',
    title: 'Login Realizado com Sucesso!',
    text: 'Redirecionando para o painel...',
    timer: 1500,
    showConfirmButton: false
  }).then(() => {
    this.router.navigate(['/menu-cliente']);
  });
},
      error: (err) => {
        console.error('Erro no login:', err);
        Swal.fire({
          icon: 'error',
          title: 'Falha no Login',
          text: 'E-mail ou senha incorretos. Verifique seus dados e tente novamente.',
          confirmButtonColor: '#c62828'
        });
      }
    });
  }

  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }
}