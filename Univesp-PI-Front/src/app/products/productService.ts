import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, Subject, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface Promotion {
  promotionId: number;
  promotionDescription?: string;
  discountPercentage?: number;
  startDate?: Date;
  endDate?: Date;
  status?: 'ACTIVE' | 'INACTIVE';
}

export interface Product {
  productId: number;
  productName: string;
  productType: string;
  quantity: number;
  batchNumber: string;
  description: string;
  expirationDate?: string | Date;
  profitMargin: number;
  batchPrice: number;
  unitPrice: number;
  finalPrice: number;
  createdAt?: Date;
  updatedAt?: Date;
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED';
  promotionId?: number;
  showActions?: boolean;
}
export interface ProductUpdate {
  productName: string;
  productType: string;
  quantity: number;
  batchNumber: string;
  description: string;
  expirationDate?: string | Date; // Adicionado operador de opcional
  profitMargin: number;
  batchPrice: number;
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED';
  promotionId?: number | null;
}
@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private apiUrl = 'http://localhost:8080/products'; // URL da API
  private productListUpdated = new Subject<void>(); // Sujeito para atualizar a lista

  constructor(private http: HttpClient) {}

  // Headers incluindo o token JWT da sessão
  private getHeaders(): HttpHeaders {
    const token = sessionStorage.getItem('auth-token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  // ========= Funções CRUD =========

  // Busca todos os produtos
  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(this.apiUrl, { headers: this.getHeaders() }).pipe(
      tap((products) => {
        console.log('Produtos recebidos:', products);
        this.convertDates(products); // Converte datas recebidas
      }),
      catchError(this.handleError) // Trata erros
    );
  }

  // Busca produtos com estoque abaixo de um limite especificado
  getCriticalStockProducts(threshold: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/critical-stock?threshold=${threshold}`, { headers: this.getHeaders() }).pipe(
      tap((products) => {
        console.log('Produtos com estoque crítico recebidos:', products);
        this.convertDates(products);
      }),
      catchError(this.handleError)
    );
  }


  // Cria um novo produto
  saveProduct(product: Product): Observable<Product> {
    this.formatDate(product); // Formata datas antes de enviar
    return this.http.post<Product>(this.apiUrl, product, { headers: this.getHeaders() }).pipe(
      tap(() => this.refreshProductList()), // Notifica sobre atualização de lista
      catchError(this.handleError)
    );
  }

  // Deleta um produto
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() }).pipe(
      tap(() => this.refreshProductList()), // Notifica sobre atualização de lista
      catchError(this.handleError)
    );
  }

  // Método updateProduct simplificado
  updateProduct(updatedProduct: ProductUpdate, productId: number): Observable<Product> {
    return this.http.put<Product>(
      `${this.apiUrl}/${productId}`,
      updatedProduct,
      { headers: this.getHeaders() }
    ).pipe(
      tap(() => this.refreshProductList()),
      catchError(this.handleError)
    );
  }

  // ========= Funções de Utilidade =========

  // Converte strings de datas recebidas em objetos Date
  private convertDates(products: Product[]): void {
    products.forEach((product) => {
      // Corrige 'dateExpiration' para 'expirationDate'
      if (typeof product.expirationDate === 'string') {
        product.expirationDate = new Date(product.expirationDate);
      }
    });
  }

  // Formata datas para o formato ISO antes de enviar ao backend
  private formatDate(product: Product): void {
    if (product.expirationDate instanceof Date) {
      product.expirationDate = product.expirationDate.toISOString().split('T')[0];
    }
  }

  // Notifica os assinantes quando a lista de produtos for atualizada
  refreshProductList(): void {
    this.productListUpdated.next();
  }

  // Permite que outros componentes assinem as atualizações da lista de produtos
  onProductListUpdated(): Observable<void> {
    return this.productListUpdated.asObservable();
  }

  // Tratamento de erros genérico
  private handleError(error: HttpErrorResponse): Observable<never> {
    const errorMessage = error.error instanceof ErrorEvent
      ? `Erro: ${error.error.message}`
      : `Código do erro: ${error.status}\nMensagem: ${error.message}`;
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
