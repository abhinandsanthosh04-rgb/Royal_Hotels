import { Routes } from '@angular/router';
import { AdminLoginComponent } from './admin/admin-login.component';
import { AdminDashboardComponent } from './admin/admin-dashboard.component';
import { adminGuard } from './admin/admin.guard';

export const routes: Routes = [
  { path: 'admin/login', component: AdminLoginComponent },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [adminGuard] },
  { path: 'admin', redirectTo: 'admin/dashboard', pathMatch: 'full' }
];
