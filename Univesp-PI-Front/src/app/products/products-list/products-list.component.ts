import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { EditProductDialogComponent } from './edit-product-dialog/edit-product-dialog.component';
import { ToolbarComponent } from '../../toolbar/toolbar.component';
import { ProductFormComponent } from '../product-form/product-form.component';
import { AlterProductComponent } from './alterproduct/alterproduct.component';
import { Subject, debounceTime, distinctUntilChanged, takeUntil } from 'rxjs';
import {Product} from "../../shared/shared-models";
import {ProductService} from "../productService";

@Component({
  selector: 'app-product-list',
  standalone: true,
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.scss'],
  imports: [
    CommonModule,
    MatDialogModule,
    ToolbarComponent,
    ReactiveFormsModule,
    FormsModule
  ],
})
export class ProductListComponent implements OnInit, OnDestroy {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];
  currentPage: number = 1;
  productsPerPage: number = 6;
  showSearchBar: boolean = false;
  sortCriteria: string = 'productName';
  sortDirection: string = 'asc';
  errorMessage: string = '';
  fontSize: number = 16;
  showFieldSelection: boolean = false;

  displayFields = [
    { key: 'productId', label: 'ID', selected: true },
    { key: 'productName', label: 'Produto', selected: true },
    { key: 'productType', label: 'Tipo', selected: true },
    { key: 'quantity', label: 'Quantidade', selected: true },
    { key: 'batchNumber', label: 'Número do Lote', selected: true },
    { key: 'description', label: 'Descrição', selected: true },
    { key: 'expirationDate', label: 'Data de Validade', selected: true },
    { key: 'profitMargin', label: 'Margem de Lucro', selected: true },
    { key: 'batchPrice', label: 'Preço por Lote', selected: true },
    { key: 'unitPrice', label: 'Preço Unitário', selected: true },
    { key: 'finalPrice', label: 'Preço Final', selected: true },
    { key: 'createdAt', label: 'Criado em', selected: true },
    { key: 'updatedAt', label: 'Atualizado em', selected: true },
    { key: 'status', label: 'Status', selected: true },
    { key: 'promotionId', label: 'ID da Promoção', selected: true }
  ];

  searchForm = new FormGroup({
    generalSearch: new FormControl(''),
    searchField: new FormControl('productName'),
    searchTerm: new FormControl(''),
    status: new FormControl('all'),
    minQuantity: new FormControl<number | null>(null),
    maxQuantity: new FormControl<number | null>(null),
    dateRange: new FormGroup({
      start: new FormControl<string | null>(null),
      end: new FormControl<string | null>(null)
    })
  });

  private destroy$ = new Subject<void>();

  constructor(
    private productService: ProductService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.setupSearch();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products.map(product => ({
          ...product,
          showActions: false
        }));
        this.applyFilters();
      },
      error: (error) => {
        this.errorMessage = 'Erro ao carregar produtos';
        console.error(error);
      }
    });
  }

  private setupSearch(): void {
    this.searchForm.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => this.applyFilters());
  }

  private applyFilters(): void {
    const filters = this.searchForm.value;

    this.filteredProducts = this.products.filter(product => {
      return (
        this.matchesGeneralSearch(product, filters.generalSearch) &&
        this.matchesFieldSearch(product, filters.searchField, filters.searchTerm) &&
        this.matchesStatus(product, filters.status) &&
        this.matchesQuantityRange(product, filters.minQuantity, filters.maxQuantity) &&
        this.matchesDateRange(product, filters.dateRange?.start, filters.dateRange?.end)
      );
    });

    this.sortProducts();
    this.updatePaginatedProducts();
  }

  private matchesGeneralSearch(product: Product, term: string | null | undefined): boolean {
    if (!term) return true;
    const searchTerm = term.toLowerCase();
    return Object.values(product).some(value =>
      String(value).toLowerCase().includes(searchTerm)
    );
  }

  private matchesFieldSearch(product: Product, field: string | null | undefined, term: string | null | undefined): boolean {
    if (!term || !field) return true;
    const fieldValue = product[field as keyof Product];
    return String(fieldValue).toLowerCase().includes(term.toLowerCase());
  }

  private matchesStatus(product: Product, status: string | null | undefined): boolean {
    return status === 'all' || !status || product.status === status;
  }

  private matchesQuantityRange(product: Product, min: number | null | undefined, max: number | null | undefined): boolean {
    const minVal = min ?? 0;
    const maxVal = max ?? Infinity;
    return product.quantity >= minVal && product.quantity <= maxVal;
  }

  private matchesDateRange(product: Product, start: string | null | undefined, end: string | null | undefined): boolean {
    if (!start && !end) return true;

    // Verifica se a data de expiração é válida
    if (!product.expirationDate) return false;

    // Converte as datas garantindo tipos válidos
    const expiration = new Date(product.expirationDate);
    const startDate = start ? new Date(start) : new Date(0);
    const endDate = end ? new Date(end) : new Date(8640000000000000);

    return expiration >= startDate && expiration <= endDate;
  }

  resetFilters(): void {
    this.searchForm.reset({
      generalSearch: '',
      searchField: 'productName',
      searchTerm: '',
      status: 'all',
      minQuantity: null,
      maxQuantity: null,
      dateRange: { start: null, end: null }
    });
  }

  sortProducts(): void {
    this.filteredProducts.sort((a, b) => {
      const valueA = this.extractValue(a, this.sortCriteria);
      const valueB = this.extractValue(b, this.sortCriteria);

      if (typeof valueA === 'string' && typeof valueB === 'string') {
        return this.sortDirection === 'asc'
          ? valueA.localeCompare(valueB)
          : valueB.localeCompare(valueA);
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
  }

  private extractValue(obj: Product, key: string): any {
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

  // Restante dos métodos mantidos
  toggleSortDirection(): void {
    this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    this.sortProducts();
    this.updatePaginatedProducts();
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.updatePaginatedProducts();
  }

  toggleAllFields(): void {
    const allSelected = this.displayFields.every(field => field.selected);
    this.displayFields.forEach(field => field.selected = !allSelected);
  }

  toggleField(key: string): void {
    const field = this.displayFields.find(f => f.key === key);
    if (field) field.selected = !field.selected;
  }

  isFieldVisible(key: string): boolean {
    return this.displayFields.some(field => field.key === key && field.selected);
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
      width: '100vw',
      height: '100vh',
      maxWidth: 'none',
      panelClass: 'custom-dialog-container',
      autoFocus: false
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) this.loadProducts();
    });
  }

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
