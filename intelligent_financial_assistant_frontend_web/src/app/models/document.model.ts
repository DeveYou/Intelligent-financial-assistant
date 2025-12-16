export interface Document {
  id: string;
  userId: string;
  accountId?: string;
  type: 'statement' | 'id_card' | 'proof_of_address' | 'other';
  title: string;
  fileName: string;
  fileSize: number;
  uploadDate: Date;
  status: 'pending' | 'verified' | 'rejected';
}
