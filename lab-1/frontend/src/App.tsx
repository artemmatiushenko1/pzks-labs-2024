import { useMutation } from '@tanstack/react-query';
import { Alert, AlertDescription, AlertTitle } from './components/ui/alert';
import { Badge } from './components/ui/badge';
import { ExpressionForm } from './components/expression-form';
import { CompilationError } from './lib/types';
import { ErrorsList } from './components/errors-list';

const App = () => {
  const {
    mutate: compileExpression,
    isPending: isCompiling,
    data: compilationErrors,
    variables: submittedExpression,
  } = useMutation({
    mutationFn: async (expression: string) => {
      const response = await fetch('/api/compile', {
        method: 'POST',
        body: JSON.stringify({ expression }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.json() as Promise<CompilationError[]>;
    },
  });

  const handleFormSubmit = (expression: string) => {
    compileExpression(expression);
  };

  return (
    <div className="flex items-center justify-center mt-20 flex-col ">
      <div className="w-[600px] gap-3 flex flex-col">
        <ExpressionForm onSubmit={handleFormSubmit} isLoading={isCompiling} />
        {Boolean(compilationErrors?.length) && (
          <Badge variant="destructive" className="max-w-max">
            Errors: {compilationErrors?.length}
          </Badge>
        )}
        {compilationErrors && (
          <ErrorsList
            errors={compilationErrors}
            expression={submittedExpression}
          />
        )}
        {compilationErrors?.length === 0 && (
          <Alert variant="success">
            <AlertTitle>Compilation success!</AlertTitle>
            <AlertDescription>Expression is valid.</AlertDescription>
          </Alert>
        )}
      </div>
    </div>
  );
};

export default App;
