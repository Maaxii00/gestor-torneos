export interface Equipo {
  id: number;
  nombre: string;
  tag: string;
  capitan: { id: number; nombre: string };
}