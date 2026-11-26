import { Component, input } from '@angular/core';
import { Ronda } from '../../models/calendario';

@Component({
  selector: 'app-calendario',
  templateUrl: './calendario.html',
  styleUrl: './calendario.scss',
})
export class Calendario {

  readonly rondas = input.required<Ronda[]>();
}