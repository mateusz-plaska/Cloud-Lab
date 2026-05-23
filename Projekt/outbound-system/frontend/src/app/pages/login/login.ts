import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly username = signal('');
  readonly password = signal('');
  readonly loading = signal(false);
  readonly error = signal('');

  async submit(): Promise<void> {
    if (this.loading()) return;
    this.error.set('');
    this.loading.set(true);
    try {
      await this.auth.login({ username: this.username(), password: this.password() });
      await this.router.navigate(['/dashboard']);
    } catch (err: unknown) {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 0 || err.status >= 500) {
          this.error.set('Serwer nie odpowiada, spróbuj ponownie za chwilę');
        } else if (err.status === 401 || err.status === 403) {
          this.error.set('Nieprawidłowa nazwa użytkownika lub hasło');
        } else {
          this.error.set('Logowanie nie powiodło się, spróbuj ponownie');
        }
      } else {
        this.error.set('Logowanie nie powiodło się, spróbuj ponownie');
      }
    } finally {
      this.loading.set(false);
    }
  }
}
