import { CheckCircledIcon } from '@radix-ui/react-icons';
import { Alert, AlertDescription, AlertTitle } from '../ui/alert';

const CompilationSuccessAlert = () => {
  return (
    <Alert variant="success">
      <CheckCircledIcon />
      <div>
        <AlertTitle>Compilation success!</AlertTitle>
        <AlertDescription>Expression is valid.</AlertDescription>
      </div>
    </Alert>
  );
};

export { CompilationSuccessAlert };
