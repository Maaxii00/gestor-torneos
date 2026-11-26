import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Torneo, CrearTorneoRequest } from '../models/torneo';
import { FilaClasificacion } from '../models/clasificacion';
import { Ronda } from '../models/calendario';

@Injectable({ providedIn: 'root' })
export class TorneoService {

  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/torneos`;

  listar(): Observable<Torneo[]> {
    return this.http.get<Torneo[]>(this.url);
  }

  crear(datos: CrearTorneoRequest): Observable<Torneo> {
    return this.http.post<Torneo>(this.url, datos);
  }

  obtener(id: number): Observable<Torneo> {
    return this.http.get<Torneo>(`${this.url}/${id}`);
  }

  clasificacion(id: number): Observable<FilaClasificacion[]> {
    return this.http.get<FilaClasificacion[]>(`${this.url}/${id}/clasificacion`);
  }

  calendario(id: number): Observable<Ronda[]> {
    return this.http.get<Ronda[]>(`${this.url}/${id}/calendario`);
  }
}