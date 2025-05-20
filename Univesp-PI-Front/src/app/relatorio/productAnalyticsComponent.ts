import {
  Component, OnInit, OnDestroy, inject, ElementRef, ViewChild, ChangeDetectorRef
} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs/operators';
import { Product, Promotion } from '../shared/shared-models';
import { ProductService } from "../products/productService";
import { PromotionService } from "../promotions/promotionService";
import {
  Chart,
  registerables,
  ChartOptions,
  ChartType,
  DoughnutControllerChartOptions,
  BarControllerChartOptions,
  LineControllerChartOptions
} from 'chart.js';

type TimeFilter = '7days' | '30days' | 'all';

Chart.register(...registerables);

@Component({
  selector: 'app-product-analytics',
  standalone: true,
  templateUrl: './productAnalyticsComponent.html',
  styleUrls: ['./productAnalyticsComponent.scss'],
  imports: [
    CommonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatTableModule,
    DatePipe
  ]
})
export class ProductAnalyticsComponent implements OnInit, OnDestroy {
  private productService = inject(ProductService);
  private promotionService = inject(PromotionService);
  private cdr = inject(ChangeDetectorRef);

  @ViewChild('categoryChart', { static: false }) categoryChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('stockChart', { static: false }) stockChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('marginChart', { static: false }) marginChartRef!: ElementRef<HTMLCanvasElement>;

  // Dados e estado
  products: Product[] = [];
  promotions: Promotion[] = [];
  filteredProducts: Product[] = [];
  displayedColumns = ['productName', 'quantity', 'expiration'];
  lowStockThreshold = 10;
  isLoading = true;
  selectedCategory = 'all';
  timeFilter: TimeFilter = 'all';

  // Gráficos
  private charts: Chart[] = [];

  async ngOnInit() {
    await this.loadData();
  }

  ngOnDestroy() {
    this.destroyCharts();
  }

  private async loadData() {
    try {
      const [products, promotions] = await Promise.all([
        this.productService.getProducts().pipe(
          finalize(() => this.isLoading = false)
        ).toPromise(),
        this.promotionService.getPromotions().toPromise()
      ]);

      this.processData(products || [], promotions || []);
      this.applyFilters();
      this.initChartsAfterRender();

    } catch (error) {
      console.error('Erro ao carregar dados:', error);
      this.isLoading = false;
    }
  }

  private processData(products: Product[], promotions: Promotion[]) {
    this.products = products.map(p => ({
      ...p,
      expirationDate: p.expirationDate ? new Date(p.expirationDate) : undefined,
      createdAt: this.safeDateParse(p.createdAt),
      updatedAt: this.safeDateParse(p.updatedAt)
    }));

    this.promotions = promotions
      .filter(p => p.status === 'ACTIVE')
      .map(p => ({
        ...p,
        startDate: new Date(p.startDate),
        endDate: new Date(p.endDate)
      }));
  }

  private initChartsAfterRender() {
    this.cdr.detectChanges();
    setTimeout(() => this.initCharts(), 0);
  }

  private safeDateParse(date: unknown): Date {
    try {
      return date ? new Date(date as string) : new Date();
    } catch {
      return new Date();
    }
  }

  protected applyFilters() {
    this.destroyCharts();
    this.filteredProducts = this.products.filter(p =>
      (this.selectedCategory === 'all' || p.productType === this.selectedCategory) &&
      this.applyTimeFilter(p.createdAt)
    );
    this.initChartsAfterRender();
  }

  private applyTimeFilter(createdAt?: Date): boolean {
    if (!createdAt) return false;
    const diffDays = this.daysDiff(new Date(), createdAt);

    return this.timeFilter === '7days' ? diffDays <= 7 :
      this.timeFilter === '30days' ? diffDays <= 30 : true;
  }

  private daysDiff(date1: Date, date2: Date): number {
    return Math.floor((date1.getTime() - date2.getTime()) / (1000 * 3600 * 24));
  }

  private getChartOptions<T extends ChartType>(type: T): ChartOptions<T> {
    const commonOptions: ChartOptions<'doughnut' | 'bar' | 'line'> = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'right' as const,
        },
        tooltip: {
          enabled: true,
        }
      }
    };

    switch(type) {
      case 'doughnut':
        return {
          ...commonOptions,
          cutout: '60%'
        } as ChartOptions<T>;

      case 'bar':
        return {
          ...commonOptions,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Quantidade'
              }
            }
          }
        } as ChartOptions<T>;

      case 'line':
        return {
          ...commonOptions,
          scales: {
            y: {
              beginAtZero: true,
              title: {
                display: true,
                text: 'Margem (%)'
              }
            }
          },
          elements: {
            line: {
              tension: 0.4
            }
          }
        } as ChartOptions<T>;

      default:
        return commonOptions as ChartOptions<T>;
    }
  }

  private initCharts() {
    this.destroyCharts();

    if (this.categoryChartRef?.nativeElement) {
      this.initCategoryChart();
    }
    if (this.stockChartRef?.nativeElement) {
      this.initStockChart();
    }
    if (this.marginChartRef?.nativeElement) {
      this.initMarginChart();
    }
  }

  private destroyCharts() {
    this.charts.forEach(chart => {
      if (chart) {
        chart.destroy();
      }
    });
    this.charts = [];
  }

  private initCategoryChart() {
    const ctx = this.categoryChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data = this.getCategoryData();
    const chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: data.labels,
        datasets: [{
          data: data.values,
          backgroundColor: this.generateColors(data.labels.length),
          borderWidth: 2
        }]
      },
      options: this.getChartOptions('doughnut')
    });

    this.charts.push(chart);
  }

  private initStockChart() {
    const ctx = this.stockChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data = this.getStockData();
    const chart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: data.labels,
        datasets: [{
          label: 'Estoque Atual',
          data: data.values,
          backgroundColor: '#3498db'
        }]
      },
      options: this.getChartOptions('bar')
    });

    this.charts.push(chart);
  }

  private initMarginChart() {
    const ctx = this.marginChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data = this.getMarginData();
    const chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: data.labels,
        datasets: [{
          label: 'Margem de Lucro (%)',
          data: data.values,
          borderColor: '#2ecc71',
          fill: false
        }]
      },
      options: this.getChartOptions('line')
    });

    this.charts.push(chart);
  }

  // Métodos auxiliares
  getCriticalStock(): Product[] {
    return this.filteredProducts.filter(p => p.quantity < this.lowStockThreshold);
  }

  private getCategoryData() {
    const categories = new Map<string, number>();
    this.filteredProducts.forEach(p => {
      const category = p.productType || 'Outros';
      categories.set(category, (categories.get(category) || 0) + 1);
    });

    return {
      labels: Array.from(categories.keys()),
      values: Array.from(categories.values())
    };
  }

  private getStockData() {
    return {
      labels: this.filteredProducts.map(p => p.productName),
      values: this.filteredProducts.map(p => p.quantity)
    };
  }

  private getMarginData() {
    return {
      labels: this.filteredProducts.map(p => p.productName),
      values: this.filteredProducts.map(p => p.profitMargin)
    };
  }

  protected generateColors(count: number): string[] {
    return Array.from({ length: count }, (_, i) =>
      `hsl(${(i * 137.508) % 360}, 70%, 50%)`
    );
  }

  get keyMetrics() {
    return {
      totalProducts: this.filteredProducts.length,
      lowStock: this.filteredProducts.filter(p => p.quantity < this.lowStockThreshold).length,
      totalValue: this.filteredProducts.reduce((sum, p) => sum + (p.unitPrice * p.quantity), 0),
      avgMargin: this.filteredProducts.length > 0
        ? this.filteredProducts.reduce((sum, p) => sum + p.profitMargin, 0) / this.filteredProducts.length
        : 0
    };
  }

  get productCategories(): string[] {
    return [...new Set(this.products.map(p => p.productType))];
  }
}
