import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { TranslatePipe } from '@supermarket/shared-ui';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [MatCardModule, TranslatePipe],
  template: `
    <div class="page-header">
      <h1>{{ 'nav.dashboard' | translate }}</h1>
    </div>
    <mat-card>
      <mat-card-content>
        <p>Micro frontend shell — Phase 0 foundation</p>
      </mat-card-content>
    </mat-card>
  `,
  styles: `
    .page-header h1 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 500;
    }
  `,
})
export class DashboardComponent {}
