package wsc;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;
import nodes.Node;
import nodes.ParallelNode;
import nodes.PointerNode;
import nodes.SequenceNode;
import nodes.ServiceNode;

public class WSCSpecies extends Species {

	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return new Parameter("wscspecies");
	}

	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
	    WSCInitializer init = (WSCInitializer) state.initializer;
	    Map<String, Node> predecessorMap = new HashMap<String, Node>();
	    predecessorMap.put("start", new ServiceNode(init.startServ));

	    finishConstructingTree(init.endServ, init, predecessorMap);

	    return new WSCIndividual(predecessorMap);
	}

	public void finishConstructingTree(Service s, WSCInitializer init, Map<String, Node> predecessorMap) {
	    Queue<Service> queue = new LinkedList<Service>();
	    queue.offer(s);

	    while (!queue.isEmpty()) {
	    	Service current = queue.poll();

	    	if (!predecessorMap.containsKey(current.name)) {
	    		SequenceNode root = new SequenceNode();
	    		Node leftChild;
	    		ServiceNode rightChild = new ServiceNode(current);

		    	Set<Service> predecessors = findPredecessors(init, current);

		    	if (predecessors.size() == 1) {
		    		Service predecessor = predecessors.iterator().next();
		    		leftChild = new PointerNode(predecessor.name);
		    		queue.offer(predecessor);
		    	}
		    	else {
		    		leftChild = new ParallelNode();
		    		for (Service predecessor : predecessors) {
		    			Node grandchild = new PointerNode(predecessor.name);
		    			leftChild.getChildren().add(grandchild);
		    			queue.offer(predecessor);
		    		}
		    	}

		    	root.getChildren().add(leftChild);
		    	root.getChildren().add(rightChild);
		    	predecessorMap.put(current.name, root);
	    	}
	    }
	}


	public Set<Service> findPredecessors(WSCInitializer init, Service s) {
		Set<Service> predecessors = new HashSet<Service>();

		// Get only inputs that are not subsumed by the given composition inputs
		Set<String> inputsNotSatisfied = init.getInputsNotSubsumed(s.getInputs(), init.startServ.outputs);
		Set<String> inputsToSatisfy = new HashSet<String>(inputsNotSatisfied);

		if (inputsToSatisfy.size() < s.getInputs().size())
			predecessors.add(init.startServ);

		// Find services to satisfy all inputs
		for (String i : inputsNotSatisfied) {
			if (inputsToSatisfy.contains(i)) {
				List<Service> candidates = init.taxonomyMap.get(i).servicesWithOutput;
				Collections.shuffle(candidates, init.random);

				Service chosen = null;
				candLoop:
				for(Service cand : candidates) {
					if (init.relevant.contains(cand) && cand.layer < s.layer) {
						predecessors.add(cand);
						chosen = cand;
						break candLoop;
					}
				}

				inputsToSatisfy.remove(i);

				// Check if other outputs can also be fulfilled by the chosen candidate, and remove them also
				Set<String> subsumed = init.getInputsSubsumed(inputsToSatisfy, chosen.outputs);
				inputsToSatisfy.removeAll(subsumed);
			}
		}
		return predecessors;
	}
}