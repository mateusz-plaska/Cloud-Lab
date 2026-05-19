import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions } from 'chart.js';
import { DashboardService } from '../../core/services/dashboard.service';
import type { DashboardStats, OrderStatus } from '../../types';

const STATUS_COLORS: Record<string, string> = {
  PENDING: '#f59e0b',
  RESERVED: '#3b82f6',
  PICKED: '#8b5cf6',
  PACKED: '#f97316',
  SHIPPED: '#22c55e',
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

  readonly stats = signal<DashboardStats | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly chartData = signal<ChartData<'bar'>>({ labels: [], datasets: [] });

  readonly chartOptions: ChartOptions<'bar'> = {
    responsive: true,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
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
      next: (stats) => {
        this.stats.set(stats);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Nie udało się pobrać statystyk');
        this.loading.set(false);
      },
    });
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status] ?? '#94a3b8';
  }

  statusEntries(): [string, number][] {
    const s = this.stats();
    return s ? Object.entries(s.byStatus) : [];
  }
}