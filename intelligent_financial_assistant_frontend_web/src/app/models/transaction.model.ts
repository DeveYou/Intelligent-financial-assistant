export interface Transaction {
  id: string;
  amount: number;
  date: string;
  description: string;
  type: 'DEBIT' | 'CREDIT';
  accountId: string;
}

