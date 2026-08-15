import { Injectable, inject, signal, computed } from '@angular/core';
import { ApiService } from './api.service';
import { Branch } from '../models/branch.model';

const BRANCH_KEY = 'sm_branch_id';
const ORG_KEY = 'sm_org_id';

@Injectable({ providedIn: 'root' })
export class BranchContextService {
  private readonly api = inject(ApiService);

  private readonly branchesSignal = signal<Branch[]>([]);
  private readonly currentBranchIdSignal = signal<string | null>(localStorage.getItem(BRANCH_KEY));
  private readonly organizationIdSignal = signal<string | null>(localStorage.getItem(ORG_KEY));

  readonly branches = this.branchesSignal.asReadonly();
  readonly currentBranchId = this.currentBranchIdSignal.asReadonly();
  readonly organizationId = this.organizationIdSignal.asReadonly();

  readonly currentBranch = computed(() => {
    const id = this.currentBranchIdSignal();
    return this.branchesSignal().find((b) => b.id === id) ?? null;
  });

  setOrganizationId(orgId: string): void {
    localStorage.setItem(ORG_KEY, orgId);
    this.organizationIdSignal.set(orgId);
    this.loadBranches(orgId);
  }

  loadBranches(orgId: string): void {
    this.api.get<Branch[]>(`/platform/organizations/${orgId}/branches`).subscribe({
      next: (branches) => {
        this.branchesSignal.set(branches);
        const stored = this.currentBranchIdSignal();
        if (!stored && branches.length > 0) {
          this.setBranch(branches[0].id);
        } else if (stored && !branches.some((b) => b.id === stored) && branches.length > 0) {
          this.setBranch(branches[0].id);
        }
      },
    });
  }

  setBranch(branchId: string): void {
    localStorage.setItem(BRANCH_KEY, branchId);
    this.currentBranchIdSignal.set(branchId);
  }

  initialize(): void {
    const orgId = this.organizationIdSignal();
    if (orgId) {
      this.loadBranches(orgId);
    }
  }

  getBranchHeaders(): Record<string, string> {
    const headers: Record<string, string> = {};
    const branchId = this.currentBranchIdSignal();
    const orgId = this.organizationIdSignal();
    if (branchId) headers['X-Branch-Id'] = branchId;
    if (orgId) headers['X-Organization-Id'] = orgId;
    return headers;
  }

  getOrgQueryParams(): Record<string, string> {
    const orgId = this.organizationIdSignal();
    return orgId ? { organizationId: orgId } : {};
  }
}
