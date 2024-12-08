import { Bar, BarConfig } from '@ant-design/charts';

type GanttChartProps = {
  data: BarConfig['data'];
};

const GanttChart = (props: GanttChartProps) => {
  const { data } = props;

  const config = {
    data,
    xField: 'processingUnitId',
    yField: ['startTime', 'endTime'],
    colorField: 'state',
  };

  return <Bar {...config} />;
};

export { GanttChart };
