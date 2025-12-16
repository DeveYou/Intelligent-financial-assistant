export interface Transaction {
  id?: number;
  userId?: number;
  bankAccountId: number | string;
  reference?: string;
  type?: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER';
  status?: 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  amount: number;
  recipientId?: number | null;
  recipientIban?: string;
  reason?: string;
  date?: string; // ISO date string from backend
}

// Interface pour la réponse paginée
export interface TransactionPageResponse {
  content: Transaction[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}