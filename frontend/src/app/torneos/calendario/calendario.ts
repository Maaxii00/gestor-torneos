import { Component, input, output } from '@angular/core';
import { Ronda } from '../../models/calendario';

@Component({
  selector: 'app-calendario',
  templateUrl: './calendario.html',
  styleUrl: './calendario.scss',
})
export class Calendario {

  readonly rondas = input.required<Ronda[]>();
  readonly esOrganizador = input(false);
  readonly procesando = input(false);

  readonly victoriaRegistrada = output<{ serieId: number; ganadorId: number }>();
}