import { Component, DestroyRef, OnInit, computed, effect, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BaseChartDirective } from 'ng2-charts';
import type { ChartData, ChartOptions, ChartType } from 'chart.js';
import { debounceTime, forkJoin, switchMap } from 'rxjs';
import { DashboardService } from '../../core/services/dashboard.service';
import { SseService } from '../../core/services/sse.service';
import { AuthService } from '../../core/services/auth.service';
import type { DashboardStats, LeadTimePoint, SseEventType, ThroughputResponse } from '../../types';

const STATUS_COLORS: Record<string, string> = {
  PLANNED: '#3b82f6',
  IN_PROGRESS: '#06b6d4',
  PACKED: '#f97316',
  READY: '#22c55e',
  COMPLETED: '#8b5cf6',
  FAILED: '#ef4444',
};

const EVENT_SERIES: { type: SseEventType; label: string; color: string }[] = [
  { type: 'ORDER_CREATED', label: 'Utworzone', color: '#3b82f6' },
  { type: 'STOCK_RESERVED', label: 'Zarezerwowane', color: '#06b6d4' },
  { type: 'ORDER_PICKED', label: 'Spickowane', color: '#8b5cf6' },
  { type: 'PACKING_FINISHED', label: 'Spakowane', color: '#f97316' },
  { type: 'SHIPMENT_CREATED', label: 'Wysłane', color: '#22c55e' },
  { type: 'ALLOCATION_FAILED', label: 'Błąd rezerwacji', color: '#ef4444' },
  { type: 'PICK_FAILED', label: 'Błąd pickingu', color: '#ec4899' },
];

const STAGE_SERIES: { key: string; label: string; color: string }[] = [
  { key: 'RESERVATION', label: 'Rezerwacja', color: '#06b6d4' },
  { key: 'PICKING', label: 'Picking', color: '#8b5cf6' },
  { key: 'PACKING', label: 'Packing', color: '#f97316' },
  { key: 'SHIPPING', label: 'Wysyłka', color: '#22c55e' },
];

const DAY_MS = 24 * 60 * 60_000;

const RANGES: { key: string; label: string; ms: number }[] = [
  { key: '15m', label: '15 min', ms: 15 * 60_000 },
  { key: '1h', label: '1 godz', ms: 60 * 60_000 },
  { key: '6h', label: '6 godz', ms: 6 * 60 * 60_000 },
  { key: '24h', label: '24 godz', ms: DAY_MS },
  { key: '7d', label: '7 dni', ms: 7 * DAY_MS },
  { key: '14d', label: '2 tyg', ms: 14 * DAY_MS },
  { key: '30d', label: '1 mies', ms: 30 * DAY_MS },
  { key: '90d', label: '3 mies', ms: 90 * DAY_MS },
  { key: '180d', label: '6 mies', ms: 180 * DAY_MS },
  { key: '365d', label: '1 rok', ms: 365 * DAY_MS },
];

const SIX_HOURS_MS = 6 * 60 * 60_000;

const GRANULARITIES: { key: string; label: string; ms: number }[] = [
  { key: '1m', label: '1 min', ms: 60_000 },
  { key: '5m', label: '5 min', ms: 5 * 60_000 },
  { key: '15m', label: '15 min', ms: 15 * 60_000 },
  { key: '30m', label: '30 min', ms: 30 * 60_000 },
  { key: '1h', label: '1 godz', ms: 60 * 60_000 },
  { key: '6h', label: '6 godz', ms: 6 * 60 * 60_000 },
  { key: '12h', label: '12 godz', ms: 12 * 60 * 60_000 },
  { key: '1d', label: '1 dzień', ms: DAY_MS },
  { key: '7d', label: '1 tydz', ms: 7 * DAY_MS },
  { key: '30d', label: '1 mies', ms: 30 * DAY_MS },
];

const MIN_BUCKETS = 2;
const MAX_BUCKETS = 500;
const AUTO = 'auto';

function formatDuration(seconds: number | null): string {
  if (seconds == null || Number.isNaN(seconds)) return '';
  if (seconds < 1) return `${Math.round(seconds * 1000)} ms`;
  if (seconds < 60) return `${round1(seconds)} s`;
  if (seconds < 3600) return `${round1(seconds / 60)} min`;
  return `${round1(seconds / 3600)} h`;
}

function round1(value: number): number {
  return Math.round(value * 10) / 10;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [BaseChartDirective],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly sseService = inject(SseService);
  private readonly auth = inject(AuthService);
  private readonly destroyRef = inject(DestroyRef);

  readonly stats = signal<DashboardStats | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly chartData = signal<ChartData<'bar'>>({ labels: [], datasets: [] });

  readonly ranges = RANGES;
  readonly auto = AUTO;
  readonly selectedRange = signal('24h');
  readonly selectedGranularity = signal(AUTO);
  readonly throughputData = signal<ChartData>({ labels: [], datasets: [] });
  readonly leadTimeData = signal<ChartData<'line'>>({ labels: [], datasets: [] });
  readonly failureRateData = signal<ChartData<'line'>>({ labels: [], datasets: [] });
  readonly stages = STAGE_SERIES;
  readonly events = EVENT_SERIES;
  readonly selectedStages = signal<Set<string>>(new Set(STAGE_SERIES.map((s) => s.key)));
  readonly selectedEvents = signal<Set<SseEventType>>(new Set(EVENT_SERIES.map((s) => s.type)));
  readonly throughputMode = signal<'bar' | 'area'>('bar');
  readonly logScale = signal(false);
  private readonly throughputRes = signal<ThroughputResponse | null>(null);
  private readonly leadTimePoints = signal<LeadTimePoint[]>([]);
  private readonly leadTimeBucketMs = signal(0);
  private readonly zone = Intl.DateTimeFormat().resolvedOptions().timeZone;

  readonly chartOptions: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },
    datasets: { bar: { maxBarThickness: 64 } },
  };

  readonly throughputOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false }, // hover a bucket -> all series at once
    plugins: { legend: { display: false } },
    scales: {
      x: { stacked: true, grid: { display: false } },
      y: { stacked: true, beginAtZero: true, ticks: { precision: 0 } },
    },
  };

  readonly failureRateOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { position: 'bottom', labels: { boxWidth: 12, usePointStyle: true } },
      tooltip: { callbacks: { label: (item) => `${item.dataset.label}: ${item.parsed.y}%` } },
    },
    scales: {
      x: { grid: { display: false } },
      y: { beginAtZero: true, max: 100, ticks: { callback: (v) => `${v}%` } },
    },
  };

  readonly leadTimeOptions = computed<ChartOptions<'line'>>(() => ({
    responsive: true,
    maintainAspectRatio: false,
    interaction: { mode: 'index', intersect: false },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (item) => `${item.dataset.label}: ${formatDuration(item.parsed.y)}`,
          footer: (items) => {
            const point = this.leadTimePoints()[items[0]?.dataIndex ?? -1];
            if (!point) return '';
            return `Zakończone: ${point.completedOrders} • p95: ${formatDuration(point.p95TotalSeconds)}`;
          },
        },
      },
    },
    scales: {
      x: { grid: { display: false } },
      y: {
        type: this.logScale() ? 'logarithmic' : 'linear',
        beginAtZero: !this.logScale(),
        ticks: {
          callback: (value) => formatDuration(typeof value === 'number' ? value : Number(value)),
        },
      },
    },
  }));

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

    this.sseService
      .watchDashboard()
      .pipe(
        debounceTime(500),
        switchMap(() => this.dashboardService.getStats()),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({ next: (stats) => this.stats.set(stats) });

    if (this.isOperatorOrAdmin()) {
      this.loadMetrics();
      this.sseService
        .watchDashboard()
        .pipe(debounceTime(1000), takeUntilDestroyed(this.destroyRef))
        .subscribe({ next: () => this.loadMetrics() });
    }
  }

  isOperatorOrAdmin(): boolean {
    return this.auth.isOperatorOrAdmin();
  }

  selectRange(key: string): void {
    if (this.selectedRange() === key) return;
    this.selectedRange.set(key);
    if (!this.granularityOptions().some((g) => g.key === this.selectedGranularity())) {
      this.selectedGranularity.set(AUTO);
    }
    this.loadMetrics();
  }

  selectGranularity(key: string): void {
    if (this.selectedGranularity() === key) return;
    this.selectedGranularity.set(key);
    this.loadMetrics();
  }

  granularityOptions(): { key: string; label: string; ms: number }[] {
    const rangeMs = this.currentRange().ms;
    return GRANULARITIES.filter((g) => {
      const buckets = Math.ceil(rangeMs / g.ms);
      return g.ms <= rangeMs && buckets >= MIN_BUCKETS && buckets <= MAX_BUCKETS;
    });
  }

  private currentRange(): { key: string; label: string; ms: number } {
    return RANGES.find((r) => r.key === this.selectedRange()) ?? RANGES[3];
  }

  private loadMetrics(): void {
    const toMs = Date.now();
    const fromMs = toMs - this.currentRange().ms;
    const granularity = GRANULARITIES.find((g) => g.key === this.selectedGranularity());
    const bucketMs = granularity?.ms;

    forkJoin({
      throughput: this.dashboardService.getThroughput(fromMs, toMs, this.zone, bucketMs),
      leadTime: this.dashboardService.getLeadTime(fromMs, toMs, this.zone, bucketMs),
    }).subscribe({
      next: ({ throughput, leadTime }) => {
        this.throughputRes.set(throughput);
        this.rebuildThroughput();
        this.failureRateData.set(this.buildFailureRate(throughput));
        this.leadTimePoints.set(leadTime.points);
        this.leadTimeBucketMs.set(leadTime.bucketMs);
        this.rebuildLeadTime();
      },
      error: () => {
        /* keep last data; transient errors shouldn't blank the charts */
      },
    });
  }

  private buildFailureRate(res: ThroughputResponse): ChartData<'line'> {
    const labels = res.points.map((p) => this.formatLabel(p.timestamp, res.bucketMs));
    const rate = (fail?: number, ok?: number): number | null => {
      const total = (fail ?? 0) + (ok ?? 0);
      return total === 0 ? null : Math.round(((fail ?? 0) / total) * 1000) / 10;
    };
    return {
      labels,
      datasets: [
        {
          label: 'Rezerwacja',
          data: res.points.map((p) => rate(p.counts.ALLOCATION_FAILED, p.counts.STOCK_RESERVED)),
          borderColor: '#ef4444',
          backgroundColor: '#ef4444',
          tension: 0.3,
          spanGaps: false,
          borderWidth: 2,
          pointRadius: 3,
          pointHoverRadius: 5,
        },
        {
          label: 'Picking',
          data: res.points.map((p) => rate(p.counts.PICK_FAILED, p.counts.ORDER_PICKED)),
          borderColor: '#ec4899',
          backgroundColor: '#ec4899',
          borderDash: [6, 4],
          tension: 0.3,
          spanGaps: false,
          borderWidth: 2,
          pointRadius: 3,
          pointHoverRadius: 5,
        },
      ],
    };
  }

  throughputChartType(): ChartType {
    return this.throughputMode() === 'area' ? 'line' : 'bar';
  }

  toggleThroughputMode(): void {
    this.throughputMode.update((m) => (m === 'bar' ? 'area' : 'bar'));
    this.rebuildThroughput();
  }

  isEventSelected(type: SseEventType): boolean {
    return this.selectedEvents().has(type);
  }

  toggleEvent(type: SseEventType): void {
    const next = new Set(this.selectedEvents());
    if (next.has(type)) {
      if (next.size === 1) return; // keep at least one series visible
      next.delete(type);
    } else {
      next.add(type);
    }
    this.selectedEvents.set(next);
    this.rebuildThroughput();
  }

  private rebuildThroughput(): void {
    const res = this.throughputRes();
    if (!res) return;
    const labels = res.points.map((p) => this.formatLabel(p.timestamp, res.bucketMs));
    const selected = this.selectedEvents();
    const area = this.throughputMode() === 'area';
    this.throughputData.set({
      labels,
      datasets: EVENT_SERIES.filter((s) => selected.has(s.type)).map((s) => ({
        label: s.label,
        data: res.points.map((p) => p.counts[s.type] ?? 0),
        backgroundColor: s.color,
        borderColor: s.color,
        stack: 'events',
        // bar mode
        borderRadius: 2,
        maxBarThickness: 40,
        // area mode (ignored for bars)
        fill: area,
        tension: 0.3,
        pointRadius: area ? 0 : 3,
        borderWidth: 2,
      })),
    });
  }

  private rebuildLeadTime(): void {
    const points = this.leadTimePoints();
    const bucketMs = this.leadTimeBucketMs();
    const labels = points.map((p) => this.formatLabel(p.timestamp, bucketMs));
    const selected = this.selectedStages();
    this.leadTimeData.set({
      labels,
      datasets: STAGE_SERIES.filter((s) => selected.has(s.key)).map((s) => ({
        label: s.label,
        // raw seconds (null = no data -> line gap); the axis/tooltip format adaptively
        data: points.map((p) => p.avgStageSeconds[s.key] ?? null),
        borderColor: s.color,
        backgroundColor: s.color,
        tension: 0.3,
        spanGaps: false,
        borderWidth: 2,
        pointRadius: 3,
        pointHoverRadius: 5,
      })),
    });
  }

  toggleLogScale(): void {
    this.logScale.update((v) => !v);
  }

  isStageSelected(key: string): boolean {
    return this.selectedStages().has(key);
  }

  toggleStage(key: string): void {
    const next = new Set(this.selectedStages());
    if (next.has(key)) {
      if (next.size === 1) return; // keep at least one stage visible
      next.delete(key);
    } else {
      next.add(key);
    }
    this.selectedStages.set(next);
    this.rebuildLeadTime();
  }

  private formatLabel(timestampMs: number, bucketMs: number): string {
    const d = new Date(timestampMs);
    const pad = (n: number) => String(n).padStart(2, '0');
    const date = `${pad(d.getDate())}.${pad(d.getMonth() + 1)}`;
    const time = `${pad(d.getHours())}:${pad(d.getMinutes())}`;
    if (bucketMs >= DAY_MS) return date;
    if (bucketMs >= SIX_HOURS_MS) return `${date} ${time}`;
    return time;
  }

  statusColor(status: string): string {
    return STATUS_COLORS[status] ?? '#94a3b8';
  }

  statusEntries(): [string, number][] {
    const s = this.stats();
    return s ? Object.entries(s.byStatus) : [];
  }
}
