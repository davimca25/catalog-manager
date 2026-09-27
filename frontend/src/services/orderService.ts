import { orderApi } from "./api";
import { type OrderResponse, type OrderRequest } from "../types/order";

export const createOrder = async (orderRequest: OrderRequest): Promise<OrderResponse> => {
    const response = await orderApi.post<OrderResponse>('/orders', orderRequest);
    return response.data
}

export const listUserOrders = async (): Promise<OrderResponse[]> => {
    const response = await orderApi.get<OrderResponse[]>('/orders');
    return response.data;
};
