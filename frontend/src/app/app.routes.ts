import { Routes } from '@angular/router';
import { TorneosLista } from './torneos/torneos-lista/torneos-lista';
import { Login } from './auth/login/login'

export const routes: Routes = [
  { path: 'torneos', component: TorneosLista },
  { path: 'login', component: Login },
  { path: '', redirectTo: 'torneos', pathMatch: 'full' },
];