export type FormatoTorneo = 'LIGA_RAPIDA' | 'LIGA_CLASICA' | 'SUIZO';
export type EstadoTorneo = 'ABIERTO' | 'EN_CURSO' | 'FINALIZADO';

export interface Torneo {
  id: number;
  nombre: string;
  estado: EstadoTorneo;
  formato: FormatoTorneo;
  organizador: { id: number; nombre: string };
  creadoEn: string;
}

export interface CrearTorneoRequest {
  nombre: string;
  formato: FormatoTorneo;
}