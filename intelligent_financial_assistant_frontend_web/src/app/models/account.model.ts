export interface BankAccount {
  id: string;
  userId: string;
  accountNumber: string;
  type: 'checking' | 'savings' | 'business';
  balance: number;
  currency: string;
  status: 'active' | 'frozen' | 'closed';
  openedDate: Date;
  overdraftLimit?: number;
}

export interface AccountTransaction {
  id: string;
  accountId: string;
  type: 'deposit' | 'withdrawal' | 'transfer';
  amount: number;
  description: string;
  date: Date;
  balanceAfter: number;
  status: 'completed' | 'pending' | 'failed';
}
