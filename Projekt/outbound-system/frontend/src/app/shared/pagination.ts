import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: true,
  templateUrl: './pagination.html',
})
export class PaginationComponent {
  @Input() page = 1;
  @Input() totalPages = 1;
  @Output() pageChange = new EventEmitter<number>();

  pageList(): (number | null)[] {
    const t = this.totalPages;
    const c = this.page;
    if (t <= 7) return Array.from({ length: t }, (_, i) => i + 1);
    const r: (number | null)[] = [1];
    if (c > 3) r.push(null);
    for (let i = Math.max(2, c - 1); i <= Math.min(t - 1, c + 1); i++) r.push(i);
    if (c < t - 2) r.push(null);
    r.push(t);
    return r;
  }
}
