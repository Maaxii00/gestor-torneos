import { Routes } from '@angular/router';
import { TorneosLista } from './torneos/torneos-lista/torneos-lista';
import { Login } from './auth/login/login'
import { TorneoNuevo } from './torneos/torneo-nuevo/torneo-nuevo'
import { authGuard } from './guards/auth.guard'
import { TorneoDetalle } from './torneos/torneo-detalle/torneo-detalle'
import { Registro } from './auth/registro/registro'
import { MisEquipos } from './equipos/mis-equipos/mis-equipos'

export const routes: Routes = [
  { path: 'torneos', component: TorneosLista },
  { path: 'torneos/nuevo', component: TorneoNuevo, canActivate: [authGuard] },
  { path: 'login', component: Login },
  { path: '', redirectTo: 'torneos', pathMatch: 'full' },
  { path: 'torneos/nuevo', component: TorneoNuevo, canActivate: [authGuard] },
  { path: 'torneos/:id', component: TorneoDetalle },
  { path: 'registro', component: Registro },
  { path: 'equipos', component: MisEquipos, canActivate: [authGuard] },
];