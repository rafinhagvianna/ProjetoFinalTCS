import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-icon-picker-modal',
  standalone: true, 
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './icon-picker-modal.component.html',
  styleUrls: ['./icon-picker-modal.component.scss']
})
export class IconPickerModalComponent implements OnInit {
  @Input() selectedIcon: string = '';
  @Output() iconSelected = new EventEmitter<string>();

  isModalOpen: boolean = false;
  searchTerm: string = '';
  allIcons: string[] = [];
  filteredIcons: string[] = [];

  constructor() { }

  ngOnInit(): void {
    this.allIcons = [
      'fa-solid fa-money-bill-transfer', 'fa-solid fa-credit-card', 'fa-solid fa-wallet',
      'fa-solid fa-piggy-bank', 'fa-solid fa-building-columns', 'fa-solid fa-landmark',
      'fa-solid fa-sack-dollar', 'fa-solid fa-chart-line', 'fa-solid fa-chart-pie',
      'fa-solid fa-lock', 'fa-solid fa-shield-alt', 'fa-solid fa-user-lock',
      'fa-solid fa-file-invoice-dollar', 'fa-solid fa-receipt', 'fa-solid fa-calculator',
      'fa-solid fa-hand-holding-dollar', 'fa-solid fa-coins', 'fa-solid fa-dollar-sign',
      'fa-solid fa-euro-sign', 'fa-solid fa-yen-sign', 'fa-solid fa-pound-sign',
      'fa-solid fa-brazilian-real-sign', 'fa-solid fa-file-contract', 'fa-solid fa-gavel',
      'fa-solid fa-headset', 'fa-solid fa-phone-volume', 'fa-solid fa-circle-info',
      'fa-solid fa-bell', 'fa-solid fa-envelope', 'fa-solid fa-house-chimney-window',
      'fa-solid fa-car-side', 'fa-solid fa-graduation-cap', 'fa-solid fa-briefcase',
      'fa-solid fa-globe', 'fa-solid fa-fingerprint', 'fa-solid fa-qrcode',
      'fa-solid fa-barcode', 'fa-solid fa-money-bill', 'fa-solid fa-arrow-right-arrow-left',
      'fa-solid fa-magnifying-glass-dollar', 'fa-solid fa-percent', 'fa-solid fa-vault',
      'fa-solid fa-money-check-dollar', 'fa-solid fa-file-shield', 'fa-solid fa-search',
      'fa-solid fa-money-bill-alt'
    ].sort();

    this.filteredIcons = [...this.allIcons];
  }

  openModal(): void {
    this.isModalOpen = true;
    this.searchTerm = '';
    this.filterIcons();
  }

  closeModal(): void {
    this.isModalOpen = false;
  }

  selectIcon(iconClass: string): void {
    this.selectedIcon = iconClass;
    this.iconSelected.emit(iconClass);
    this.closeModal();
  }

  filterIcons(): void {
    const lowerCaseSearchTerm = this.searchTerm.toLowerCase();
    this.filteredIcons = this.allIcons.filter(icon =>
      icon.toLowerCase().includes(lowerCaseSearchTerm)
    );
  }

  getIconName(iconClass: string): string {
    const parts = iconClass.split(' ');
    return parts[parts.length - 1].replace('fa-', '');
  }

  getFaIconArray(iconClass: string): (string | string[]) {
    return iconClass; 
  }
}