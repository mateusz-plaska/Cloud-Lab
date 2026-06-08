import { Injectable, inject } from '@angular/core';
import { ConfigService } from './config.service';

const VERIFIER_KEY = 'sso_code_verifier';
const STATE_KEY = 'sso_state';

@Injectable({ providedIn: 'root' })
export class SsoService {
  private readonly config = inject(ConfigService);

  get enabled(): boolean {
    return this.config.sso.enabled;
  }

  async startLogin(): Promise<void> {
    const sso = this.config.sso;
    if (!sso.enabled) return;

    const verifier = this.randomString(64);
    const state = this.randomString(32);
    const challenge = await this.sha256Base64Url(verifier);

    sessionStorage.setItem(VERIFIER_KEY, verifier);
    sessionStorage.setItem(STATE_KEY, state);

    const params = new URLSearchParams({
      response_type: 'code',
      client_id: sso.clientId,
      redirect_uri: sso.redirectUri,
      scope: sso.scopes,
      state,
      code_challenge: challenge,
      code_challenge_method: 'S256',
      prompt: 'select_account',
    });

    window.location.href = `${sso.authorizationUri}?${params.toString()}`;
  }

  consumeVerifier(returnedState: string | null): string | null {
    const expectedState = sessionStorage.getItem(STATE_KEY);
    const verifier = sessionStorage.getItem(VERIFIER_KEY);
    sessionStorage.removeItem(STATE_KEY);
    sessionStorage.removeItem(VERIFIER_KEY);
    if (!verifier || !expectedState || returnedState !== expectedState) return null;
    return verifier;
  }

  private randomString(bytes: number): string {
    const buffer = new Uint8Array(bytes);
    crypto.getRandomValues(buffer);
    return this.base64Url(buffer);
  }

  private async sha256Base64Url(value: string): Promise<string> {
    const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(value));
    return this.base64Url(new Uint8Array(digest));
  }

  private base64Url(buffer: Uint8Array): string {
    let binary = '';
    buffer.forEach((b) => (binary += String.fromCharCode(b)));
    return btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  }
}
