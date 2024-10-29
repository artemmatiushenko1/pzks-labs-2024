import { CompilationError, TreeNode } from '@/lib/types';
import { useMutation } from '@tanstack/react-query';

const useCompileExpression = () => {
  return useMutation({
    mutationFn: async (expression: string) => {
      const response = await fetch('/api/compile', {
        method: 'POST',
        body: JSON.stringify({ expression }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.json() as Promise<{
        syntaxErrors: CompilationError[];
        tree: TreeNode;
      }>;
    },
  });
};

export { useCompileExpression };
