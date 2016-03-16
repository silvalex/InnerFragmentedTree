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

//	@Override
//	public String toString() {
//		return String.format("ServiceNode(%s)", service.getName());
//	}
	
   @Override
    public String toString() {
        String serviceName;
        if (service == null)
            serviceName = "null";
        else
            serviceName = service.name;
        return String.format("%d [label=\"Service(%s)\"]; ", hashCode(), serviceName);
    }

}
