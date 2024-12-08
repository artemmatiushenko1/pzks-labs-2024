import { ProcessingUnitState } from '@/lib/types';
import { useMutation } from '@tanstack/react-query';

const useEvaluateExpression = () => {
  return useMutation({
    mutationFn: async (expression: string) => {
      const response = await fetch('/api/evaluate', {
        method: 'POST',
        body: JSON.stringify({ expression }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.json() as Promise<{
        entries: {
          time: number;
          processingUnitId: string;
          taskId: string;
          state: ProcessingUnitState;
        }[];
      }>;
    },
  });
};

export { useEvaluateExpression };
