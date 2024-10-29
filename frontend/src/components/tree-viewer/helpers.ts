import { TreeNode } from '@/lib/types';
import { RawNodeDatum } from 'react-d3-tree';

const convertTreeToReactD3TreeFormat = (node: TreeNode): RawNodeDatum => {
  return {
    name: node.value ?? '',
    children: node.children.map(convertTreeToReactD3TreeFormat),
  };
};

export { convertTreeToReactD3TreeFormat };
