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
        httpParams = httpParams.append(key, params[key]);
      }
    });

    // Ajouter la pagination par défaut si non spécifiée
    if (!params.page) httpParams = httpParams.append('page', '0');
    if (!params.size) httpParams = httpParams.append('size', '10');

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
}
