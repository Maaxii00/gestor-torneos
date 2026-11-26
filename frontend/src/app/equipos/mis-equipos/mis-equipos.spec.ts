import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MisEquipos } from './mis-equipos';

describe('MisEquipos', () => {
  let component: MisEquipos;
  let fixture: ComponentFixture<MisEquipos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisEquipos],
    }).compileComponents();

    fixture = TestBed.createComponent(MisEquipos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
