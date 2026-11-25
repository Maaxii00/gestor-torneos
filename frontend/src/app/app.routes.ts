import { Routes } from '@angular/router';
import { TorneosLista } from './torneos/torneos-lista/torneos-lista';
import { Login } from './auth/login/login'
import { TorneoNuevo } from './torneos/torneo-nuevo/torneo-nuevo'
import { authGuard } from './guards/auth.guard'

export const routes: Routes = [
  { path: 'torneos', component: TorneosLista },
  { path: 'torneos/nuevo', component: TorneoNuevo, canActivate: [authGuard] },
  { path: 'login', component: Login },
  { path: '', redirectTo: 'torneos', pathMatch: 'full' },
];