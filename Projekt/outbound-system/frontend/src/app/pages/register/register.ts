import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
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

  username = '';
  email = '';
  password = '';
  readonly loading = signal(false);
  readonly error = signal('');

  async submit(): Promise<void> {
    if (this.loading()) return;
    this.error.set('');
    this.loading.set(true);
    try {
      await this.auth.register({ username: this.username, email: this.email, password: this.password });
      this.router.navigate(['/dashboard']);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : '';
      this.error.set(msg.includes('already') ? msg : 'Rejestracja nie powiodła się');
    } finally {
      this.loading.set(false);
    }
  }
}