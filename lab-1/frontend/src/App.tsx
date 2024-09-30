import { Input } from './components/ui/input';
import { Button } from './components/ui/button';
import { GearIcon } from './components/icons/gear-icon';
import { useMutation } from '@tanstack/react-query';

const App = () => {
  const { mutate: compileExpression, isPending: isCompiling } = useMutation({
    mutationFn: async (expression: string) => {
      const response = await fetch('/api/compile', {
        method: 'POST',
        body: JSON.stringify({ expression }),
        headers: {
          'Content-Type': 'application/json',
        },
      });

      return response.json();
    },
  });

  const handleFormSubmit: React.FormEventHandler<HTMLFormElement> = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target as HTMLFormElement);
    const expression = formData.get('expression');

    console.log({ expression });
    if (expression) {
      compileExpression(expression as string);
    }
  };

  return (
    <div className="flex items-center justify-center h-dvh">
      <form
        className="flex flex-col w-[600px] gap-3"
        onSubmit={handleFormSubmit}
      >
        <Input
          name="expression"
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
    </div>
  );
};

export default App;
