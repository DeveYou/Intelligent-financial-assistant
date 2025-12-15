export interface Transaction {
  id?: number;
  reference?: string;
  type?: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
  status?: string;
  amount: number;
  date?: string; // ISO date string from backend
  bankAccountId: string;
  recipientId?: number | null; // new field to reference a recipient
  reason?: string;
}
