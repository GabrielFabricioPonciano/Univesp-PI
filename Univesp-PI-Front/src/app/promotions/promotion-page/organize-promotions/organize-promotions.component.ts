import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';
import { Product, ProductUpdate, Promotion, PromotionRequest } from '../../../shared/shared-models';
import { ProductService } from '../../../products/productService';
import { PromotionService } from '../../promotionService';
import { NgForOf, NgIf } from "@angular/common";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatIcon } from "@angular/material/icon";
import { MatFormField, MatLabel } from "@angular/material/form-field";
import {MatOption, MatSelect, MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";

@Component({
  selector: 'app-organize-promotions',
  templateUrl: './organize-promotions.component.html',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgForOf,
    NgIf,
    MatCheckbox,
    MatIcon,
    MatLabel,
    MatFormField,
    MatSelect,
    MatOption,
    MatInputModule,
    MatSelectModule,
  ],
  styleUrls: ['./organize-promotions.component.scss']
})
export class OrganizePromotionsDialogComponent implements OnInit {
  selectedProducts: Product[] = [];
  filterControl = new FormControl('');
  filteredProducts: Product[] = [];
  errorMessage = '';
  todayISO = new Date().toISOString().split('T')[0];

  promotionForm: PromotionRequest = {
    description: '',
    discountPercentage: 0,
    startDate: '',
    endDate: '',
    status: 'ACTIVE'
  };

  constructor(
    public dialogRef: MatDialogRef<OrganizePromotionsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      products: Product[],
      promotion?: Promotion,
      promotions: Promotion[]
    },
    private productService: ProductService,
    private promotionService: PromotionService
  ) {
    if (data.promotion) {
      this.promotionForm = {
        description: data.promotion.description,
        discountPercentage: data.promotion.discountPercentage,
        startDate: this.formatDate(data.promotion.startDate),
        endDate: this.formatDate(data.promotion.endDate),
        status: data.promotion.status
      };
    }
  }

  ngOnInit(): void {
    this.filteredProducts = this.data.products;
    this.filterControl.valueChanges.subscribe(value => {
      this.filteredProducts = this.data.products.filter(p =>
        p.productName.toLowerCase().includes((value || '').toLowerCase())
      );
    });
  }

  savePromotion(): void {
    if (this.data.promotion) {
      this.updatePromotion();
    } else {
      this.createPromotion();
    }
  }

  isProductSelected(product: Product): boolean {
    return this.selectedProducts.some(p => p.productId === product.productId);
  }

  toggleProductSelection(product: Product): void {
    const index = this.selectedProducts.findIndex(p => p.productId === product.productId);
    if (index > -1) {
      this.selectedProducts.splice(index, 1);
    } else {
      this.selectedProducts.push(product);
    }
  }

  private applyPromotion(promotionId: number): void {
    const updates = this.selectedProducts.map(product => {
      const updateData: ProductUpdate = {
        productName: product.productName,
        productType: product.productType,
        quantity: product.quantity,
        batchNumber: product.batchNumber,
        description: product.description,
        expirationDate: product.expirationDate instanceof Date ?
          product.expirationDate.toISOString().split('T')[0] :
          product.expirationDate,
        profitMargin: product.profitMargin,
        batchPrice: product.batchPrice,
        status: product.status,
        promotionId: promotionId
      };
      return this.productService.updateProduct(updateData, product.productId);
    });

    forkJoin(updates).subscribe({
      next: () => this.dialogRef.close(true),
      error: (err) => this.showError('Erro ao aplicar promoção: ' + err.message)
    });
  }

  private createPromotion(): void {
    this.promotionService.createPromotion(this.promotionForm).subscribe({
      next: (promo) => this.applyPromotion(promo.id),
      error: (err) => this.showError('Erro ao criar promoção: ' + err.message)
    });
  }

  private updatePromotion(): void {
    if (!this.data.promotion) return;

    this.promotionService.updatePromotion(
      this.data.promotion.id,
      this.promotionForm
    ).subscribe({
      next: () => this.applyPromotion(this.data.promotion!.id),
      error: (err) => this.showError('Erro ao atualizar promoção: ' + err.message)
    });
  }

  private formatDate(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  private showError(message: string): void {
    this.errorMessage = message;
    setTimeout(() => this.errorMessage = '', 5000);
  }

  cancel(): void {
    this.dialogRef.close();
  }
}
