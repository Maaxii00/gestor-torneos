import { EquipoResumen } from './clasificacion';

export interface Inscripcion {
  id: number;
  equipo: EquipoResumen;
  torneo: { id: number; nombre: string };
  inscritoEn: string;
}