import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
  ReactiveFormsModule,
  FormsModule
} from '@angular/forms';

import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';
import { FuncionarioService, FuncionarioRequest, FuncionarioResponse } from '../../../services/funcionario.service';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { NavbarComponent } from '../../../components/navbar/navbar.component';

@Component({
  selector: 'app-register-funcionario',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    NgxMaskDirective,
    NavbarComponent
  ],
  providers: [provideNgxMask()],
  templateUrl: './registerFuncionario.component.html',
  styleUrls: ['./registerFuncionario.component.scss']
})
export class RegisterFuncionarioComponent implements OnInit, OnDestroy {
  registerForm!: FormGroup;
  hideRegisterPassword = true;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private funcionarioService: FuncionarioService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      cpf: ['', [Validators.required, this.cpfInvalidoValidator]],
      senha: ['', Validators.required],
      confirmarSenha: ['', Validators.required]
    }, {
      validators: this.senhasCorrespondemValidator
    });
    document.body.classList.add('menu-funcionario-bg');
  }

  ngOnDestroy(): void {
    document.body.classList.remove('menu-funcionario-bg');
  }

  cpfInvalidoValidator(ctrl: AbstractControl): ValidationErrors | null {
    const raw = (ctrl.value || '').replace(/\D/g, '');
    return raw.length === 11 ? null : { cpfInvalido: true };
  }

  funcionarioEmailValidator(ctrl: AbstractControl): ValidationErrors | null{
    const email: string = ctrl.value ||''
    return email.endsWith('@bankflow.com') ? null : {invalidFuncionarioEmail: true}
  }

  senhasCorrespondemValidator(formGroup: AbstractControl): ValidationErrors | null {
    const senha = formGroup.get('senha')?.value;
    const confirmarSenha = formGroup.get('confirmarSenha')?.value;

    if (senha !== confirmarSenha) {
      
      return { senhasNaoCorrespondem: true };
    }
    
    return null;
  }

  onSubmit(): void {
    if (this.registerForm.invalid || this.isLoading) {
      this.registerForm.markAllAsTouched();
      return;
    }
    const dto: FuncionarioRequest = this.registerForm.value;

    if (!dto.email.endsWith('@bankflow.com')) {
      Swal.fire({
        icon: 'error',
        title: 'E-mail Inválido',
        text: 'O e-mail do funcionário deve terminar com @bankflow.com.',
        confirmButtonColor: '#c62828'
      });
      return;  // interrompe o fluxo de cadastro
    }


    this.isLoading = true;
    this.funcionarioService.cadastrar(dto).subscribe({
      next: (res: FuncionarioResponse) => {
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Cadastro Realizado!',
          text: `Funcionário ${res.nome} cadastrado com sucesso!`,
          timer: 2000,
          showConfirmButton: false
        }).then(() => this.router.navigate(['/menu-funcionario']));
      },
      error: (err: any) => {
        this.isLoading = false;
        const msg =
          err.status === 409
            ? err.error
            : 'Ocorreu uma falha no servidor. Tente novamente mais tarde.';
        Swal.fire({
          icon: 'error',
          title: 'Falha no Cadastro',
          text: msg,
          confirmButtonColor: '#c62828'
        });
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/menu-funcionario']);
  }
}
