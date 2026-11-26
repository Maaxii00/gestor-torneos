import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PartidoService {

  private readonly http = inject(HttpClient);

  registrar(serieId: number, ganadorId: number): Observable<unknown> {
    return this.http.post(`${environment.apiUrl}/series/${serieId}/partidos`, { ganadorId });
  }
}