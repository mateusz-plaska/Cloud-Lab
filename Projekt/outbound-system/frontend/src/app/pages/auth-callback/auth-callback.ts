import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { SsoService } from '../../core/services/sso.service';

@Component({
  selector: 'app-auth-callback',
  standalone: true,
  templateUrl: './auth-callback.html',
})
export class AuthCallback implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  private readonly sso = inject(SsoService);

  readonly error = signal('');

  async ngOnInit(): Promise<void> {
    const params = this.route.snapshot.queryParamMap;
    const code = params.get('code');
    const state = params.get('state');

    if (params.get('error') || !code) {
      this.fail('Logowanie przez SSO zostało przerwane');
      return;
    }

    const verifier = this.sso.consumeVerifier(state);
    if (!verifier) {
      this.fail('Nieprawidłowa sesja logowania, spróbuj ponownie');
      return;
    }

    try {
      await this.auth.exchangeSso(code, verifier);
      await this.router.navigate(['/dashboard']);
    } catch {
      this.fail('Logowanie przez SSO nie powiodło się');
    }
  }

  private fail(message: string): void {
    this.error.set(message);
    setTimeout(() => void this.router.navigate(['/login']), 2500);
  }
}
