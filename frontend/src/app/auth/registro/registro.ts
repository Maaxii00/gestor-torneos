import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { contrasenasIguales } from '../../validators/contrasenas-iguales';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.scss',
})
export class Registro {

  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly formulario = this.fb.nonNullable.group(
    {
      nombre: ['', [Validators.required, Validators.maxLength(60)]],
      email: ['', { validators: [Validators.required, Validators.email, Validators.maxLength(120)], updateOn: 'blur' }],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(72)]],
      confirmacion: ['', Validators.required],
    },
    { validators: contrasenasIguales },
  );

  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);

  registrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    const { nombre, email, password } = this.formulario.getRawValue();

    this.auth.registrar({ nombre, email, password })
      .pipe(switchMap(() => this.auth.login({ email, password })))
      .subscribe({
        next: () => this.router.navigate(['/torneos']),
        error: (e: HttpErrorResponse) => {
          this.error.set(e.error?.mensaje ?? 'No se pudo completar el registro');
          this.enviando.set(false);
        },
      });
  }
}