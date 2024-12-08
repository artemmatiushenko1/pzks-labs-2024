type CompilationError = {
  message: string;
  position: number;
  type: 'SyntaxError' | 'LexicalError' | 'Exception';
};

type TreeNode = {
  value: string | null;
  children: TreeNode[];
};

enum ProcessingUnitState {
  IDLE = 'IDLE',
  READING = 'READING',
  PROCESSING = 'PROCESSING',
  WRITING = 'WRITING',
}

export { type CompilationError, type TreeNode, ProcessingUnitState };
