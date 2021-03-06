package nodes;

import java.util.ArrayList;
import java.util.List;

public class SequenceNode implements Node {
	private List<Node> children;

	public SequenceNode() {
		children = new ArrayList<Node>();
	}

	@Override
	public List<Node> getChildren() {
		return children;
	}

	@Override
	public Node clone(){
		SequenceNode newRoot = new SequenceNode();
		for (Node child : children) {
			newRoot.children.add(child.clone());
		}
		return newRoot;
	}

//	@Override
//	public String toString(){
//		StringBuilder builder = new StringBuilder();
//		builder.append("SequenceNode(");
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
	        builder.append(String.format("%d [label=\"Sequence\"]; ", hashCode()));
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
