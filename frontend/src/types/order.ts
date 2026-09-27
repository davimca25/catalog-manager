export type OrderStatus = 'PENDING' | 'COMPLETED' | "FAILED"

export interface OrderItemRequest {
    productId: string;
    quantity: number;
    price: number;
}

export interface OrderRequest {
    items: OrderItemRequest[];
}

export interface OrderResponse {
    id: string;
    userName: string;
    status: OrderStatus;
    createdAt: string;
    items: OrderItemRequest[];
}