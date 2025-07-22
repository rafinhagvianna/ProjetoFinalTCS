import { Component, OnInit, ViewChild, ElementRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap'; 
import { ConfirmationModalComponent } from '../../components/confirmationmodal/confirmationmodal'; 
import { ClienteService } from '../../services/cliente.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
  isDropdownOpen = false;
  userFirstName: string | null = null;
  userEmail: string | null = null;
  userPhoto: string | null = null;
  userRole: string | null = null;

  @ViewChild('fileInput') fileInput!: ElementRef; 

  constructor(
    private router: Router,
    private modalService: NgbModal ,
    private clienteService: ClienteService
  ) {}

  // ngOnInit(): void {
  //   const fullName = localStorage.getItem('userName');
  //   this.userFirstName = fullName ? fullName.split(' ')[0] : null;
  //   this.userEmail = localStorage.getItem('userEmail');
  //   this.userPhoto = localStorage.getItem('userPhoto');
  // }

  ngOnInit(): void {
    // 👇 ALTERAÇÃO AQUI: Lendo do sessionStorage 👇
    const fullName = sessionStorage.getItem('userName');
    this.userFirstName = fullName ? fullName.split(' ')[0] : null;
    this.userEmail = sessionStorage.getItem('userEmail');
    this.userPhoto = sessionStorage.getItem('userPhoto');
    this.userRole = sessionStorage.getItem('userRole');

    console.log('User Role do sessionStorage:', this.userRole);
  }

  isCliente(): boolean {
    return this.userRole === 'CLIENTE';
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  confirmLogout(): void {
    const modalRef = this.modalService.open(ConfirmationModalComponent, { centered: true });

    modalRef.componentInstance.title = 'Confirmação de Saída';
    modalRef.componentInstance.message = 'Tem certeza que deseja <strong>sair</strong> do BankFlow?'; 
    modalRef.componentInstance.confirmButtonText = 'Sim, sair';

    modalRef.result.then(
      (result) => {
        if (result === true) {
          console.log('User confirmed logout.');
          this.performLogout(); 
        } else {
          console.log('User canceled logout.');
        }
      },
      (reason) => {
        console.log(`Logout confirmation modal dismissed by: ${reason}.`);
      }
    );
  }

  private performLogout(): void {
    // localStorage.clear();
    sessionStorage.clear();
    this.router.navigate(['/']);
    console.log('Logout feito com sucesso.');
  }

  // onPhotoSelected(event: Event): void {
  //   const input = event.target as HTMLInputElement;
  //   if (input.files && input.files.length) {
  //     const reader = new FileReader();
  //     reader.onload = () => {
  //       const base64 = reader.result as string;
  //       localStorage.setItem('userPhoto', base64);
  //       this.userPhoto = base64;
  //     };
  //     reader.readAsDataURL(input.files[0]);
  //   }
  // }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    // 1. Pega o ID do usuário do sessionStorage
    const clienteId = sessionStorage.getItem('userId');

    if (file && clienteId) {
      // 2. Chama o serviço para fazer o upload do arquivo para o backend
      this.clienteService.uploadFoto(clienteId, file).subscribe({
        next: (fotoBase64: string) => {
          // 3. Em caso de sucesso, atualiza a foto na tela e no sessionStorage
          this.userPhoto = fotoBase64;
          sessionStorage.setItem('userPhoto', fotoBase64);
          
          Swal.fire({
            icon: 'success',
            title: 'Foto Atualizada!',
            timer: 1500,
            showConfirmButton: false
          });
        },
        error: (err: any) => {
          console.error('Erro ao fazer upload da foto', err);
          Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Não foi possível atualizar sua foto. Tente novamente.',
            confirmButtonColor: '#c62828'
          });
        }
      });
    } else if (!clienteId) {
        console.error('ID do usuário não encontrado no sessionStorage. Não é possível fazer o upload.');
    }
  }

  // navigateToMenu(): void {
  //   this.router.navigate(['/menu-cliente']);
  // }

  navigateToMenu(): void {
    const url = this.router.url;
  
    // Se estiver em qualquer rota que comece com '/menu-funcionario'
    if (url.startsWith('/menu-funcionario')) {
      this.router.navigate(['/menu-funcionario']);
    } else {
      // Para outras telas, segue para menu-cliente (ou outra rota desejada)
      this.router.navigate(['/menu-cliente']);
    }
  }
}