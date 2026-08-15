import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import {
  AuthService,
  BranchContextService,
  TranslatePipe,
  NotificationService,
  MATERIAL_IMPORTS,
} from '@supermarket/shared-ui';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, TranslatePipe, ...MATERIAL_IMPORTS],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly branchContext = inject(BranchContextService);
  private readonly router = inject(Router);
  private readonly notify = inject(NotificationService);

  readonly loading = signal(false);

  readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    organizationId: [localStorage.getItem('sm_org_id') ?? ''],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { email, password, organizationId } = this.form.getRawValue();
    if (organizationId) {
      this.branchContext.setOrganizationId(organizationId);
    }
    this.loading.set(true);
    this.auth.login({ email, password }, organizationId || undefined).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.notify.error(err.error?.message ?? 'Login failed');
      },
    });
  }
}
