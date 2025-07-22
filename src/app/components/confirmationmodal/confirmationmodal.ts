import { Component, Input } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { NgbActiveModal, NgbModule } from '@ng-bootstrap/ng-bootstrap'; 

@Component({
  selector: 'app-confirmation-modal',
  standalone: true, 
  imports: [CommonModule, NgbModule],
  templateUrl: './confirmationmodal.html', 
})
export class ConfirmationModalComponent {
  @Input() title: string = 'Confirmação';
  @Input() message: string = 'Tem certeza que deseja realizar esta ação?';
  @Input() confirmButtonText: string = 'Confirmar';

  constructor(public activeModal: NgbActiveModal) {}
}