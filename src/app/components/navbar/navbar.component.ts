import { Component, OnInit } from '@angular/core';
import { CommonModule }       from '@angular/common';
import { Router }             from '@angular/router';

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
  userEmail:      string | null = null;
  userPhoto:      string | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const fullName = localStorage.getItem('userName');
    this.userFirstName = fullName ? fullName.split(' ')[0] : null;
    this.userEmail     = localStorage.getItem('userEmail');
    this.userPhoto     = localStorage.getItem('userPhoto');
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/']);
  }

  onPhotoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      const reader = new FileReader();
      reader.onload = () => {
        const base64 = reader.result as string;
        localStorage.setItem('userPhoto', base64);
        this.userPhoto = base64;
      };
      reader.readAsDataURL(input.files[0]);
    }
  }
  navigateToMenu(): void {
    this.router.navigate(['/menu-cliente']);
  }
}
