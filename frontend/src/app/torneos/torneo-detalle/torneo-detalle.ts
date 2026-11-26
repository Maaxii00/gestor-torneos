import { Component, OnInit, input, signal } from '@angular/core';
import { inject } from '@angular/core';
import { TorneoService } from '../../services/torneo.service';
import { Torneo } from '../../models/torneo';
import { FilaClasificacion } from '../../models/clasificacion';
import { FormatoTorneoPipe } from '../../pipes/formato-torneo.pipe';
import { EstadoTorneoPipe } from '../../pipes/estado-torneo.pipe';

@Component({
  selector: 'app-torneo-detalle',
  imports: [FormatoTorneoPipe, EstadoTorneoPipe],
  templateUrl: './torneo-detalle.html',
  styleUrl: './torneo-detalle.scss',
})
export class TorneoDetalle implements OnInit {

  readonly id = input.required<string>();

  private readonly torneoService = inject(TorneoService);

  protected readonly torneo = signal<Torneo | null>(null);
  protected readonly filas = signal<FilaClasificacion[]>([]);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.id());

    this.torneoService.obtener(id).subscribe({
      next: (torneo) => this.torneo.set(torneo),
      error: () => this.error.set('No se encontró el torneo'),
    });

    this.torneoService.clasificacion(id).subscribe({
      next: (filas) => this.filas.set(filas),
    });
  }
}