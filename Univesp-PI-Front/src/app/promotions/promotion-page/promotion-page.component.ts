import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { ProductService } from "../../products/productService";
import { Product, ProductUpdate, Promotion } from "../../shared/shared-models";
import { PromotionService } from "../promotionService";
import { MatIcon } from "@angular/material/icon";
import { forkJoin } from "rxjs";
import { OrganizePromotionsDialogComponent } from "./organize-promotions/organize-promotions.component";
import {MatInputModule} from "@angular/material/input";
import {MatSelectModule} from "@angular/material/select";

@Component({
  selector: 'app-promotion-page',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    RouterLink,
    DatePipe,
    MatProgressSpinner,
    MatIcon,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './promotion-page.component.html',
  styleUrls: ['./promotion-page.component.scss']
})
export class PromotionPageComponent implements OnInit {
  products: Product[] = [];
  promotions: Promotion[] = [];
  filteredProducts = {
    withPromotion: [] as Product[],
    withoutPromotion: [] as Product[]
  };
  loading = true;

  constructor(
    private productService: ProductService,
    private promotionService: PromotionService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.loading = true;
    forkJoin([
      this.productService.getProducts(),
      this.promotionService.getPromotions()
    ]).subscribe({
      next: ([products, promotions]) => {
        this.products = products;
        this.promotions = promotions;
        this.filterProducts();
        this.loading = false;
      },
      error: (err) => {
        this.handleError(err);
        this.loading = false; // Garante que o loading seja desativado mesmo em caso de erro
      }
    });
  }

  private filterProducts(): void {
    this.filteredProducts = {
      withPromotion: this.products.filter(p => p.promotionId),
      withoutPromotion: this.products.filter(p => !p.promotionId)
    };
  }

  getPromotionDetails(promotionId?: number): Promotion | null {
    return this.promotions.find(p => p.id === promotionId) || null;
  }

  removePromotionFromProduct(product: Product): void {
    if (!confirm('Remover promoção deste produto?')) return;

    const updateData: ProductUpdate = {
      ...product,
      promotionId: null,
      expirationDate: this.formatDate(product.expirationDate)
    };

    this.productService.updateProduct(updateData, product.productId).subscribe({
      next: () => this.loadData(),
      error: (err) => this.handleError(err)
    });
  }

  openPromotionDialog(promotion?: Promotion): void {
    const dialogRef = this.dialog.open(OrganizePromotionsDialogComponent, {
      width: '100vw',
      height: '100vh',
      maxWidth: '100vw',
      panelClass: 'full-screen-dialog',
      data: {
        products: this.products,
        promotion: promotion,
        promotions: this.promotions
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadData();
    });
  }

  confirmDelete(promotionId: number): void {
    if (confirm('Excluir esta promoção permanentemente?')) {
      this.promotionService.deletePromotion(promotionId).subscribe({
        next: () => {
          this.promotions = this.promotions.filter(p => p.id !== promotionId);
          this.filterProducts();
        },
        error: (err) => this.handleError(err)
      });
    }
  }

  private formatDate(date?: Date | string): string {
    if (!date) return '';
    const d = date instanceof Date ? date : new Date(date);
    return d.toISOString().split('T')[0];
  }

  private handleError(error: Error): void {
    console.error('Erro:', error.message);
    this.loading = false;
  }
}
