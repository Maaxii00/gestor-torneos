export interface EquipoResumen {
  id: number;
  nombre: string;
  tag: string;
}

export interface FilaClasificacion {
  posicion: number;
  empateSinResolver: boolean;
  equipo: EquipoResumen;
  seriesJugadas: number;
  seriesGanadas: number;
  seriesPerdidas: number;
  partidasGanadas: number;
  partidasPerdidas: number;
  diferenciaDePartidas: number;
}