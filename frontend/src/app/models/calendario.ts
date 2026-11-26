import { EquipoResumen } from './clasificacion';

export type EstadoSerie = 'PENDIENTE' | 'EN_CURSO' | 'FINALIZADA';

export interface SerieResumen {
  id: number;
  equipoA: EquipoResumen;
  equipoB: EquipoResumen;
  victoriasEquipoA: number;
  victoriasEquipoB: number;
  estado: EstadoSerie;
  ganador: EquipoResumen | null;
}

export interface Ronda {
  id: number;
  numero: number;
  nombre: string;
  victoriasParaGanarSerie: number;
  fase: string;
  tipoFase: 'LIGA' | 'ELIMINATORIA' | 'SUIZO' | 'DESEMPATE';
  series: SerieResumen[];
}