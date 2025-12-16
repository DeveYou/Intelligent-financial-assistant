export interface BankAccount {
  id: number;
  iban: string;
  balance: number;
  expirationDate: string;
  isActive: boolean;
  isPaymentByCard: boolean;
  isWithdrawal: boolean;
  isOnlinePayment: boolean;
  isContactless: boolean;
  createdAt: string;
  type: string;
  overDraft?: number;
  interestRate?: number;
  userId: number;
}
