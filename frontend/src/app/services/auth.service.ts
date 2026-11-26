import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth';
import { RegistroRequest, Usuario } from '../models/auth';

const CLAVE_TOKEN = 'token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/auth`;

  readonly token = signal<string | null>(localStorage.getItem(CLAVE_TOKEN));
  readonly logueado = computed(() => this.token() !== null);

  login(datos: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.url}/login`, datos).pipe(
      tap((respuesta) => {
        localStorage.setItem(CLAVE_TOKEN, respuesta.token);
        this.token.set(respuesta.token);
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(CLAVE_TOKEN);
    this.token.set(null);
  }

  registrar(datos: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(`${environment.apiUrl}/usuarios`, datos);
  }

  readonly usuarioId = computed<number | null>(() => {
    const token = this.token();
    if (!token) {
      return null;
    }
    try {
        const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        return Number(JSON.parse(atob(payload)).sub);
      } catch {
        return null;
      }
    });
}