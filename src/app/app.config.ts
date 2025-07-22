import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
// Importe a constante do seu interceptor funcional
import { authInterceptor } from './interceptors/auth.interceptor'; // Ajuste o caminho e o nome aqui

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),

    provideRouter(
      routes,
      withInMemoryScrolling({
        anchorScrolling: 'enabled',
        scrollPositionRestoration: 'enabled'
      })
    ),

    provideHttpClient(
      withInterceptors([
        authInterceptor // Use a constante do interceptor funcional aqui
      ])
    ),

    importProvidersFrom(FormsModule),
  ]
};

export const APP_CONFIG = {
  // apiUrl: 'https://bankflow.ddns-ip.net/api'
  apiUrl: 'http://localhost:9000/api'
};