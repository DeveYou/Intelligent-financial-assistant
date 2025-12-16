import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BankAccount } from '../models/bank-account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private readonly API_URL = 'http://localhost:8080/account-service/api/accounts';

  constructor(private http: HttpClient) { }

  getAccountsByUserId(userId: number): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.API_URL}/user/${userId}`);
  }

  getAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.API_URL}`);
  }

  getAccountCount(): Observable<number> {
    return this.http.get<number>(`${this.API_URL}/count`);
  }

  createAccount(account: any): Observable<BankAccount> {
    return this.http.post<BankAccount>(this.API_URL, account);
  }

  updateAccount(id: number, account: Partial<BankAccount>): Observable<BankAccount> {
    return this.http.patch<BankAccount>(`${this.API_URL}/${id}`, account);
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  getAccountByIban(iban: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.API_URL}/iban/${iban}`);
  }
}
