import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { StockService } from '../../core/services/stock.service';

@Component({
  selector: 'app-stocks',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './stocks.html',
})
export class Stocks {
  private readonly stockService = inject(StockService);

  productId = '';
  quantity = 1;
  readonly loading = signal(false);
  readonly successMsg = signal('');
  readonly errorMsg = signal('');

  async submit(): Promise<void> {
    if (!this.productId.trim() || this.quantity < 1 || this.loading()) return;
    this.successMsg.set('');
    this.errorMsg.set('');
    this.loading.set(true);
    try {
      await new Promise<void>((resolve, reject) => {
        this.stockService.addStock(this.productId.trim(), this.quantity).subscribe({
          next: () => resolve(),
          error: reject,
        });
      });
      this.successMsg.set(`Dodano ${this.quantity} szt. produktu ${this.productId}`);
      this.productId = '';
      this.quantity = 1;
    } catch {
      this.errorMsg.set('Nie udało się zaktualizować stanu magazynowego');
    } finally {
      this.loading.set(false);
    }
  }
}