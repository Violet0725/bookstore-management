import { Routes } from '@angular/router';

/**
 * Application Routes
 * 
 * Defines the URL paths for the application.
 * 
 * Interview Points:
 * - Lazy loading: components are loaded on demand
 * - Route parameters: :id for dynamic segments
 * - Default route: redirects to inventory
 */
export const routes: Routes = [
  {
    path: '',
    redirectTo: 'inventory',
    pathMatch: 'full'
  },
  {
    path: 'inventory',
    loadComponent: () => import('./components/inventory/inventory.component')
      .then(m => m.InventoryComponent)
  },
  {
    path: 'analytics',
    loadComponent: () => import('./components/analytics/analytics.component')
      .then(m => m.AnalyticsDashboardComponent)
  },
  // Wildcard route for 404
  {
    path: '**',
    redirectTo: 'inventory'
  }
];
