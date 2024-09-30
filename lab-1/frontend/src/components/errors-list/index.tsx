import { CompilationError } from '@/lib/types';
import { ErrorIndicator } from '../error-indicator';
import { Alert, AlertDescription, AlertTitle } from '../ui/alert';

type ErrorsListProps = {
  expression: string;
  errors: CompilationError[];
};

const ErrorsList = (props: ErrorsListProps) => {
  const { errors, expression } = props;

  return errors?.map((item) => {
    return (
      <Alert
        variant="default"
        key={item.message}
        className="gap-2 flex flex-col"
      >
        <div>
          <AlertTitle>{item.type}</AlertTitle>
          <AlertDescription>
            {item.message}{' '}
            {item.position !== null && <>Position: {item.position}.</>}
          </AlertDescription>
        </div>
        <ErrorIndicator expression={expression} errorAtIndex={item.position} />
      </Alert>
    );
  });
};

export { ErrorsList };
