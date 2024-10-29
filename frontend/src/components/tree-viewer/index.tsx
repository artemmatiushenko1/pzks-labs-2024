import { TreeNode } from '@/lib/types';
import Tree, { RenderCustomNodeElementFn } from 'react-d3-tree';
import { convertTreeToReactD3TreeFormat } from './helpers';

type TreeViewerProps = {
  tree: TreeNode;
};

const TreeViewer = (props: TreeViewerProps) => {
  const { tree } = props;

  const renderCustomNode: RenderCustomNodeElementFn = ({
    nodeDatum,
    toggleNode,
    ...rest
  }) => {
    console.log({ nodeDatum, toggleNode, rest });

    return (
      <g>
        <circle
          r={15}
          className="fill-green-600 stroke-none"
          onClick={toggleNode}
        />
        <text
          fill="white"
          stroke="white"
          strokeWidth="1"
          dy=".35em"
          textAnchor="middle"
        >
          {nodeDatum.name}
        </text>
      </g>
    );
  };

  return (
    <div style={{ width: '500px', height: '500px' }}>
      <Tree
        depthFactor={80}
        collapsible={false}
        orientation="vertical"
        rootNodeClassName="node-root"
        translate={{ x: 250, y: 100 }}
        renderCustomNodeElement={renderCustomNode}
        separation={{ siblings: 0.8, nonSiblings: 1 }}
        data={convertTreeToReactD3TreeFormat(tree.children[0])}
      />
    </div>
  );
};

export { TreeViewer };
