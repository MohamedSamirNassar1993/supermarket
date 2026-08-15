import { Injectable, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslationService } from './translation.service';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);
  private readonly i18n = inject(TranslationService);

  success(message: string): void {
    this.snackBar.open(message, this.i18n.t('common.close'), {
      duration: 4000,
      panelClass: ['snack-success'],
    });
  }

  error(message: string): void {
    this.snackBar.open(message, this.i18n.t('common.close'), {
      duration: 6000,
      panelClass: ['snack-error'],
    });
  }
}
