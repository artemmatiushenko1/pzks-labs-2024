type ErrorIndicatorProps = {
  expression: string;
  errorAtIndex: number;
};

const ErrorIndicator = (props: ErrorIndicatorProps) => {
  const { expression, errorAtIndex } = props;

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

export { ErrorIndicator };
