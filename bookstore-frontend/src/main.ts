/**
 * Angular Application Bootstrap
 * 
 * This is the entry point for the Angular application.
 * bootstrapApplication() bootstraps the root component standalone (no NgModule)
 * 
 * Interview Points:
 * - Standalone components: Angular 14+ feature, no NgModule required
 * - provideHttpClient(): Angular 15+ way to provide HttpClient
 * - provideRouter(): Routing configuration
 */
import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    provideRouter(routes),
    provideAnimations()
  ]
}).catch(err => console.error(err));
