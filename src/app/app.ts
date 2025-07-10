import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterOutlet, CommonModule],
  templateUrl: './app.component.html', 
  styleUrls: ['./app.component.scss'] 
})
export class AppComponent {
  title = 'Projeto----a';
}