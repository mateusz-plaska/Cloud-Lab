import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { ProductService } from '../../core/services/product.service';
import { StockService, StockItem } from '../../core/services/stock.service';
import type { Product } from '../../types';
import { PaginationComponent } from '../../shared/pagination';

const PAGE_SIZE = 10;

interface StockRow {
  productId: string;
  name: string;
  quantity: number;
}

@Component({
  selector: 'app-stocks',
  standalone: true,
  imports: [FormsModule, PaginationComponent],
  templateUrl: './stocks.html',
})
export class Stocks implements OnInit {
  protected readonly auth = inject(AuthService);
  private readonly productService = inject(ProductService);
  private readonly stockService = inject(StockService);

  readonly products = signal<Product[]>([]);
  readonly stocks = signal<StockItem[]>([]);
  readonly dataLoading = signal(true);
  readonly dataError = signal('');

  readonly search = signal('');
  readonly sortField = signal<'name' | 'quantity' | null>('quantity');
  readonly sortDir = signal<'asc' | 'desc'>('desc');

  readonly rows = computed((): StockRow[] => {
    const stockMap = new Map(this.stocks().map((s) => [s.productId, s.quantity]));
    return this.products().map((p) => ({
      productId: p.productId,
      name: p.name,
      quantity: stockMap.get(p.productId) ?? 0,
    }));
  });

  readonly filteredRows = computed((): StockRow[] => {
    const q = this.search().toLowerCase();
    const field = this.sortField();
    const dir = this.sortDir();
    let list = !q ? this.rows() : this.rows().filter(
      (r) => r.name.toLowerCase().includes(q) || r.productId.toLowerCase().includes(q));
    if (field) {
      list = [...list].sort((a, b) => {
        const cmp = field === 'name' ? a.name.localeCompare(b.name) : a.quantity - b.quantity;
        return dir === 'asc' ? cmp : -cmp;
      });
    }
    return list;
  });

  readonly page = signal(1);
  readonly totalPages = computed(() => Math.max(1, Math.ceil(this.filteredRows().length / PAGE_SIZE)));
  readonly pagedRows = computed(() => {
    const p = this.page();
    const list = this.filteredRows();
    const safe = Math.min(p, Math.max(1, Math.ceil(list.length / PAGE_SIZE)));
    return list.slice((safe - 1) * PAGE_SIZE, safe * PAGE_SIZE);
  });

  selectedProductId = '';
  addQty = 1;
  readonly addLoading = signal(false);
  readonly addMsg = signal('');
  readonly addError = signal('');

  newProductName = '';
  readonly createLoading = signal(false);
  readonly createMsg = signal('');
  readonly createError = signal('');

  ngOnInit(): void {
    this.reload();
  }

  onSearch(value: string): void { this.search.set(value); this.page.set(1); }

  sort(field: 'name' | 'quantity'): void {
    if (this.sortField() !== field) {
      this.sortField.set(field);
      this.sortDir.set('asc');
    } else if (this.sortDir() === 'asc') {
      this.sortDir.set('desc');
    } else {
      this.sortField.set(null);
    }
    this.page.set(1);
  }

  sortState(field: 'name' | 'quantity'): 'none' | 'asc' | 'desc' {
    if (this.sortField() !== field) return 'none';
    return this.sortDir();
  }

  private reload(): void {
    this.dataLoading.set(true);
    let productsLoaded = false;
    let stocksLoaded = false;
    const done = () => {
      if (productsLoaded && stocksLoaded) this.dataLoading.set(false);
    };

    this.productService.getProducts().subscribe({
      next: (p) => {
        this.products.set(p);
        productsLoaded = true;
        done();
      },
      error: () => {
        this.dataError.set('Nie udało się pobrać katalogu produktów');
        this.dataLoading.set(false);
      },
    });

    this.stockService.getStocks().subscribe({
      next: (s) => {
        this.stocks.set(s);
        stocksLoaded = true;
        done();
      },
      error: () => {
        this.dataError.set('Nie udało się pobrać stanu magazynu');
        this.dataLoading.set(false);
      },
    });
  }

  clampQty(): void {
    if (!this.addQty || this.addQty < 1) this.addQty = 1;
    else if (this.addQty > 1000) this.addQty = 1000;
  }

  async addStock(): Promise<void> {
    if (!this.selectedProductId || this.addQty < 1 || this.addLoading()) return;
    this.addMsg.set('');
    this.addError.set('');
    this.addLoading.set(true);
    try {
      await new Promise<void>((res, rej) =>
        this.stockService.addStock(this.selectedProductId, this.addQty).subscribe({ next: res, error: rej }),
      );
      const name = this.products().find((p) => p.productId === this.selectedProductId)?.name ?? this.selectedProductId;
      this.addMsg.set(`Dodano ${this.addQty} szt. produktu „${name}"`);
      this.selectedProductId = '';
      this.addQty = 1;
      this.reload();
    } catch {
      this.addError.set('Nie udało się zaktualizować stanu');
    } finally {
      this.addLoading.set(false);
    }
  }

  async createProduct(): Promise<void> {
    if (!this.newProductName.trim() || this.createLoading()) return;
    this.createMsg.set('');
    this.createError.set('');
    this.createLoading.set(true);
    try {
      await new Promise<void>((res, rej) =>
        this.productService.createProduct(this.newProductName.trim()).subscribe({ next: () => res(), error: rej }),
      );
      this.createMsg.set(`Produkt „${this.newProductName.trim()}" dodany do katalogu`);
      this.newProductName = '';
      this.reload();
    } catch {
      this.createError.set('Nie udało się dodać produktu');
    } finally {
      this.createLoading.set(false);
    }
  }
}
