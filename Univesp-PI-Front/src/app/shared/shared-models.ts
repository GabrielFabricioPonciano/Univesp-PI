export interface Promotion {
  id: number;
  description: string;
  discountPercentage: number;
  startDate: Date;     // Mantido como Date para uso interno
  endDate: Date;       // Mantido como Date para uso interno
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
}

// Interface para criação/atualização
export interface PromotionRequest {
  description: string;
  discountPercentage: number;
  startDate: string;  // String ISO para envio à API
  endDate: string;    // String ISO para envio à API
  status: 'ACTIVE' | 'INACTIVE' | 'EXPIRED';
}

export interface Product {
  productId: number;
  productName: string;
  productType: string;
  quantity: number;
  batchNumber: string;
  description: string;
  expirationDate?: string | Date;
  profitMargin: number;
  batchPrice: number;
  unitPrice: number;
  finalPrice: number;
  createdAt?: Date;
  updatedAt?: Date;
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED';
  promotionId?: number;
  showActions?: boolean;
}

export interface ProductUpdate {
  productName: string;
  productType: string;
  quantity: number;
  batchNumber: string;
  description: string;
  expirationDate?: string;
  profitMargin: number;
  batchPrice: number;
  status: 'ACTIVE' | 'INACTIVE' | 'DISCONTINUED';
  promotionId?: number | null;
}
