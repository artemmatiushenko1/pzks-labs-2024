type CompilationError = {
  message: string;
  position: number;
  type: 'SyntaxError' | 'LexicalError' | 'Exception';
};

type TreeNode = {
  value: string | null;
  children: TreeNode[];
};

export { type CompilationError, type TreeNode };
