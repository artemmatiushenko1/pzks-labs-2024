import { Badge } from './components/ui/badge';
import { ExpressionForm } from './components/expression-form';
import { ErrorsList } from './components/errors-list';
import { CrossCircledIcon } from '@radix-ui/react-icons';
import { useCompileExpression } from './queries/compile-expression.mutation';
import { CompilationSuccessAlert } from './components/compilation-success-alert';
import { TreeViewer } from './components/tree-viewer';

const App = () => {
  const {
    mutate: compileExpression,
    isPending: isCompiling,
    data: compilationResult,
    variables: submittedExpression,
  } = useCompileExpression();

  const {
    errors: compilationErrors,
    optimizedTree,
    optimizedExpressionString,
  } = compilationResult ?? {};

  return (
    <div className="flex items-center justify-center mt-20 flex-col ">
      <div className="w-[600px] gap-3 flex flex-col">
        <ExpressionForm onSubmit={compileExpression} isLoading={isCompiling} />
        {Boolean(compilationErrors?.length) && (
          <Badge
            startIcon={<CrossCircledIcon />}
            variant="destructive"
            className="max-w-max"
          >
            Errors: {compilationErrors?.length}
          </Badge>
        )}
        {compilationErrors && submittedExpression && (
          <ErrorsList
            errors={compilationErrors}
            expression={submittedExpression}
          />
        )}
        {compilationErrors?.length === 0 && <CompilationSuccessAlert />}
      </div>
      {optimizedExpressionString && (
        <div>
          <p className="text-start">
            Optimized expression: {optimizedExpressionString}
          </p>
        </div>
      )}
      {optimizedTree && <TreeViewer tree={optimizedTree} />}
    </div>
  );
};

export default App;
