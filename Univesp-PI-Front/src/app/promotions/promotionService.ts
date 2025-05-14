import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, Subject, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Promotion, PromotionRequest } from '../shared/shared-models';

@Injectable({ providedIn: 'root' })
export class PromotionService {
  private apiUrl = 'http://localhost:8080/promotions';
  private promotionListUpdated = new Subject<void>();

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = sessionStorage.getItem('auth-token');
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`
    });
  }

  getPromotions(): Observable<Promotion[]> {
    return this.http.get<Promotion[]>(this.apiUrl, { headers: this.getHeaders() }).pipe(
      tap(promotions => {
        promotions.forEach(p => {
          p.startDate = this.parseDateString(p.startDate);
          p.endDate = this.parseDateString(p.endDate);
        });
      })
    );
  }

  private parseDateString(date: string | Date): Date {
    if (date instanceof Date) return date;
    const [datePart] = date.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    return new Date(year, month - 1, day);
  }

  createPromotion(promotion: PromotionRequest): Observable<Promotion> {
    return this.http.post<Promotion>(this.apiUrl, promotion, {
      headers: this.getHeaders()
    }).pipe(
      tap(promo => {
        promo.startDate = this.parseDateString(promo.startDate);
        promo.endDate = this.parseDateString(promo.endDate);
      })
    );
  }

  updatePromotion(id: number, promotion: PromotionRequest): Observable<Promotion> {
    return this.http.put<Promotion>(`${this.apiUrl}/${id}`, promotion, {
      headers: this.getHeaders()
    }).pipe(
      tap(updatedPromo => {
        updatedPromo.startDate = this.parseDateString(updatedPromo.startDate);
        updatedPromo.endDate = this.parseDateString(updatedPromo.endDate);
        this.refreshPromotionList();
      }),
      catchError(this.handleError)
    );
  }

  deletePromotion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() }).pipe(
      tap(() => this.refreshPromotionList()),
      catchError(this.handleError)
    );
  }

  refreshPromotionList(): void {
    this.promotionListUpdated.next();
  }

  onPromotionListUpdated(): Observable<void> {
    return this.promotionListUpdated.asObservable();
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    const errorMessage = error.error?.message || 'Erro desconhecido na gestão de promoções';
    console.error('Erro no PromotionService:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
