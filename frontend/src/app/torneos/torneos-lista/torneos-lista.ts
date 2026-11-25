import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { TorneoService } from '../../services/torneo.service';
import { Torneo } from '../../models/torneo';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-torneos-lista',
  imports: [RouterLink],
  templateUrl: './torneos-lista.html',
  styleUrl: './torneos-lista.scss',
})
export class TorneosLista implements OnInit {

  private readonly torneoService = inject(TorneoService);
  protected readonly auth = inject(AuthService);
  protected readonly torneos = signal<Torneo[]>([]);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.torneoService.listar().subscribe({
      next: (torneos) => this.torneos.set(torneos),
      error: () => this.error.set('No se pudieron cargar los torneos'),
    });
  }
}