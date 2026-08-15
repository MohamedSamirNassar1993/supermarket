import { Routes } from '@angular/router';
import { loadRemoteModule } from '@angular-architects/native-federation';
import { authGuard, guestGuard } from '@supermarket/shared-ui';

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () =>
      loadRemoteModule('mf-auth', './routes').then((m) => m.AUTH_ROUTES),
    canActivate: [guestGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./layout/main-layout/main-layout.component').then((m) => m.MainLayoutComponent),
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'catalog',
        loadChildren: () =>
          loadRemoteModule('mf-catalog', './routes').then((m) => m.REMOTE_ROUTES),
      },
      {
        path: 'inventory',
        loadChildren: () =>
          loadRemoteModule('mf-inventory', './routes').then((m) => m.REMOTE_ROUTES),
      },
      {
        path: 'procurement',
        loadChildren: () =>
          loadRemoteModule('mf-procurement', './routes').then((m) => m.REMOTE_ROUTES),
      },
      {
        path: 'sales',
        loadChildren: () =>
          loadRemoteModule('mf-sales', './routes').then((m) => m.REMOTE_ROUTES),
      },
      {
        path: 'operations',
        loadChildren: () =>
          loadRemoteModule('mf-operations', './routes').then((m) => m.REMOTE_ROUTES),
      },
      {
        path: 'analytics',
        loadChildren: () =>
          loadRemoteModule('mf-analytics', './routes').then((m) => m.REMOTE_ROUTES),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
