import { mkdirSync, writeFileSync, existsSync } from 'fs';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';

const scriptDir = dirname(fileURLToPath(import.meta.url));
const appRoot = join(scriptDir, '..', 'src', 'app');
const assetsRoot = join(scriptDir, '..', 'src', 'assets');

function ensureDir(p) {
  if (!existsSync(p)) mkdirSync(p, { recursive: true });
}

function write(relPath, content, root = appRoot) {
  const full = join(root, relPath);
  ensureDir(dirname(full));
  writeFileSync(full, content, 'utf8');
  return full;
}

const listComponent = (name, title, apiPath, orgParam = true) => {
  const org = orgParam ? `    const orgId = this.branchContext.organizationId();\n    if (!orgId) return;\n` : '';
  const apiExpr = orgParam
    ? `\`${apiPath}?organizationId=\${orgId}\``
    : `'${apiPath}'`;
  return `import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { ApiService } from '../../core/services/api.service';
import { BranchContextService } from '../../core/services/branch-context.service';
import { TranslatePipe } from '../../core/pipes/translate.pipe';

@Component({
  selector: 'app-${name}-list',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatProgressSpinnerModule, MatCardModule, TranslatePipe],
  template: \`
    <mat-card>
      <mat-card-header>
        <mat-card-title>${title}</mat-card-title>
        <button mat-icon-button (click)="load()"><mat-icon>refresh</mat-icon></button>
      </mat-card-header>
      <mat-card-content>
        @if (loading()) { <mat-spinner diameter="40"></mat-spinner> }
        @else if (error()) { <p class="error">{{ error() }}</p> }
        @else {
          <table mat-table [dataSource]="items()" class="full-width">
            <ng-container matColumnDef="id">
              <th mat-header-cell *matHeaderCellDef>ID</th>
              <td mat-cell *matCellDef="let row">{{ row.id | slice:0:8 }}</td>
            </ng-container>
            <ng-container matColumnDef="name">
              <th mat-header-cell *matHeaderCellDef>{{ 'common.name' | translate }}</th>
              <td mat-cell *matCellDef="let row">{{ row.name || row.code || row.email || row.invoiceNumber || row.batchNumber || '-' }}</td>
            </ng-container>
            <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
            <tr mat-row *matRowDef="let row; columns: displayedColumns"></tr>
          </table>
          @if (items().length === 0) { <p>{{ 'common.noData' | translate }}</p> }
        }
      </mat-card-content>
    </mat-card>
  \`,
  styles: [\`.full-width { width: 100%; } .error { color: #c62828; }\`]
})
export class ${name.charAt(0).toUpperCase() + name.slice(1)}ListComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly branchContext = inject(BranchContextService);
  readonly items = signal<Record<string, unknown>[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  displayedColumns = ['id', 'name'];

  ngOnInit(): void { this.load(); }

  load(): void {
${org}    this.loading.set(true);
    this.error.set(null);
    this.api.get<Record<string, unknown>[] | { content: Record<string, unknown>[] }>(${apiExpr}).subscribe({
      next: (data) => {
        const list = Array.isArray(data) ? data : (data?.content ?? []);
        this.items.set(list);
        this.loading.set(false);
      },
      error: (err) => { this.error.set(err.message || 'Failed to load'); this.loading.set(false); }
    });
  }
}
`;
};

const features = [
  ['users', 'Users', '/users', false],
  ['products', 'Products', '/products', true],
  ['warehouse', 'Warehouses', '/warehouses', true],
  ['inventory', 'Stock Batches', '/stock-batches', true],
  ['suppliers', 'Suppliers', '/suppliers', true],
  ['purchases', 'Purchase Orders', '/purchases/orders', true],
  ['customers', 'Customers', '/customers', true],
  ['sales', 'Sales Invoices', '/sales/invoices', true],
  ['expenses', 'Expenses', '/expenses', true],
  ['hr', 'Employees', '/hr/employees', true],
  ['reports', 'Reports', '/reports/types', false],
  ['notifications', 'Notifications', '/notifications', false],
];

for (const [name, title, path, org] of features) {
  write(`features/${name}/${name}-list.component.ts`, listComponent(name, title, path, org));
}

write('features/auth/login.component.ts', `import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth.service';
import { TranslatePipe } from '../../core/pipes/translate.pipe';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule, MatFormFieldModule, MatInputModule, MatButtonModule, MatProgressSpinnerModule, TranslatePipe],
  template: \`
    <div class="login-container">
      <mat-card>
        <mat-card-header><mat-card-title>{{ 'auth.login' | translate }}</mat-card-title></mat-card-header>
        <mat-card-content>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>{{ 'auth.email' | translate }}</mat-label>
              <input matInput formControlName="email" type="email" />
            </mat-form-field>
            <mat-form-field appearance="outline" class="full-width">
              <mat-label>{{ 'auth.password' | translate }}</mat-label>
              <input matInput formControlName="password" type="password" />
            </mat-form-field>
            @if (error()) { <p class="error">{{ error() }}</p> }
            <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || loading()">
              @if (loading()) { <mat-spinner diameter="20"></mat-spinner> } @else { {{ 'auth.signIn' | translate }} }
            </button>
          </form>
        </mat-card-content>
      </mat-card>
    </div>
  \`,
  styles: [\`.login-container { display:flex; justify-content:center; align-items:center; min-height:100vh; padding:1rem; }
    mat-card { width:100%; max-width:400px; }
    .full-width { width:100%; display:block; margin-bottom:1rem; }
    .error { color:#c62828; }\`]
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  form = this.fb.group({ email: ['admin@supermarket.local', Validators.required], password: ['Admin123!', Validators.required] });

  submit(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.error.set(null);
    this.auth.login(this.form.getRawValue() as { email: string; password: string }).subscribe({
      next: () => { this.loading.set(false); this.router.navigate(['/dashboard']); },
      error: () => { this.error.set('Invalid credentials'); this.loading.set(false); }
    });
  }
}
`);

write('features/dashboard/dashboard.component.ts', `import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApiService } from '../../core/services/api.service';
import { BranchContextService } from '../../core/services/branch-context.service';
import { TranslatePipe } from '../../core/pipes/translate.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatGridListModule, MatProgressSpinnerModule, TranslatePipe],
  template: \`
    <h1>{{ 'nav.dashboard' | translate }}</h1>
    @if (loading()) { <mat-spinner></mat-spinner> }
    @else {
      <div class="metrics">
        @for (m of metrics(); track m.label) {
          <mat-card><mat-card-title>{{ m.label }}</mat-card-title><mat-card-content><h2>{{ m.value }}</h2></mat-card-content></mat-card>
        }
      </div>
    }
  \`,
  styles: [\`.metrics { display:grid; grid-template-columns:repeat(auto-fill,minmax(200px,1fr)); gap:1rem; }\`]
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly branchContext = inject(BranchContextService);
  readonly metrics = signal<{ label: string; value: string }[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    const orgId = this.branchContext.organizationId();
    const branchId = this.branchContext.currentBranchId();
    if (!orgId) { this.loading.set(false); return; }
    this.api.get<Record<string, unknown>>(\`/dashboard?organizationId=\${orgId}&branchId=\${branchId || ''}\`).subscribe({
      next: (data) => {
        this.metrics.set([
          { label: 'Today Sales', value: String(data['todaySales'] ?? '0') },
          { label: 'Today Purchases', value: String(data['todayPurchases'] ?? '0') },
          { label: 'Today Profit', value: String(data['todayProfit'] ?? '0') },
          { label: 'Cash Balance', value: String(data['cashBalance'] ?? '0') },
          { label: 'Inventory Value', value: String(data['inventoryValue'] ?? '0') },
          { label: 'Low Stock Alerts', value: String(data['lowStockCount'] ?? '0') },
        ]);
        this.loading.set(false);
      },
      error: () => { this.loading.set(false); this.metrics.set([]); }
    });
  }
}
`);

write('features/pos/offline-sync.service.ts', `import { Injectable } from '@angular/core';

const DB_NAME = 'supermarket-pos';
const STORE = 'pending-sales';

@Injectable({ providedIn: 'root' })
export class OfflineSyncService {
  private db: IDBDatabase | null = null;

  async init(): Promise<void> {
    if (this.db) return;
    this.db = await new Promise((resolve, reject) => {
      const req = indexedDB.open(DB_NAME, 1);
      req.onupgradeneeded = () => req.result.createObjectStore(STORE, { keyPath: 'id', autoIncrement: true });
      req.onsuccess = () => resolve(req.result);
      req.onerror = () => reject(req.error);
    });
  }

  async enqueueSale(payload: unknown): Promise<void> {
    await this.init();
    await new Promise<void>((resolve, reject) => {
      const tx = this.db!.transaction(STORE, 'readwrite');
      tx.objectStore(STORE).add({ payload, createdAt: new Date().toISOString() });
      tx.oncomplete = () => resolve();
      tx.onerror = () => reject(tx.error);
    });
  }

  async drain(): Promise<unknown[]> {
    await this.init();
    const items: unknown[] = await new Promise((resolve, reject) => {
      const tx = this.db!.transaction(STORE, 'readonly');
      const req = tx.objectStore(STORE).getAll();
      req.onsuccess = () => resolve(req.result);
      req.onerror = () => reject(req.error);
    });
    return items;
  }

  async clear(): Promise<void> {
    await this.init();
    await new Promise<void>((resolve, reject) => {
      const tx = this.db!.transaction(STORE, 'readwrite');
      tx.objectStore(STORE).clear();
      tx.oncomplete = () => resolve();
      tx.onerror = () => reject(tx.error);
    });
  }
}
`);

write('features/pos/pos.component.ts', `import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../core/services/api.service';
import { BranchContextService } from '../../core/services/branch-context.service';
import { OfflineSyncService } from './offline-sync.service';
import { TranslatePipe } from '../../core/pipes/translate.pipe';

interface CartLine { productId: string; name: string; quantity: number; unitPrice: number; }

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [CommonModule, FormsModule, MatCardModule, MatButtonModule, MatInputModule, MatFormFieldModule, MatTableModule, MatIconModule, TranslatePipe],
  template: \`
    <div class="pos-grid">
      <mat-card>
        <mat-form-field class="full-width"><mat-label>{{ 'pos.barcode' | translate }}</mat-label>
          <input matInput [(ngModel)]="barcode" (keyup.enter)="scan()" autofocus /></mat-form-field>
        <button mat-flat-button color="primary" (click)="scan()">{{ 'pos.addItem' | translate }}</button>
      </mat-card>
      <mat-card>
        <table mat-table [dataSource]="cart()" class="full-width">
          <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Item</th><td mat-cell *matCellDef="let l">{{ l.name }}</td></ng-container>
          <ng-container matColumnDef="qty"><th mat-header-cell *matHeaderCellDef>Qty</th><td mat-cell *matCellDef="let l">{{ l.quantity }}</td></ng-container>
          <ng-container matColumnDef="price"><th mat-header-cell *matHeaderCellDef>Price</th><td mat-cell *matCellDef="let l">{{ l.unitPrice | number:'1.2-2' }}</td></ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr><tr mat-row *matRowDef="let row; columns: cols"></tr>
        </table>
        <p><strong>Total: {{ total() | number:'1.2-2' }}</strong></p>
        <button mat-flat-button color="accent" (click)="checkout()" [disabled]="cart().length===0">{{ 'pos.checkout' | translate }}</button>
        <button mat-stroked-button (click)="syncOffline()">{{ 'pos.sync' | translate }}</button>
      </mat-card>
    </div>
  \`,
  styles: [\`.pos-grid { display:grid; grid-template-columns:1fr 2fr; gap:1rem; } .full-width { width:100%; }\`]
})
export class PosComponent {
  private readonly api = inject(ApiService);
  private readonly branchContext = inject(BranchContextService);
  private readonly offline = inject(OfflineSyncService);
  barcode = '';
  readonly cart = signal<CartLine[]>([]);
  cols = ['name', 'qty', 'price'];

  total = () => this.cart().reduce((s, l) => s + l.quantity * l.unitPrice, 0);

  scan(): void {
    const orgId = this.branchContext.organizationId();
    if (!orgId || !this.barcode.trim()) return;
    this.api.get<{ id: string; name: string; retailPrice: number }>(\`/products/by-barcode/\${encodeURIComponent(this.barcode)}?organizationId=\${orgId}\`).subscribe({
      next: (p) => {
        this.cart.update(c => [...c, { productId: p.id, name: p.name, quantity: 1, unitPrice: Number(p.retailPrice) || 0 }]);
        this.barcode = '';
      },
      error: () => this.cart.update(c => [...c, { productId: this.barcode, name: 'Unknown ' + this.barcode, quantity: 1, unitPrice: 0 }])
    });
  }

  checkout(): void {
    const orgId = this.branchContext.organizationId();
    const branchId = this.branchContext.currentBranchId();
    const payload = { organizationId: orgId, branchId, lines: this.cart(), payments: [{ method: 'CASH', amount: this.total() }] };
    if (!navigator.onLine) {
      this.offline.enqueueSale(payload).then(() => { this.cart.set([]); });
      return;
    }
    this.api.post('/sales/invoices', payload).subscribe({ next: () => this.cart.set([]) });
  }

  syncOffline(): void {
    this.offline.drain().then(items => {
      items.forEach((item: { payload?: unknown }) => {
        if (item.payload) this.api.post('/pos/sync', item.payload).subscribe();
      });
      this.offline.clear();
    });
  }
}
`);

write('layout/main-layout/main-layout.component.ts', `import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatMenuModule } from '@angular/material/menu';
import { MatBadgeModule } from '@angular/material/badge';
import { AuthService } from '../../core/services/auth.service';
import { BranchContextService } from '../../core/services/branch-context.service';
import { TranslationService } from '../../core/services/translation.service';
import { ApiService } from '../../core/services/api.service';
import { Branch } from '../../core/models/branch.model';
import { TranslatePipe } from '../../core/pipes/translate.pipe';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterOutlet, MatSidenavModule, MatToolbarModule, MatListModule, MatIconModule, MatButtonModule, MatSelectModule, MatFormFieldModule, MatMenuModule, MatBadgeModule, TranslatePipe],
  template: \`
    <mat-sidenav-container class="shell">
      <mat-sidenav mode="side" opened class="sidenav">
        <div class="brand">Supermarket ERP</div>
        <mat-nav-list>
          @for (item of navItems; track item.path) {
            <a mat-list-item [routerLink]="item.path" routerLinkActive="active"><mat-icon matListItemIcon>{{ item.icon }}</mat-icon><span matListItemTitle>{{ item.labelKey | translate }}</span></a>
          }
        </mat-nav-list>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          <span class="spacer"></span>
          <mat-form-field appearance="outline" class="branch-select" subscriptSizing="dynamic">
            <mat-select [value]="branchContext.currentBranchId()" (selectionChange)="branchContext.setBranch($event.value)">
              @for (b of branches(); track b.id) { <mat-option [value]="b.id">{{ b.name }}</mat-option> }
            </mat-select>
          </mat-form-field>
          <button mat-icon-button [matMenuTriggerFor]="langMenu"><mat-icon>language</mat-icon></button>
          <mat-menu #langMenu="matMenu">
            <button mat-menu-item (click)="translation.switchLanguage('en')">English</button>
            <button mat-menu-item (click)="translation.switchLanguage('ar')">العربية</button>
          </mat-menu>
          <button mat-icon-button routerLink="/notifications"><mat-icon matBadge="0" matBadgeSize="small">notifications</mat-icon></button>
          <button mat-icon-button [matMenuTriggerFor]="userMenu"><mat-icon>account_circle</mat-icon></button>
          <mat-menu #userMenu="matMenu">
            <button mat-menu-item disabled>{{ auth.user()?.email }}</button>
            <button mat-menu-item (click)="auth.logout()">{{ 'auth.logout' | translate }}</button>
          </mat-menu>
        </mat-toolbar>
        <main class="content"><router-outlet /></main>
      </mat-sidenav-content>
    </mat-sidenav-container>
  \`,
  styles: [\`.shell { height:100vh; } .sidenav { width:260px; } .brand { padding:1rem; font-weight:600; font-size:1.1rem; }
    .content { padding:1.5rem; } .spacer { flex:1; } .branch-select { width:180px; margin-right:1rem; font-size:14px; }
    a.active { background:rgba(0,0,0,0.08); }\`]
})
export class MainLayoutComponent implements OnInit {
  readonly auth = inject(AuthService);
  readonly branchContext = inject(BranchContextService);
  readonly translation = inject(TranslationService);
  private readonly api = inject(ApiService);
  readonly branches = signal<Branch[]>([]);

  navItems = [
    { path: '/dashboard', icon: 'dashboard', labelKey: 'nav.dashboard' },
    { path: '/users', icon: 'people', labelKey: 'nav.users' },
    { path: '/products', icon: 'inventory_2', labelKey: 'nav.products' },
    { path: '/warehouse', icon: 'warehouse', labelKey: 'nav.warehouse' },
    { path: '/inventory', icon: 'layers', labelKey: 'nav.inventory' },
    { path: '/suppliers', icon: 'local_shipping', labelKey: 'nav.suppliers' },
    { path: '/purchases', icon: 'shopping_cart', labelKey: 'nav.purchases' },
    { path: '/customers', icon: 'groups', labelKey: 'nav.customers' },
    { path: '/sales', icon: 'receipt_long', labelKey: 'nav.sales' },
    { path: '/pos', icon: 'point_of_sale', labelKey: 'nav.pos' },
    { path: '/expenses', icon: 'payments', labelKey: 'nav.expenses' },
    { path: '/hr', icon: 'badge', labelKey: 'nav.hr' },
    { path: '/reports', icon: 'assessment', labelKey: 'nav.reports' },
  ];

  ngOnInit(): void {
    const orgId = this.branchContext.organizationId();
    if (orgId) {
      this.api.get<Branch[]>(\`/platform/organizations/\${orgId}/branches\`).subscribe({
        next: (b) => { this.branches.set(b); if (b.length && !this.branchContext.currentBranchId()) this.branchContext.setBranch(b[0].id); }
      });
    }
  }
}
`);

write('app.routes.ts', `import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'users', loadComponent: () => import('./features/users/users-list.component').then(m => m.UsersListComponent) },
      { path: 'products', loadComponent: () => import('./features/products/products-list.component').then(m => m.ProductsListComponent) },
      { path: 'warehouse', loadComponent: () => import('./features/warehouse/warehouse-list.component').then(m => m.WarehouseListComponent) },
      { path: 'inventory', loadComponent: () => import('./features/inventory/inventory-list.component').then(m => m.InventoryListComponent) },
      { path: 'suppliers', loadComponent: () => import('./features/suppliers/suppliers-list.component').then(m => m.SuppliersListComponent) },
      { path: 'purchases', loadComponent: () => import('./features/purchases/purchases-list.component').then(m => m.PurchasesListComponent) },
      { path: 'customers', loadComponent: () => import('./features/customers/customers-list.component').then(m => m.CustomersListComponent) },
      { path: 'sales', loadComponent: () => import('./features/sales/sales-list.component').then(m => m.SalesListComponent) },
      { path: 'pos', loadComponent: () => import('./features/pos/pos.component').then(m => m.PosComponent) },
      { path: 'expenses', loadComponent: () => import('./features/expenses/expenses-list.component').then(m => m.ExpensesListComponent) },
      { path: 'hr', loadComponent: () => import('./features/hr/hr-list.component').then(m => m.HrListComponent) },
      { path: 'reports', loadComponent: () => import('./features/reports/reports-list.component').then(m => m.ReportsListComponent) },
      { path: 'notifications', loadComponent: () => import('./features/notifications/notifications-list.component').then(m => m.NotificationsListComponent) },
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
`);

write('i18n/en.json', JSON.stringify({
  nav: { dashboard: 'Dashboard', users: 'Users', products: 'Products', warehouse: 'Warehouse', inventory: 'Inventory', suppliers: 'Suppliers', purchases: 'Purchases', customers: 'Customers', sales: 'Sales', pos: 'POS', expenses: 'Expenses', hr: 'HR', reports: 'Reports' },
  auth: { login: 'Login', email: 'Email', password: 'Password', signIn: 'Sign In', logout: 'Logout' },
  common: { name: 'Name', noData: 'No records found', save: 'Save', cancel: 'Cancel', refresh: 'Refresh' },
  pos: { barcode: 'Barcode', addItem: 'Add Item', checkout: 'Checkout', sync: 'Sync Offline Sales' }
}, null, 2), assetsRoot);

write('i18n/ar.json', JSON.stringify({
  nav: { dashboard: 'لوحة التحكم', users: 'المستخدمون', products: 'المنتجات', warehouse: 'المستودعات', inventory: 'المخزون', suppliers: 'الموردون', purchases: 'المشتريات', customers: 'العملاء', sales: 'المبيعات', pos: 'نقطة البيع', expenses: 'المصروفات', hr: 'الموارد البشرية', reports: 'التقارير' },
  auth: { login: 'تسجيل الدخول', email: 'البريد الإلكتروني', password: 'كلمة المرور', signIn: 'دخول', logout: 'خروج' },
  common: { name: 'الاسم', noData: 'لا توجد سجلات', save: 'حفظ', cancel: 'إلغاء', refresh: 'تحديث' },
  pos: { barcode: 'الباركود', addItem: 'إضافة', checkout: 'الدفع', sync: 'مزامنة المبيعات' }
}, null, 2), assetsRoot);

console.log('Frontend generation complete');
