import { CompilationError } from '@/lib/types';
import { ErrorIndicator } from '../error-indicator';
import { Alert, AlertDescription, AlertTitle } from '../ui/alert';
import { Badge } from '../ui/badge';

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
        className="gap-2 flex flex-col relative"
      >
        <div>
          <AlertTitle>{item.type}</AlertTitle>
          <AlertDescription>{item.message} </AlertDescription>
          {item.position !== null && (
            <Badge className="absolute top-2 right-2" variant="secondary">
              Position: {item.position}
            </Badge>
          )}
        </div>
        <ErrorIndicator expression={expression} errorAtIndex={item.position} />
      </Alert>
    );
  });
};

export { ErrorsList };
