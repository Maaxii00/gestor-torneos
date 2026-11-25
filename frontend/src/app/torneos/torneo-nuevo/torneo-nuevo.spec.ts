import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TorneoNuevo } from './torneo-nuevo';

describe('TorneoNuevo', () => {
  let component: TorneoNuevo;
  let fixture: ComponentFixture<TorneoNuevo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TorneoNuevo],
    }).compileComponents();

    fixture = TestBed.createComponent(TorneoNuevo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
