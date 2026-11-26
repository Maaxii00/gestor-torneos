import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { EquipoService } from '../../services/equipo.service';
import { InscripcionService } from '../../services/inscripcion.service';
import { Equipo } from '../../models/equipo';
import { Inscripcion } from '../../models/inscripcion';
import { TorneoService } from '../../services/torneo.service';
import { Torneo } from '../../models/torneo';
import { FilaClasificacion } from '../../models/clasificacion';
import { FormatoTorneoPipe } from '../../pipes/formato-torneo.pipe';
import { EstadoTorneoPipe } from '../../pipes/estado-torneo.pipe';

@Component({
  selector: 'app-torneo-detalle',
  imports: [FormatoTorneoPipe, EstadoTorneoPipe, ReactiveFormsModule],
  templateUrl: './torneo-detalle.html',
  styleUrl: './torneo-detalle.scss',
})
export class TorneoDetalle implements OnInit {

  readonly id = input.required<string>();

  private readonly torneoService = inject(TorneoService);
  private readonly equipoService = inject(EquipoService);
  private readonly inscripcionService = inject(InscripcionService);
  protected readonly auth = inject(AuthService);

  protected readonly torneo = signal<Torneo | null>(null);
  protected readonly filas = signal<FilaClasificacion[]>([]);
  protected readonly inscripciones = signal<Inscripcion[]>([]);
  protected readonly misEquipos = signal<Equipo[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly errorInscripcion = signal<string | null>(null);

  protected readonly equiposInscribibles = computed(() => {
    const inscritos = new Set(this.inscripciones().map((i) => i.equipo.id));
    return this.misEquipos().filter((e) => !inscritos.has(e.id));
  });

  protected readonly equipoSeleccionado = new FormControl<number | null>(null, Validators.required);

  ngOnInit(): void {
    const id = Number(this.id());

    this.torneoService.obtener(id).subscribe({
      next: (torneo) => this.torneo.set(torneo),
      error: () => this.error.set('No se encontró el torneo'),
    });

    this.torneoService.clasificacion(id).subscribe({
      next: (filas) => this.filas.set(filas),
    });

    this.inscripcionService.deTorneo(id).subscribe({
      next: (inscripciones) => this.inscripciones.set(inscripciones),
    });

    if (this.auth.logueado()) {
      this.equipoService.mios().subscribe({
        next: (equipos) => this.misEquipos.set(equipos),
      });
    }
  }

  inscribir(): void {
    const equipoId = this.equipoSeleccionado.value;
    if (equipoId === null) {
      return;
    }

    this.errorInscripcion.set(null);

    this.inscripcionService.inscribir(Number(this.id()), equipoId).subscribe({
      next: (nueva) => {
        this.inscripciones.update((lista) => [...lista, nueva]);
        this.equipoSeleccionado.reset();
      },
      error: (e: HttpErrorResponse) =>
        this.errorInscripcion.set(e.error?.mensaje ?? 'No se pudo inscribir el equipo'),
    });
  }
}