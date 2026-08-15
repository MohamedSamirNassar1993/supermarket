import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { BreakpointObserver } from '@angular/cdk/layout';
import {
  AuthService,
  BranchContextService,
  TranslationService,
  TranslatePipe,
  HasPermissionDirective,
  MATERIAL_IMPORTS,
  Lang,
} from '@supermarket/shared-ui';

interface NavItem {
  labelKey: string;
  icon: string;
  route: string;
  permissions?: string[];
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    TranslatePipe,
    HasPermissionDirective,
    ...MATERIAL_IMPORTS,
  ],
  templateUrl: './main-layout.component.html',
  styleUrl: './main-layout.component.scss',
})
export class MainLayoutComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly branchContext = inject(BranchContextService);
  private readonly i18n = inject(TranslationService);
  private readonly breakpointObserver = inject(BreakpointObserver);

  readonly user = this.auth.user;
  readonly branches = this.branchContext.branches;
  readonly currentBranchId = this.branchContext.currentBranchId;
  readonly sidenavOpened = signal(true);
  readonly isHandset = signal(false);

  readonly navItems: NavItem[] = [
    { labelKey: 'nav.dashboard', icon: 'dashboard', route: '/dashboard' },
    { labelKey: 'nav.products', icon: 'inventory_2', route: '/catalog' },
    { labelKey: 'nav.inventory', icon: 'shelves', route: '/inventory' },
    { labelKey: 'nav.purchases', icon: 'shopping_cart', route: '/procurement' },
    { labelKey: 'nav.sales', icon: 'receipt_long', route: '/sales' },
    { labelKey: 'nav.reports', icon: 'assessment', route: '/analytics' },
    { labelKey: 'nav.warehouse', icon: 'warehouse', route: '/operations' },
  ];

  ngOnInit(): void {
    this.breakpointObserver.observe('(max-width: 768px)').subscribe((result) => {
      this.isHandset.set(result.matches);
      if (result.matches) this.sidenavOpened.set(false);
    });
    this.branchContext.initialize();
  }

  onBranchChange(branchId: string): void {
    this.branchContext.setBranch(branchId);
  }

  switchLanguage(lang: Lang): void {
    this.i18n.switchLanguage(lang);
  }

  logout(): void {
    this.auth.logout();
  }

  toggleSidenav(): void {
    this.sidenavOpened.update((v) => !v);
  }
}
