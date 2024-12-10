import { HistoryEntry, ProcessingUnitState } from '@/lib/types';
import { Bar } from '@ant-design/charts';
import { useLayoutEffect, useState } from 'react';

type GanttChartProps = {
  entries: HistoryEntry[];
};

const STATE_TO_COLOR = {
  [ProcessingUnitState.IDLE]: '#ecf0f1',
  [ProcessingUnitState.READING]: '#f39c12',
  [ProcessingUnitState.PROCESSING]: '#27ae60',
  [ProcessingUnitState.WRITING]: '#3498db',
};

const getColorByState = (state: ProcessingUnitState) => STATE_TO_COLOR[state];

const GanttChart = (props: GanttChartProps) => {
  const { entries } = props;

  const [width, setWidth] = useState(800);

  useLayoutEffect(() => {
    setWidth(window.innerWidth / 1.2 - 80);
  }, []);

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
    <>
      <div className="flex items-start w-full gap-3">
        {[
          ProcessingUnitState.IDLE,
          ProcessingUnitState.READING,
          ProcessingUnitState.PROCESSING,
          ProcessingUnitState.WRITING,
        ].map((state) => (
          <div key={state} className="flex items-center gap-1">
            <span
              style={{
                borderRadius: '20px',
                width: '20px',
                height: '20px',
                background: getColorByState(state),
              }}
            >
              &nbsp;
            </span>
            <span className="text-sm">{state.toLowerCase()}</span>
          </div>
        ))}
      </div>
      <Bar
        {...config}
        axis={{ y: { title: 'Time' }, x: { title: 'Processing Unit' } }}
        height={260}
        width={width}
        style={{
          fill: ({ state }: HistoryEntry) => STATE_TO_COLOR[state],
        }}
      />
    </>
  );
};

export { GanttChart };
