import { Component, DestroyRef, OnInit, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { debounceTime, switchMap } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { SseService } from '../../core/services/sse.service';
import type { DashboardStats, OrderStatus } from '../../types';

const STATUS_COLORS: Record<string, string> = {
  PLANNED: '#f59e0b',
  IN_PROGRESS: '#3b82f6',
  PACKED: '#f97316',
  READY: '#8b5cf6',
  COMPLETED: '#22c55e',
  FAILED: '#ef4444',
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [BaseChartDirective],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly sseService = inject(SseService);
  private readonly destroyRef = inject(DestroyRef);

  readonly stats = signal<DashboardStats | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly chartData = signal<ChartData<'bar'>>({ labels: [], datasets: [] });

  readonly chartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
    datasets: { bar: { maxBarThickness: 64 } },
  };

  constructor() {
    effect(() => {
      const s = this.stats();
      if (!s) return;
      const labels = Object.keys(s.byStatus);
      this.chartData.set({
        labels,
        datasets: [
          {
            label: 'Zamówienia',
            data: Object.values(s.byStatus),
            backgroundColor: labels.map((l) => STATUS_COLORS[l] ?? '#94a3b8'),
            borderRadius: 4,
          },
        ],
      });
    });
  }

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe({
      next: (stats) => { this.stats.set(stats); this.loading.set(false); },
      error: () => { this.error.set('Nie udało się pobrać statystyk'); this.loading.set(false); },
    });

    this.sseService
      .watchDashboard()
      .pipe(debounceTime(500), switchMap(() => this.dashboardService.getStats()), takeUntilDestroyed(this.destroyRef))
      .subscribe({ next: (stats) => this.stats.set(stats) });
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status] ?? '#94a3b8';
  }

  statusEntries(): [string, number][] {
    const s = this.stats();
    return s ? Object.entries(s.byStatus) : [];
  }
}