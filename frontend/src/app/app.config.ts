import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    // withFetch() uses Angular 22's native Fetch API integration.
    // The rendering fix for zoneless async is handled in app.ts via
    // ChangeDetectorRef.markForCheck() in every HTTP subscription callback.
    provideHttpClient(withFetch()),
  ]
};
