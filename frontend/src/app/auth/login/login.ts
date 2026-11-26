import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {

  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly formulario = this.fb.nonNullable.group({
    email: ['', { validators: [Validators.required, Validators.email], updateOn: 'blur' }],
    password: ['', Validators.required],
  });

  protected readonly error = signal<string | null>(null);
  protected readonly enviando = signal(false);

  entrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    this.auth.login(this.formulario.getRawValue()).subscribe({
      next: () => this.router.navigate(['/torneos']),
      error: () => {
        this.error.set('Email o contraseña incorrectos');
        this.enviando.set(false);
      },
    });
  }
}