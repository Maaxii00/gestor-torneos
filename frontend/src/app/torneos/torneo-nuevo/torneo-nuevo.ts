import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { TorneoService } from '../../services/torneo.service';
import { FormatoTorneo } from '../../models/torneo';

@Component({
  selector: 'app-torneo-nuevo',
  imports: [ReactiveFormsModule],
  templateUrl: './torneo-nuevo.html',
  styleUrl: './torneo-nuevo.scss',
})
export class TorneoNuevo {

  private readonly fb = inject(FormBuilder);
  private readonly torneoService = inject(TorneoService);
  private readonly router = inject(Router);

  protected readonly formatos: { valor: FormatoTorneo; texto: string }[] = [
    { valor: 'LIGA_RAPIDA', texto: 'Liga rápida (Bo1)' },
    { valor: 'LIGA_CLASICA', texto: 'Liga clásica (Bo3)' },
    { valor: 'SUIZO', texto: 'Suizo' },
  ];

  protected readonly formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(120)]],
    formato: ['LIGA_CLASICA' as FormatoTorneo, Validators.required],
  });

  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);

  crear(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    this.torneoService.crear(this.formulario.getRawValue()).subscribe({
      next: () => this.router.navigate(['/torneos']),
      error: (e: HttpErrorResponse) => {
        this.error.set(e.error?.mensaje ?? 'No se pudo crear el torneo');
        this.enviando.set(false);
      },
    });
  }
}