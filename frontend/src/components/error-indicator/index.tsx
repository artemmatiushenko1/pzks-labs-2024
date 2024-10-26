type ErrorIndicatorProps = {
  expression: string;
  errorAtIndex: number;
};

const ErrorIndicator = (props: ErrorIndicatorProps) => {
  const { expression, errorAtIndex } = props;

  return (
    <div className="font-mono text-sm bg-secondary px-2 border-l-2 rounded-r-md flex items-center text-nowrap overflow-x-auto overflow-y-hidden">
      <div className="h-[45px] flex items-center">
        {expression.split('').map((token, index) => (
          <span className="relative inline-block">
            <span
              className={
                index === errorAtIndex
                  ? "font-bold text-red-500 before:content-['^'] before:-bottom-4 before:absolute before:text-md"
                  : ''
              }
            >
              {token}
            </span>
          </span>
        ))}
      </div>
    </div>
  );
};

export { ErrorIndicator };
