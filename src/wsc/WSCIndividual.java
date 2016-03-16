package wsc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nodes.Node;
import ec.Individual;
import ec.simple.SimpleFitness;
import ec.util.Parameter;

public class WSCIndividual extends Individual {

	private static final long serialVersionUID = 1L;
	private Map<String, Node> predecessorMap;

	public WSCIndividual(){
		super();
		super.fitness = new SimpleFitness();
		super.species = new WSCSpecies();
	}

	public WSCIndividual(Map<String, Node> map) {
		super();
		super.fitness = new SimpleFitness();
		super.species = new WSCSpecies();
		predecessorMap = map;
	}

	@Override
	public Parameter defaultBase() {
		return new Parameter("wscindividual");
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof WSCIndividual) {
			return toString().equals(other.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public Map<String, Node> getPredecessorMap() {
		return predecessorMap;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("digraph g {");
		for (Entry<String, Node> e : predecessorMap.entrySet()) {
			builder.append(e.getValue().toString());
		}
		builder.append("}");
		return builder.toString();
	}

	@Override
	public WSCIndividual clone() {
		Map<String, Node> newMap = new HashMap<String, Node>();
		for (Entry<String, Node> e : predecessorMap.entrySet()) {
			String key = e.getKey();
			Node value = e.getValue().clone();
			newMap.put(key, value);
		}
		WSCIndividual wsci = new WSCIndividual(newMap);
		wsci.fitness = (SimpleFitness) fitness.clone();
		wsci.species = species;
		return wsci;
	}
}
