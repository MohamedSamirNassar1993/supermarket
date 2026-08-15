import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { BranchContextService } from '../services/branch-context.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const branchContext = inject(BranchContextService);

  const token = auth.getAccessToken();
  const branchHeaders = branchContext.getBranchHeaders();

  let headers = req.headers;
  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }
  Object.entries(branchHeaders).forEach(([key, value]) => {
    headers = headers.set(key, value);
  });

  const authReq = req.clone({ headers });

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.includes('/auth/login')) {
        return auth.refresh().pipe(
          switchMap(() => {
            const newToken = auth.getAccessToken();
            let retryHeaders = req.headers.set('Authorization', `Bearer ${newToken}`);
            Object.entries(branchHeaders).forEach(([key, value]) => {
              retryHeaders = retryHeaders.set(key, value);
            });
            return next(req.clone({ headers: retryHeaders }));
          }),
          catchError((refreshErr) => {
            auth.logout();
            return throwError(() => refreshErr);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
