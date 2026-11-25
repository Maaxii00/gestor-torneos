import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TorneosLista } from './torneos-lista';

describe('TorneosLista', () => {
  let component: TorneosLista;
  let fixture: ComponentFixture<TorneosLista>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TorneosLista],
    }).compileComponents();

    fixture = TestBed.createComponent(TorneosLista);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
