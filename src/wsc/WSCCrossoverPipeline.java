package wsc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import nodes.Node;
import nodes.ParallelNode;
import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class WSCCrossoverPipeline extends BreedingPipeline {

	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return new Parameter("wsccrossoverpipeline");
	}

	@Override
	public int numSources() {
		return 2;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {

		WSCInitializer init = (WSCInitializer) state.initializer;

		Individual[] inds1 = new Individual[inds.length];
		Individual[] inds2 = new Individual[inds.length];

		int n1 = sources[0].produce(min, max, 0, subpopulation, inds1, state, thread);
		int n2 = sources[1].produce(min, max, 0, subpopulation, inds2, state, thread);

        if (!(sources[0] instanceof BreedingPipeline)) {
            for(int q=0;q<n1;q++)
                inds1[q] = (Individual)(inds1[q].clone());
        }

        if (!(sources[1] instanceof BreedingPipeline)) {
            for(int q=0;q<n2;q++)
                inds2[q] = (Individual)(inds2[q].clone());
        }

        if (!(inds1[0] instanceof WSCIndividual))
            // uh oh, wrong kind of individual
            state.output.fatal("WSCCrossoverPipeline didn't get a WSCIndividual. The offending individual is in subpopulation "
            + subpopulation + " and it's:" + inds1[0]);

        if (!(inds2[0] instanceof WSCIndividual))
            // uh oh, wrong kind of individual
            state.output.fatal("WSCCrossoverPipeline didn't get a WSCIndividual. The offending individual is in subpopulation "
            + subpopulation + " and it's:" + inds2[0]);

        int nMin = Math.min(n1, n2);

        // Perform crossover
        for(int q=start,x=0; q < nMin + start; q++,x++) {
    		WSCIndividual t1 = ((WSCIndividual)inds1[x]);
    		WSCIndividual t2 = ((WSCIndividual)inds2[x]);

    		// Get all fragment roots from both candidates
    		List<String> allT1Keys = new ArrayList<String>(t1.getPredecessorMap().keySet());
            List<String> allT2Keys = new ArrayList<String>(t2.getPredecessorMap().keySet());

            // Shuffle them so that the crossover is done randomly
            Collections.shuffle( allT1Keys, init.random );
            Collections.shuffle( allT2Keys, init.random );

            // Select the fragment root for crossover
            String selected = null;

            outerLoop:
            for (String s1 : allT1Keys) {
                if (!s1.equals("start")) {
                    for (String s2 : allT2Keys) {
                        if (s1.equals( s2 )) {
                            selected = s1;
                            break outerLoop;
                        }
                    }
                }
            }

            // Create replacement fragments
            Map<String, Node> t1Replacements = findRelevantReplacements(selected, t2, init);
            Map<String, Node> t2Replacements = findRelevantReplacements(selected, t1, init);

            // Add replacement fragments to original candidates
            addReplacementFragments(t1, t1Replacements, selected, init);
            addReplacementFragments(t2, t2Replacements, selected, init);

	        inds[q] = t1;
	        inds[q].evaluated=false;

	        if (q+1 < inds.length) {
	        	inds[q+1] = t2;
	        	inds[q+1].evaluated=false;
	        }
        }
        return n1;
	}

	private Map<String, Node> findRelevantReplacements(String selected, WSCIndividual t, WSCInitializer init) {
	    Map<String, Node> originalMap = t.getPredecessorMap();
	    Map<String, Node> replacementMap = new HashMap<String, Node>();

	    Queue<String> queue = new LinkedList<String>();
	    queue.offer( selected );
	    while(!queue.isEmpty()) {
	        String current = queue.poll();
	        if (!replacementMap.containsKey( current ) && !current.equals( "start" )) {
	            Node newValue = originalMap.get(current).clone();
	            replacementMap.put( current, newValue );

	            Node leftChild = newValue.getChildren().get(0);
	            if (leftChild instanceof ParallelNode) {
	            	for (Node grandchild : leftChild.getChildren()) {
	            		queue.offer(init.getName(grandchild));
	            	}
	            }
	            else {
	            	queue.offer(init.getName(leftChild));
	            }
	        }
	    }
	    return replacementMap;
	}

	private void addReplacementFragments(WSCIndividual t, Map<String, Node> replacements, String selected, WSCInitializer init) {
	    Map<String, Node> originalMap = t.getPredecessorMap();

	    // Add the main fragment
	    Node newValue = replacements.get( selected );
	    originalMap.put( selected, newValue );

	    // Recurse down and add any additional fragments that are needed
	    Queue<String> queue = new LinkedList<String>();
	    Node leftChild = newValue.getChildren().get(0);

	    if (leftChild instanceof ParallelNode) {
        	for (Node grandchild : leftChild.getChildren()) {
        		queue.offer(init.getName(grandchild));
        	}
        }
        else {
        	queue.offer(init.getName(leftChild));
        }

	    while(!queue.isEmpty()) {
	        String current = queue.poll();
	        if (!originalMap.containsKey( current )) {
                Node v = replacements.get(current).clone();
                originalMap.put( current, v );

                leftChild = v.getChildren().get(0);

        	    if (leftChild instanceof ParallelNode) {
                	for (Node grandchild : leftChild.getChildren()) {
                		queue.offer(init.getName(grandchild));
                	}
                }
                else {
                	queue.offer(init.getName(leftChild));
                }
            }
	    }
	}
}
