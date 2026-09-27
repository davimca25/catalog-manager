import { useEffect, useState } from "react";
import { listUserOrders } from "../services/orderService";
import { type OrderResponse } from "../types/order"; 
import { OrderStatusBadge } from "./OrderStatusBadge";
import { Package } from 'lucide-react';

export function OrderList() {
    const [orders, setOrders] = useState<OrderResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        listUserOrders()
            .then(setOrders)
            .catch(() => setError('Não foi possível carregar seus pedidos.'))
            .finally(() => setLoading(false));
    }, [])


    if (loading) {
        return <div className="text-center py-10 text-gray-500">Carregando seus pedidos...</div>;
    }

    if (error) {
        return <div className="text-center py-10 text-red-500">{error}</div>;
    }

 return (
    <div className="max-w-4xl mx-auto p-4 space-y-4">
      <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
        <Package className="w-6 h-6 text-blue-600" /> Meus Pedidos
      </h2>

      {orders.length === 0 ? (
        <div className="text-center py-8 bg-white rounded-lg border text-gray-500">Nenhum pedido encontrado.</div>
      ) : (
        orders.map((order) => (
          <div key={order.id} className="bg-white border rounded-xl p-5 shadow-sm space-y-3">
            <div className="flex items-center justify-between border-b pb-3">
              <div>
                <p className="text-xs text-gray-400 font-mono">ID: {order.id}</p>
                <p className="text-sm font-medium text-gray-600">Cliente: {order.userName}</p>
              </div>
              <OrderStatusBadge status={order.status} />
            </div>

            <div className="space-y-2">
              {order.items.map((item, index) => (
                <div key={index} className="flex justify-between text-sm py-1 border-b border-dashed last:border-0">
                  <span>Produto #{item.productId} (x{item.quantity})</span>
                  <span className="font-semibold">R$ {(item.price * item.quantity).toFixed(2)}</span>
                </div>
              ))}
            </div>
          </div>
        ))
      )}
    </div>
  );
}