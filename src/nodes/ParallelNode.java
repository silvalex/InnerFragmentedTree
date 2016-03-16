package nodes;

import java.util.ArrayList;
import java.util.List;

public class ParallelNode implements Node {
	private List<Node> children;

	public ParallelNode() {
		children = new ArrayList<Node>();
	}

	@Override
	public List<Node> getChildren() {
		return children;
	}

	@Override
	public Node clone(){
		ParallelNode newRoot = new ParallelNode();
		for (Node child : children) {
			newRoot.children.add(child.clone());
		}
		return newRoot;
	}

//	@Override
//	public String toString(){
//		StringBuilder builder = new StringBuilder();
//		builder.append("ParallelNode(");
//		for (int i = 0; i < children.size(); i++){
//			builder.append(children.get(i).toString());
//			if (i != children.size()-1)
//				builder.append(", ");
//		}
//		builder.append(")");
//		return builder.toString();
//	}
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%d [label=\"Parallel\"]; ", hashCode()));
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                if (child != null) {
                    builder.append(String.format("%d -> %d [dir=back]; ", hashCode(), child.hashCode()));
                    builder.append(child.toString());
                }
            }
        }
        return builder.toString();
    }
}
