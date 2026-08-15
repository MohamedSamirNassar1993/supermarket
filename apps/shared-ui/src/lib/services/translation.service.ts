import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export type Lang = 'en' | 'ar';

@Injectable({ providedIn: 'root' })
export class TranslationService {
  private readonly http = inject(HttpClient);
  private readonly translations = signal<Record<string, string>>({});
  private readonly currentLang = signal<Lang>(
    (localStorage.getItem('sm_lang') as Lang) || 'en'
  );

  readonly lang = this.currentLang.asReadonly();
  readonly isRtl = signal(this.currentLang() === 'ar');

  constructor() {
    this.loadLanguage(this.currentLang());
  }

  loadLanguage(lang: Lang): void {
    this.http.get<Record<string, unknown>>(`/assets/i18n/${lang}.json`).subscribe({
      next: (data) => {
        this.translations.set(this.flatten(data));
        this.currentLang.set(lang);
        this.isRtl.set(lang === 'ar');
        localStorage.setItem('sm_lang', lang);
        document.documentElement.lang = lang;
        document.documentElement.dir = lang === 'ar' ? 'rtl' : 'ltr';
      },
    });
  }

  translate(key: string, params?: Record<string, string>): string {
    let text = this.translations()[key] ?? key;
    if (params) {
      Object.entries(params).forEach(([k, v]) => {
        text = text.replace(`{{${k}}}`, v);
      });
    }
    return text;
  }

  t(key: string, params?: Record<string, string>): string {
    return this.translate(key, params);
  }

  switchLanguage(lang: Lang): void {
    if (lang !== this.currentLang()) {
      this.loadLanguage(lang);
    }
  }

  private flatten(obj: Record<string, unknown>, prefix = ''): Record<string, string> {
    const result: Record<string, string> = {};
    for (const [key, value] of Object.entries(obj)) {
      const fullKey = prefix ? `${prefix}.${key}` : key;
      if (value && typeof value === 'object' && !Array.isArray(value)) {
        Object.assign(result, this.flatten(value as Record<string, unknown>, fullKey));
      } else {
        result[fullKey] = String(value);
      }
    }
    return result;
  }
}
