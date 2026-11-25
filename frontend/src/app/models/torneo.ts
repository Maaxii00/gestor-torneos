export interface Torneo {
  id: number;
  nombre: string;
  estado: 'ABIERTO' | 'EN_CURSO' | 'FINALIZADO';
  formato: 'LIGA_RAPIDA' | 'LIGA_CLASICA' | 'SUIZO';
  organizador: { id: number; email: string; nombre: string; rol: string };
  creadoEn: string;
}