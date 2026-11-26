import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Inscripcion } from '../models/inscripcion';

@Injectable({ providedIn: 'root' })
export class InscripcionService {

  private readonly http = inject(HttpClient);

  deTorneo(torneoId: number): Observable<Inscripcion[]> {
    return this.http.get<Inscripcion[]>(`${environment.apiUrl}/torneos/${torneoId}/inscripciones`);
  }

  inscribir(torneoId: number, equipoId: number): Observable<Inscripcion> {
    return this.http.post<Inscripcion>(`${environment.apiUrl}/inscripciones`, { torneoId, equipoId });
  }
}