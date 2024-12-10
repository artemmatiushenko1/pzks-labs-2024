import { Badge } from './components/ui/badge';
import { ExpressionForm } from './components/expression-form';
import { ErrorsList } from './components/errors-list';
import { CrossCircledIcon } from '@radix-ui/react-icons';
import { useCompileExpression } from './queries/compile-expression.mutation';
import { CompilationSuccessAlert } from './components/compilation-success-alert';
import { TreeViewer } from './components/tree-viewer';
import { GanttChart } from './components/gantt-chart';
import { useEvaluateExpression } from './queries/evaluate-expression.mutation';
import {
  GanttChartIcon,
  MonitorCog,
  Network,
  SquareRadical,
} from 'lucide-react';

// TODO:
// 1. Display benchmark data.
// 2. Create protocol

const App = () => {
  const {
    mutateAsync: compileExpression,
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

  const handleEvaluateExpression = (expression: string) => {
    evaluateExpression(expression);
  };

  const handleCompileExpression = (expression: string) => {
    compileExpression(expression).then(() =>
      handleEvaluateExpression(expression)
    );
  };

  return (
    <div className="flex flex-col h-dvh">
      <div className="flex">
        <div className="w-[800px] gap-3 flex flex-col border-r-2 p-5">
          <div className="flex gap-2 mb-4">
            <SquareRadical />
            <h2 className="font-bold">Expression Form</h2>
          </div>
          <ExpressionForm
            onSubmit={handleCompileExpression}
            isLoading={isCompiling || isEvaluating}
          />
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
        <div className="w-full h-[444px]">
          <div className="flex gap-2 px-5 pt-5">
            <Network />
            <h2 className="font-bold">Expression Tree</h2>
          </div>
          {optimizedTree && <TreeViewer tree={optimizedTree} />}
        </div>
      </div>
      {
        <div className="border-t-2 border-r-2 flex flex-1 h-0">
          <div className="border-r-2 pt-5 pb-6 w-[1220px]">
            <div className="flex flex-col gap-2 px-5">
              <div className="flex gap-2 mb-4 ">
                <GanttChartIcon />
                <h2 className="font-bold">Gantt Diagram</h2>
              </div>
              {evaluationResult && (
                <GanttChart entries={evaluationResult?.entries ?? []} />
              )}
            </div>
          </div>
          <div className="px-5 py-5">
            <div className="flex gap-2 mb-4">
              <MonitorCog />
              <h2 className="font-bold">System Specs</h2>
            </div>
            <ul>
              <li>
                <span>Type: </span> Vector System
              </li>
              <li>
                <span>Processing units count: </span>{' '}
                {evaluationResult?.specs.processingUnitsCount}
              </li>
              <li>
                <span>Parallel processing time: </span>{' '}
                {evaluationResult?.specs.parallelProcessingTime}
              </li>
              <li>
                <span>Sequential processing time: </span>{' '}
                {evaluationResult?.specs.sequentialProcessingTime}
              </li>
              <li>
                <span>Acceleration: </span>{' '}
                {evaluationResult?.specs.acceleration.toFixed(3)}
              </li>
              <li>
                <span>Efficiency: </span>{' '}
                {evaluationResult?.specs.efficiency.toFixed(3)}
              </li>
            </ul>
          </div>
        </div>
      }
    </div>
  );
};

export default App;
