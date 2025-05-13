import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MatDialog, MatDialogModule } from '@angular/material/dialog'; // Para o diálogo
import { Product, ProductService } from "../../products/productService";
import { Promotion, PromotionService } from "../promotionService";
import { OrganizePromotionsDialogComponent } from './organize-promotions/organize-promotions.component';
import {RouterLink} from "@angular/router"; // O diálogo de organização de promoções

@Component({
  selector: 'app-promotion-page',
  standalone: true,
    imports: [CommonModule, FormsModule, HttpClientModule, MatDialogModule, RouterLink],
  templateUrl: './promotion-page.component.html',
  styleUrls: ['./promotion-page.component.scss']
})
export class PromotionPageComponent implements OnInit {
  products: Product[] = [];
  productsWithPromotion: Product[] = [];
  productsWithoutPromotion: Product[] = [];
  promotions: Promotion[] = [];  // Lista de promoções
  selectedProduct?: Product;
  loading: boolean = false;


  constructor(
    private productService: ProductService,
    private promotionService: PromotionService,
    public dialog: MatDialog // Para abrir o diálogo
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadPromotions(); // Carregar as promoções também
  }

  getPromotionById(promotionId?: number): Promotion | undefined {
    return this.promotions.find(p => p.promotionId === promotionId);
  }

  // Carregar lista de produtos e separar entre com e sem promoção
  loadProducts(): void {
    this.loading = true;
    this.productService.getProducts().subscribe(products => {
      this.products = products;
      // 👇 Corrigir para usar promotionId
      this.productsWithPromotion = products.filter(p => p.promotionId);
      this.productsWithoutPromotion = products.filter(p => !p.promotionId);
      this.loading = false;
    });
  }

  // Carregar promoções do backend
  loadPromotions(): void {
    this.promotionService.getPromotions().subscribe(promotions => {
      this.promotions = promotions;
    });
  }

  removePromotion(product: Product): void {
    if (confirm('Tem certeza que deseja remover a promoção?')) {
      this.loading = true;
      product.promotionId = undefined; // Alterado para promotionId
      this.productService.updateProduct(product, product.productId).subscribe(() => {
        this.loadProducts();
      });
    }
  }

  selectProduct(product: Product): void {
    this.selectedProduct = product;
  }

  // Abrir o diálogo de organizar promoções
  openOrganizePromotionsDialog(): void {
    // Abre o diálogo com o componente OrganizePromotionsDialogComponent
    const dialogRef = this.dialog.open(OrganizePromotionsDialogComponent, {
      width: '80%',           // Define a largura do diálogo
      height: '80%',          // Define a altura do diálogo
      panelClass: 'custom-dialog-container', // Classe CSS personalizada para estilização

      // Passa os dados necessários para o componente do diálogo
      data: {
        products: this.products,
        promotions: this.promotions
      }
    });

    // Executa quando o diálogo é fechado
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProducts(); // Recarrega a lista de produtos caso o diálogo retorne um valor
      }
    });
  }
}
