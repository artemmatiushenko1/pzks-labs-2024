import { Alert, AlertDescription, AlertTitle } from './components/ui/alert';
import { Badge } from './components/ui/badge';
import { ExpressionForm } from './components/expression-form';
import { ErrorsList } from './components/errors-list';
import { CheckCircledIcon, CrossCircledIcon } from '@radix-ui/react-icons';
import { useCompileExpression } from './queries/compile-expression.mutation';

const App = () => {
  const {
    mutate: compileExpression,
    isPending: isCompiling,
    data: compilationErrors,
    variables: submittedExpression,
  } = useCompileExpression();

  const handleFormSubmit = (expression: string) => {
    compileExpression(expression);
  };

  return (
    <div className="flex items-center justify-center mt-20 flex-col ">
      <div className="w-[600px] gap-3 flex flex-col">
        <ExpressionForm onSubmit={handleFormSubmit} isLoading={isCompiling} />
        {Boolean(compilationErrors?.length) && (
          <Badge
            startIcon={<CrossCircledIcon />}
            variant="destructive"
            className="max-w-max"
          >
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
            <CheckCircledIcon />
            <div>
              <AlertTitle>Compilation success!</AlertTitle>
              <AlertDescription>Expression is valid.</AlertDescription>
            </div>
          </Alert>
        )}
      </div>
    </div>
  );
};

export default App;
