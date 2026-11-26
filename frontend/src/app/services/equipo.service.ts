import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Equipo } from '../models/equipo';

@Injectable({ providedIn: 'root' })
export class EquipoService {

  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/equipos`;

  mios(): Observable<Equipo[]> {
    return this.http.get<Equipo[]>(`${this.url}/mios`);
  }
}