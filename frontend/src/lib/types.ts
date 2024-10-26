type CompilationError = {
  message: string;
  position: number;
  type: 'SyntaxError' | 'LexicalError';
};

export { type CompilationError };
