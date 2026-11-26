import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EquipoService } from '../../services/equipo.service';
import { Equipo } from '../../models/equipo';

@Component({
  selector: 'app-mis-equipos',
  imports: [ReactiveFormsModule],
  templateUrl: './mis-equipos.html',
  styleUrl: './mis-equipos.scss',
})
export class MisEquipos implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly equipoService = inject(EquipoService);

  protected readonly equipos = signal<Equipo[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);

  protected readonly formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(60)]],
    tag: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(5)]],
  });

  ngOnInit(): void {
    this.equipoService.mios().subscribe({
      next: (equipos) => this.equipos.set(equipos),
    });
  }

  crear(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    const { nombre, tag } = this.formulario.getRawValue();

    this.equipoService.crear({ nombre: nombre.trim(), tag: tag.trim().toUpperCase() }).subscribe({
      next: (nuevo) => {
        this.equipos.update((lista) => [...lista, nuevo]);
        this.formulario.reset();
        this.enviando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.mensaje ?? 'No se pudo crear el equipo');
        this.enviando.set(false);
      },
    });
  }
}