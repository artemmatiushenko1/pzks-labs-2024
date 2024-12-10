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

type HistoryEntry = {
  time: number;
  processingUnitId: string;
  taskId: string;
  state: ProcessingUnitState;
};

type SystemSpecs = {
  efficiency: number;
  acceleration: number;
  sequentialProcessingTime: number;
  parallelProcessingTime: number;
  processingUnitsCount: number;
  parallelProcessingHistory: HistoryEntry[];
};

export {
  type CompilationError,
  type TreeNode,
  ProcessingUnitState,
  type HistoryEntry,
  type SystemSpecs,
};
