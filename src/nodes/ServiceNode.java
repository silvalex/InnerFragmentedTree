package nodes;

import java.util.List;

import wsc.Service;

public class ServiceNode implements Node {
	private	Service service;

	public ServiceNode(Service s) {
		service = s;
	}

	public Service getService() {
		return service;
	}

	@Override
	public List<Node> getChildren() {
		return null;
	}

	@Override
	public Node clone() {
		return this;
	}

	@Override
	public String toString() {
		return String.format("ServiceNode(%s)", service.getName());
	}

}
