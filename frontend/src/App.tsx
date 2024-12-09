import { Badge } from './components/ui/badge';
import { ExpressionForm } from './components/expression-form';
import { ErrorsList } from './components/errors-list';
import { CrossCircledIcon } from '@radix-ui/react-icons';
import { useCompileExpression } from './queries/compile-expression.mutation';
import { CompilationSuccessAlert } from './components/compilation-success-alert';
import { TreeViewer } from './components/tree-viewer';
import { GanttChart } from './components/gantt-chart';
import { Button } from './components/ui/button';
import { useEvaluateExpression } from './queries/evaluate-expression.mutation';
import { GanttChartIcon } from 'lucide-react';

// TODO:
// 1. Display benchmark data.
// 2. Create protocol

const App = () => {
  const {
    mutate: compileExpression,
    isPending: isCompiling,
    data: compilationResult,
    variables: submittedExpression,
  } = useCompileExpression();

  const {
    mutate: evaluateExpression,
    isPending: isEvaluating,
    data: evaluationResult,
  } = useEvaluateExpression();

  const { errors: compilationErrors, optimizedTree } = compilationResult ?? {};

  const handleEvaluateExpression = () => {
    if (submittedExpression) {
      evaluateExpression(submittedExpression);
    }
  };

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
        {optimizedTree && <TreeViewer tree={optimizedTree} />}
      </div>
      <div className='className="w-[800px]'>
        {optimizedTree && (
          <div className="flex flex-col gap-2">
            <Button
              className="self-start"
              onClick={handleEvaluateExpression}
              disabled={isEvaluating}
            >
              <GanttChartIcon />
              Evaluate
            </Button>
            <GanttChart entries={evaluationResult?.entries ?? []} />
          </div>
        )}
      </div>
    </div>
  );
};

export default App;
