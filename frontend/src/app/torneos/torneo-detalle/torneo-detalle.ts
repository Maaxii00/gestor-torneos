import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TorneoService } from '../../services/torneo.service';
import { EquipoService } from '../../services/equipo.service';
import { InscripcionService } from '../../services/inscripcion.service';
import { PartidoService } from '../../services/partido.service';
import { AuthService } from '../../services/auth.service';
import { Torneo } from '../../models/torneo';
import { FilaClasificacion } from '../../models/clasificacion';
import { Ronda } from '../../models/calendario';
import { Equipo } from '../../models/equipo';
import { Inscripcion } from '../../models/inscripcion';
import { FormatoTorneoPipe } from '../../pipes/formato-torneo.pipe';
import { EstadoTorneoPipe } from '../../pipes/estado-torneo.pipe';
import { Calendario } from '../calendario/calendario';

@Component({
  selector: 'app-torneo-detalle',
  imports: [FormatoTorneoPipe, EstadoTorneoPipe, ReactiveFormsModule, Calendario],
  templateUrl: './torneo-detalle.html',
  styleUrl: './torneo-detalle.scss',
})
export class TorneoDetalle implements OnInit {

  readonly id = input.required<string>();

  private readonly torneoService = inject(TorneoService);
  private readonly equipoService = inject(EquipoService);
  private readonly inscripcionService = inject(InscripcionService);
  private readonly partidoService = inject(PartidoService);
  protected readonly auth = inject(AuthService);

  protected readonly torneo = signal<Torneo | null>(null);
  protected readonly filas = signal<FilaClasificacion[]>([]);
  protected readonly rondas = signal<Ronda[]>([]);
  protected readonly inscripciones = signal<Inscripcion[]>([]);
  protected readonly misEquipos = signal<Equipo[]>([]);

  protected readonly error = signal<string | null>(null);
  protected readonly errorInscripcion = signal<string | null>(null);
  protected readonly errorAccion = signal<string | null>(null);
  protected readonly procesando = signal(false);

  protected readonly equiposInscribibles = computed(() => {
    const inscritos = new Set(this.inscripciones().map((i) => i.equipo.id));
    return this.misEquipos().filter((e) => !inscritos.has(e.id));
  });

  protected readonly esOrganizador = computed(() => {
    const t = this.torneo();
    return t !== null && t.organizador.id === this.auth.usuarioId();
  });

  protected readonly seriesPendientes = computed(() =>
    this.rondas().flatMap((r) => r.series).filter((s) => s.estado !== 'FINALIZADA').length,
  );

  protected readonly hayEmpatesSinResolver = computed(() =>
    this.filas().some((f) => f.empateSinResolver),
  );

  protected readonly equipoSeleccionado = new FormControl<number | null>(null, Validators.required);
  protected readonly victoriasDesempate = new FormControl(1, { nonNullable: true });

  private get torneoId(): number {
    return Number(this.id());
  }

  ngOnInit(): void {
    this.cargar();

    if (this.auth.logueado()) {
      this.equipoService.mios().subscribe({
        next: (equipos) => this.misEquipos.set(equipos),
      });
    }
  }

  private cargar(): void {
    const id = this.torneoId;

    this.torneoService.obtener(id).subscribe({
      next: (torneo) => this.torneo.set(torneo),
      error: () => this.error.set('No se encontró el torneo'),
    });
    this.torneoService.clasificacion(id).subscribe({
      next: (filas) => this.filas.set(filas),
    });
    this.torneoService.calendario(id).subscribe({
      next: (rondas) => this.rondas.set(rondas),
    });
    this.inscripcionService.deTorneo(id).subscribe({
      next: (inscripciones) => this.inscripciones.set(inscripciones),
    });
  }

  inscribir(): void {
    const equipoId = this.equipoSeleccionado.value;
    if (equipoId === null) {
      return;
    }
    this.errorInscripcion.set(null);

    this.inscripcionService.inscribir(this.torneoId, equipoId).subscribe({
      next: (nueva) => {
        this.inscripciones.update((lista) => [...lista, nueva]);
        this.equipoSeleccionado.reset();
      },
      error: (e: HttpErrorResponse) =>
        this.errorInscripcion.set(e.error?.mensaje ?? 'No se pudo inscribir el equipo'),
    });
  }

  registrarVictoria(evento: { serieId: number; ganadorId: number }): void {
    this.ejecutar(this.partidoService.registrar(evento.serieId, evento.ganadorId));
  }

  iniciar(): void {
    this.ejecutar(this.torneoService.iniciar(this.torneoId));
  }

  organizarDesempate(): void {
    this.ejecutar(this.torneoService.organizarDesempate(this.torneoId, this.victoriasDesempate.value));
  }

  finalizar(): void {
    this.ejecutar(this.torneoService.finalizar(this.torneoId));
  }

  private ejecutar(accion: Observable<unknown>): void {
    this.procesando.set(true);
    this.errorAccion.set(null);

    accion.subscribe({
      next: () => {
        this.procesando.set(false);
        this.cargar();
      },
      error: (e: HttpErrorResponse) => {
        this.errorAccion.set(e.error?.mensaje ?? 'No se pudo completar la acción');
        this.procesando.set(false);
      },
    });
  }
}