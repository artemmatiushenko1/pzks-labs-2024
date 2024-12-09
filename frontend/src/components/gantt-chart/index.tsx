import { HistoryEntry, ProcessingUnitState } from '@/lib/types';
import { Bar } from '@ant-design/charts';

type GanttChartProps = {
  entries: HistoryEntry[];
};

const STATE_TO_COLOR = {
  [ProcessingUnitState.IDLE]: '#ecf0f1',
  [ProcessingUnitState.READING]: '#f39c12',
  [ProcessingUnitState.PROCESSING]: '#27ae60',
  [ProcessingUnitState.WRITING]: '#3498db',
};

const GanttChart = (props: GanttChartProps) => {
  const { entries } = props;

  const transformedData = entries.map((item) => ({
    ...item,
    startTime: item.time - 1,
    endTime: item.time,
  }));

  const config = {
    tooltip: false,
    data: transformedData,
    xField: 'processingUnitId',
    yField: ['startTime', 'endTime'],
    minBarWidth: 200,
    label: {
      position: 'inside',
      text: (item: HistoryEntry) => `${item.taskId ?? ''}`,
    },
  };

  return (
    <Bar
      {...config}
      axis={{ y: { title: 'Time' }, x: { title: 'Processing Unit' } }}
      height={300}
      width={900}
      style={{
        width: 30,
        minHeight: 50,
        fill: ({ state }: HistoryEntry) => STATE_TO_COLOR[state],
      }}
    />
  );
};

export { GanttChart };
