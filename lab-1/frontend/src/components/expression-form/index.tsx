import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { GearIcon } from '../icons/gear-icon';

type ExpressionFormProps = {
  isLoading: boolean;
  onSubmit: (expression: string) => void;
};

const ExpressionForm = (props: ExpressionFormProps) => {
  const { onSubmit, isLoading } = props;

  const [expression, setExpression] = useState('');

  const handleFormSubmit: React.FormEventHandler<HTMLFormElement> = (e) => {
    e.preventDefault();

    if (!expression.trim()) return;

    onSubmit(expression);
  };

  return (
    <form className="flex flex-col gap-3 w-full" onSubmit={handleFormSubmit}>
      <Input
        name="expression"
        value={expression}
        onChange={(e) => setExpression(e.target.value)}
        placeholder="Enter your expression here (e.g 2+2*(1/4.56)-a)"
      />
      <Button className="flex gap-3" disabled={isLoading}>
        {isLoading ? (
          <span>Compiling...</span>
        ) : (
          <>
            <GearIcon />
            <span>Compile</span>
          </>
        )}
      </Button>
    </form>
  );
};

export { ExpressionForm };
