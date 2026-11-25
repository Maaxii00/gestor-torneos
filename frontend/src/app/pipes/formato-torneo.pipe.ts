import { Pipe, PipeTransform } from '@angular/core';
import { FormatoTorneo } from '../models/torneo';

const TEXTOS: Record<FormatoTorneo, string> = {
  LIGA_RAPIDA: 'Liga rápida · Bo1',
  LIGA_CLASICA: 'Liga clásica · Bo3',
  SUIZO: 'Suizo',
};

@Pipe({ name: 'formatoTorneo' })
export class FormatoTorneoPipe implements PipeTransform {
  transform(formato: FormatoTorneo): string {
    return TEXTOS[formato];
  }
}