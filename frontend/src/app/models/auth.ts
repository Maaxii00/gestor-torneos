export interface LoginRequest {
  email: string;
  password: string;
}

export interface Usuario {
  id: number;
  email: string;
  nombre: string;
  rol: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  usuario: Usuario;
}

export interface RegistroRequest {
  nombre: string;
  email: string;
  password: string;
}