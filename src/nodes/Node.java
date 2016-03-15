package nodes;

import java.util.List;

public interface Node{
	List<Node> getChildren();

	Node clone();
	String toString();
}
