import { Pipe, PipeTransform } from '@angular/core';
import { EstadoTorneo } from '../models/torneo';

const TEXTOS: Record<EstadoTorneo, string> = {
  ABIERTO: 'Abierto',
  EN_CURSO: 'En curso',
  FINALIZADO: 'Finalizado',
};

@Pipe({ name: 'estadoTorneo' })
export class EstadoTorneoPipe implements PipeTransform {
  transform(estado: EstadoTorneo): string {
    return TEXTOS[estado];
  }
}