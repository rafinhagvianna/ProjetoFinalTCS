import { Component, OnInit } from '@angular/core';
import { CommonModule }      from '@angular/common';
import { Router, RouterLink }from '@angular/router';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';

import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import {
  ClienteService,
  ClienteRequest,
  ClienteResponse
} from '../../services/cliente.service';
import Swal from 'sweetalert2';

function senhasCorrespondemValidator(control: AbstractControl): ValidationErrors | null {
  const s = control.get('senha')?.value;
  const c = control.get('confirmarSenha')?.value;
  return s === c ? null : { senhasNaoCorrespondem: true };
}

function cpfValidator(control: AbstractControl): ValidationErrors | null {
  const raw    = control.value || '';
  const digits = raw.replace(/\D/g, '');
  return digits.length === 11 ? null : { cpfInvalido: true };
}

function agenciaValidator(control: AbstractControl): ValidationErrors | null {
  const raw    = control.value || '';
  const digits = raw.replace(/\D/g, '');
  return digits.length === 4 ? null : { agenciaInvalida: true };
}

function contaValidator(control: AbstractControl): ValidationErrors | null {
  const raw    = control.value || '';
  const digits = raw.replace(/\D/g, '');
  return digits.length === 9 ? null : { contaInvalida: true };
}

function emailDeFuncionarioValidator(control: AbstractControl): ValidationErrors | null {
  const email = control.value as string;
  if (email && email.toLowerCase().endsWith('@bankflow.com')) {
    // Retorna um objeto de erro se o e-mail for de funcionário
    return { emailDeFuncionario: true };
  }
  // Retorna null se a validação passar
  return null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    NgxMaskDirective
  ],
  providers: [
    provideNgxMask()
  ],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm!: FormGroup;
  hideRegisterPassword = true;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      nome:           ['', Validators.required],
      cpf:            ['', [ Validators.required, cpfValidator ]],
      telefone:       ['', Validators.required],
      agencia:        ['', [ Validators.required, agenciaValidator ]],
      conta:          ['', [ Validators.required, contaValidator ]],
      email:          ['', [ Validators.required, Validators.email, emailDeFuncionarioValidator ]],
      senha:          ['', [ Validators.required, Validators.minLength(6) ]],
      confirmarSenha: ['', Validators.required]
    },{
      validators: senhasCorrespondemValidator
    });
  }

  onSubmit(): void {
    this.registerForm.markAllAsTouched();
    if (this.registerForm.invalid) {
      Swal.fire({
        icon: 'error',
        title: 'Dados Inválidos!',
        html: this.getErrorMessage(),
        confirmButtonColor: '#c62828'
      });
      return;
    }

    const dto: ClienteRequest = this.registerForm.value;
    this.clienteService.cadastrar(dto).subscribe({
      next: (res: ClienteResponse) => {
        Swal.fire({
          icon: 'success',
          title: 'Cadastro Realizado!',
          text: `Cliente ${res.nome} cadastrado com sucesso!`,
          timer: 2000,
          showConfirmButton: false
        }).then(() => this.router.navigate(['/login']));
      },
      error: err => {
        const msg = err.status === 409
          ? err.error
          : 'Ocorreu uma falha no servidor. Por favor, tente novamente mais tarde.';
        Swal.fire({
          icon: 'error',
          title: 'Falha no Cadastro',
          text: msg,
          confirmButtonColor: '#c62828'
        });
      }
    });
  }

  private getErrorMessage(): string {
    if (this.registerForm.hasError('senhasNaoCorrespondem')) {
      return 'As senhas não correspondem. Por favor, verifique.';
    }
    const c = this.registerForm.controls;
    if (Object.values(c).some(ctrl => ctrl.hasError('required'))) {
      return 'Todos os campos são obrigatórios. Por favor, preencha todos.';
    }
    const msgs: string[] = [];
    if (c['email'].hasError('email')) {
      msgs.push('O formato do e-mail é inválido.');
    }
    if (c['cpf'].hasError('cpfInvalido')) {
      msgs.push('O CPF deve conter exatamente 11 números.');
    }
    if (c['agencia'].hasError('agenciaInvalida')) {
      msgs.push('A agência deve conter exatamente 4 números.');
    }
    if (c['conta'].hasError('contaInvalida')) {
      msgs.push('A conta deve conter exatamente 9 números.');
    }
    if (c['senha'].hasError('minlength')) {
      msgs.push('A senha deve ter no mínimo 6 caracteres.');
    }
    return msgs.join('<br>');
  }

  toggleRegisterPasswordVisibility(): void {
    this.hideRegisterPassword = !this.hideRegisterPassword;
  }

  
}
