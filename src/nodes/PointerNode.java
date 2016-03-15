package nodes;

import java.util.List;

public class PointerNode implements Node{
	private String service;

	public PointerNode(String service){
		this.service = service;
	}

	@Override
	public Node clone(){
		return this;
	}

	@Override
	public String toString(){
		return String.format("PointerNode(%s)", service);
	}

	@Override
	public List<Node> getChildren() {
		return null;
	}

	public String getService(){
		return service;
	}
}
