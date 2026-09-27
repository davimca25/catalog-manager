import { Clock, CheckCircle2, XCircle } from 'lucide-react';

interface Props {
    status: string;
}
     
export function OrderStatusBadge({ status }: Props) {
  switch (status) {
    case 'COMPLETED':
      return (
        <span className="flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full bg-green-100 text-green-700">
          <CheckCircle2 className="w-3.5 h-3.5" /> Concluído
        </span>
      );
    case 'FAILED':
      return (
        <span className="flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full bg-red-100 text-red-700">
          <XCircle className="w-3.5 h-3.5" /> Falhou
        </span>
      );
    default:
      return (
        <span className="flex items-center gap-1 text-xs font-semibold px-2.5 py-1 rounded-full bg-yellow-100 text-yellow-700">
          <Clock className="w-3.5 h-3.5" /> Pendente
        </span>
      );
  }
}