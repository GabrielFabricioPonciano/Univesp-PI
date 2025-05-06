import { Component, OnInit } from '@angular/core';
import { ProductService, Product } from '../productService';
import {FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { EditProductDialogComponent } from './edit-product-dialog/edit-product-dialog.component';
import { ToolbarComponent } from '../../toolbar/toolbar.component';
import { ProductFormComponent } from '../product-form/product-form.component';
import { AlterProductComponent } from './alterproduct/alterproduct.component';


@Component({
  selector: 'app-product-list',
  standalone: true,
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.scss'],
  imports: [CommonModule, MatDialogModule, ToolbarComponent, ReactiveFormsModule, FormsModule],
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];
  currentPage: number = 1;
  productsPerPage: number = 6;
  showSearchBar: boolean = false;
  sortCriteria: string = 'productName';
  sortDirection: string = 'asc';
  errorMessage: string = '';

  filters: { searchField: FormControl; searchTerm: FormControl }[] = [];

  displayFields = [
    { key: 'productId', label: 'ID', selected: true },
    { key: 'productName', label: 'Produto', selected: true },
    { key: 'productType', label: 'Tipo', selected: true },
    { key: 'quantity', label: 'Quantidade', selected: true },
    { key: 'numberLote', label: 'Número do Lote', selected: true },
    { key: 'description', label: 'Descrição', selected: true },
    { key: 'dateExpiration', label: 'Data de Validade', selected: true },
    { key: 'gainPercentage', label: 'Porcentagem de Ganho', selected: true },
    { key: 'priceForLote', label: 'Preço por Lote', selected: true },
    { key: 'priceForLotePercent', label: 'Preço por Lote com Percentual', selected: true },
    { key: 'priceForUnity', label: 'Preço Unitário', selected: true },
    { key: 'priceForUnityPercent', label: 'Preço Unitário com Percentual', selected: true },
    { key: 'createdAt', label: 'Criado em', selected: true },
    { key: 'updatedAt', label: 'Atualizado em', selected: true },
    { key: 'status', label: 'Status', selected: true },
    { key: 'promotion', label: 'Promoção', selected: true },
  ];
  protected showFieldSelection!: boolean;


  constructor(private productService: ProductService, public dialog: MatDialog) {}

  ngOnInit(): void {
    this.loadProducts();
    this.addFilter();
  }

  loadProducts(): void {
    this.productService.getProducts().subscribe(
      (products) => {
        this.products = products.map((product) => ({ ...product, showActions: false }));
        this.applyFilters();
      },
      (error) => {
        this.errorMessage = 'Erro ao carregar produtos';
        console.error(error);
      }
    );
  }

  addFilter(): void {
    const newFilter = { searchField: new FormControl('productName'), searchTerm: new FormControl('') };
    this.filters.push(newFilter);
    this.observeFilterChanges(newFilter);
  }

  observeFilterChanges(filter: { searchField: FormControl; searchTerm: FormControl }): void {
    filter.searchField.valueChanges.subscribe(() => this.applyFilters());
    filter.searchTerm.valueChanges.subscribe(() => this.applyFilters());
  }

  removeFilter(index: number): void {
    this.filters.splice(index, 1);
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredProducts = this.products.filter((product) =>
      this.filters.every((filter) => {
        const value = (product as any)[filter.searchField.value]?.toString().toLowerCase() || '';
        return value.includes(filter.searchTerm.value.toLowerCase());
      })
    );
    this.updatePaginatedProducts();
  }

  sortProducts(): void {
    this.filteredProducts.sort((a, b) => {
      const valueA = this.extractValue(a, this.sortCriteria);
      const valueB = this.extractValue(b, this.sortCriteria);

      if (typeof valueA === 'string' && typeof valueB === 'string') {
        return this.sortDirection === 'asc' ? valueA.localeCompare(valueB) : valueB.localeCompare(valueA);
      }
      if (typeof valueA === 'number' && typeof valueB === 'number') {
        return this.sortDirection === 'asc' ? valueA - valueB : valueB - valueA;
      }
      if (valueA instanceof Date && valueB instanceof Date) {
        return this.sortDirection === 'asc'
          ? valueA.getTime() - valueB.getTime()
          : valueB.getTime() - valueA.getTime();
      }
      return 0;
    });

    this.updatePaginatedProducts();
  }

  toggleSortDirection(): void {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.sortProducts();
  }

  extractValue(obj: Product, key: string): any {
    const keys = key.split('.');
    let value: any = obj;
    for (const k of keys) {
      value = value ? value[k] : undefined;
    }
    return value;
  }

  updatePaginatedProducts(): void {
    const startIndex = (this.currentPage - 1) * this.productsPerPage;
    this.paginatedProducts = this.filteredProducts.slice(startIndex, startIndex + this.productsPerPage);
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.updatePaginatedProducts();
  }

  toggleAllFields(): void {
    const allSelected = this.displayFields.every((field) => field.selected);
    this.displayFields.forEach((field) => (field.selected = !allSelected));
    this.applyFilters();
  }

  toggleField(key: string): void {
    const field = this.displayFields.find((f) => f.key === key);
    if (field) field.selected = !field.selected;
    this.applyFilters();
  }

  isFieldVisible(key: string): boolean {
    return this.displayFields.some((field) => field.key === key && field.selected);
  }

  openAlterDialog(product: Product): void {
    const dialogRef = this.dialog.open(AlterProductComponent, {
      data: product,
      width: '500px',
      maxWidth: '100%',
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadProducts();
    });
  }

  openEditDialog(product: Product): void {
    const dialogRef = this.dialog.open(EditProductDialogComponent, {
      data: product,
      width: '100%',
      maxWidth: '600px',
      maxHeight: '90vh',
      autoFocus: false,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadProducts();
    });
  }
  onToggleSearch(): void {
    this.showSearchBar = !this.showSearchBar;
  }
  totalPages(): number {
    return Math.ceil(this.filteredProducts.length / this.productsPerPage);
  }
  openAddDialog(): void {
    const dialogRef = this.dialog.open(ProductFormComponent, {
      width: '100%',
      maxWidth: '600px',
      maxHeight: '90vh',
      autoFocus: false,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.loadProducts();
      }
    });
  }
  fontSize: number = 16;

  adjustFontSize(action: string): void {
    const minFontSize = 12;
    const maxFontSize = 24;

    if (action === 'increase' && this.fontSize < maxFontSize) {
      this.fontSize += 2;
    } else if (action === 'decrease' && this.fontSize > minFontSize) {
      this.fontSize -= 2;
    }
  }

  confirmDelete(productId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent);
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.productService.deleteProduct(productId).subscribe(() => this.loadProducts());
      }
    });
  }

  toggleFieldSelectionVisibility(): void {
    this.showFieldSelection = !this.showFieldSelection;
  }

}
