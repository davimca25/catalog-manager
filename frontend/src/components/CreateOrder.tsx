import { useState } from 'react';
import { createOrder } from '../services/orderService';
import { type OrderItemRequest } from '../types/order';
import { PlusCircle, ShoppingBag, Trash2 } from 'lucide-react';

interface Props {
    onOrderCreated?: () => void;
}

export function CreateOrder({ onOrderCreated }: Props) {
    const [items, setItems] = useState<OrderItemRequest[]>([]);
    const [productId, setProductId] = useState<string>("");
    const [quantity, setQuantity] = useState<number>(0);
    const [price, setPrice] = useState<number>(0);
    
    const [submitting, setSubmitting] = useState(false);
    const [message, setMessage] = useState<string | null>(null);

    const handleAddItem = (e: React.SubmitEvent) => {
        e.preventDefault();
        if (!productId || quantity <= 0 || price <= 0) return;

        setItems([...items, { productId, quantity, price }]);

        setProductId("");
        setQuantity(0);
        setPrice(0);
    };

    const handleRemoveItem = (index: number) => {
        setItems(items.filter((_, i) => i !== index));
    }

    const handleSubmitOrder = async () => {
        if (items.length === 0) return;

        try {
            setSubmitting(true);
            setMessage(null);
            await createOrder({ items });

            setMessage
        }
    }
} 