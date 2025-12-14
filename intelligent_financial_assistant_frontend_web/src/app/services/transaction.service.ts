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
  private apiUrl = `${environment.apiBaseUrl}/transactions`;

  constructor(private http: HttpClient, private authService: AuthService) { }

  private buildHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  deposit(depositRequest: any): Observable<Transaction> {
    // ensure userId is present
    const userId = this.authService.getCurrentUser()?.id;
    const body = { userId, bankAccountId: depositRequest.bankAccountId, amount: depositRequest.amount, reason: depositRequest.reason };
    return this.http.post<Transaction>(`${this.apiUrl}/deposit`, body, { headers: this.buildHeaders() });
  }

  withdraw(withdrawalRequest: any): Observable<Transaction> {
    const userId = this.authService.getCurrentUser()?.id;
    const body = { userId, bankAccountId: withdrawalRequest.bankAccountId, amount: withdrawalRequest.amount, reason: withdrawalRequest.reason };
    return this.http.post<Transaction>(`${this.apiUrl}/withdrawal`, body, { headers: this.buildHeaders() });
  }

  transfer(transferRequest: any): Observable<Transaction> {
    const userId = this.authService.getCurrentUser()?.id;
    const body = {
      userId,
      sourceAccountId: transferRequest.sourceBankAccountId || transferRequest.sourceAccountId || transferRequest.bankAccountId,
      targetAccountId: transferRequest.destinationBankAccountId || transferRequest.targetAccountId,
      amount: transferRequest.amount,
      reason: transferRequest.reason
    };
    return this.http.post<Transaction>(`${this.apiUrl}/transfer`, body, { headers: this.buildHeaders() });
  }

  getHistoryByAccount(bankAccountId: string): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/by-account/${bankAccountId}`, { headers: this.buildHeaders() });
  }

  getByReference(reference: string): Observable<Transaction> {
    return this.http.get<Transaction>(`${this.apiUrl}/by-reference/${reference}`, { headers: this.buildHeaders() });
  }

  search(params: any): Observable<Transaction[]> {
    let httpParams = new HttpParams();
    Object.keys(params).forEach(key => {
      if (params[key]) {
        httpParams = httpParams.append(key, params[key]);
      }
    });
    return this.http.get<Transaction[]>(`${this.apiUrl}/search`, { params: httpParams, headers: this.buildHeaders() });
  }
}
