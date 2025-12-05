export enum TransactionType {
  DEPOSIT = 'DEPOSIT',
  WITHDRAWAL = 'WITHDRAWAL',
  TRANSFER = 'TRANSFER',
  PAYMENT = 'PAYMENT'
}

export enum TransactionStatus {
  PENDING = 'PENDING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  CANCELED = 'CANCELED'
}

export interface Transaction {
  id: string;
  amount: number;
  date: string; // ISO string
  description?: string;
  category?: string;
  reference: string;
  bankAccountId?: string;
  receiver?: string;
  type?: TransactionType;
  status?: TransactionStatus;
}
