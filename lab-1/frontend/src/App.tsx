import { Input } from './components/ui/input';
import { Button } from './components/ui/button';
import { GearIcon } from './components/icons/gear-icon';
import { useMutation } from '@tanstack/react-query';
import { Alert, AlertDescription, AlertTitle } from './components/ui/alert';
import { useState } from 'react';
import { Badge } from './components/ui/badge';

const ErrorIndicator = ({
  expression,
  errorAtIndex,
}: {
  expression: string;
  errorAtIndex: number;
}) => {
  return (
    <div className="font-mono text-sm">
      {expression.split('').map((token, index) => (
        <div className="relative inline-block">
          <span
            className={index === errorAtIndex ? 'font-bold text-red-500' : ''}
          >
            {token}
          </span>
          {errorAtIndex === index && (
            <span className="absolute inset-x-0 -bottom-5 text-xl text-red-500 before:content-['^']"></span>
          )}
        </div>
      ))}
    </div>
  );
};

const App = () => {
  const [expression, setExpression] = useState('');

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

      return response.json() as Promise<
        {
          message: string;
          position: number;
          type: 'SyntaxError' | 'LexicalError';
        }[]
      >;
    },
  });

  const handleFormSubmit: React.FormEventHandler<HTMLFormElement> = (e) => {
    e.preventDefault();

    if (expression) {
      compileExpression(expression);
    }
  };

  return (
    <div className="flex items-center justify-center mt-20 flex-col ">
      <div className="w-[600px] gap-3 flex flex-col">
        <form
          className="flex flex-col gap-3 w-full"
          onSubmit={handleFormSubmit}
        >
          <Input
            name="expression"
            value={expression}
            onChange={(e) => setExpression(e.target.value)}
            placeholder="Enter your expression here (e.g 2+2*(1/4.56)-a)"
          />
          <Button className="flex gap-3" disabled={isCompiling}>
            {isCompiling ? (
              <span>Compiling...</span>
            ) : (
              <>
                <GearIcon />
                <span>Compile</span>
              </>
            )}
          </Button>
        </form>
        {Boolean(compilationErrors?.length) && (
          <div>
            <Badge variant="destructive">
              Errors: {compilationErrors?.length}
            </Badge>
          </div>
        )}
        {compilationErrors?.map((item) => {
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
              <ErrorIndicator
                expression={submittedExpression}
                errorAtIndex={item.position}
              />
            </Alert>
          );
        })}
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
