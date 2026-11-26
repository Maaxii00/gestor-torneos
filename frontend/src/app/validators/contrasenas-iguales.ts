import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const contrasenasIguales: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const password = grupo.get('password')?.value;
  const confirmacion = grupo.get('confirmacion')?.value;
  return password === confirmacion ? null : { contrasenasDistintas: true };
};