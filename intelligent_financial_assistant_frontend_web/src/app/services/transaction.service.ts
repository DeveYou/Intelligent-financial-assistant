import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction } from '../models/transaction.model';
import { environment } from '../../environments/environment';
import { AuthService } from '../core/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private apiUrl = `${environment.apiBaseUrl}/transactions-service/admin/transactions`;

  constructor(private http: HttpClient) { }

  // Pour admin - Récupérer toutes les transactions avec pagination
  getAllTransactions(params: any): Observable<any> {
    let httpParams = new HttpParams();

    // Ajouter tous les paramètres de filtrage
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
        let value = params[key];
        // Format dates for OffsetDateTime (append :00Z if from datetime-local)
        if ((key === 'startDate' || key === 'endDate') && typeof value === 'string' && value.match(/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/)) {
          value = value + ':00Z';
        }
        httpParams = httpParams.append(key, value);
      }
    });

    // Ajouter la pagination par défaut si non spécifiée
    if (params.page === undefined || params.page === null) httpParams = httpParams.append('page', '0');
    if (params.size === undefined || params.size === null) httpParams = httpParams.append('size', '10');

    return this.http.get<any>(this.apiUrl, { params: httpParams });
  }

  getByReference(reference: string): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/reference/${reference}`);
  }

  createDeposit(data: { bankAccountId: number, amount: number, reason: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/deposit`, data);
  }

  createWithdrawal(data: { bankAccountId: number, amount: number, reason: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/withdrawal`, data);
  }

  createTransfer(data: { bankAccountId: number, amount: number, recipientIban: string, reason: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/transfer`, data);
  }

  getTransactionStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }

  getDailyTransactionStats(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/daily`);
  }
}
