import { Routes } from '@angular/router';
import { TorneosLista } from './torneos/torneos-lista/torneos-lista';

export const routes: Routes = [
  { path: 'torneos', component: TorneosLista },
  { path: '', redirectTo: 'torneos', pathMatch: 'full' },
];