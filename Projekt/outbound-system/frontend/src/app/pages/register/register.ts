import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly username = signal('');
  readonly email = signal('');
  readonly password = signal('');
  readonly loading = signal(false);
  readonly error = signal('');

  async submit(): Promise<void> {
    if (this.loading()) return;
    this.error.set('');
    this.loading.set(true);
    try {
      await this.auth.register({ username: this.username(), email: this.email(), password: this.password() });
      await this.router.navigate(['/dashboard']);
    } catch (err: unknown) {
      if (err instanceof HttpErrorResponse) {
        if (err.status === 0 || err.status >= 500) {
          this.error.set('Serwer nie odpowiada, spróbuj ponownie za chwilę');
        } else if (err.status === 409) {
          const serverMsg = (err.error as { message?: string } | null)?.message;
          this.error.set(serverMsg ?? 'Użytkownik o takiej nazwie lub adresie e-mail już istnieje');
        } else {
          this.error.set('Rejestracja nie powiodła się');
        }
      } else {
        this.error.set('Rejestracja nie powiodła się');
      }
    } finally {
      this.loading.set(false);
    }
  }
}
