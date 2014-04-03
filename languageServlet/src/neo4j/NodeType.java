package neo4j;

import org.neo4j.graphdb.Label;

public enum NodeType implements Label{
	Model, Item, Noun, Verb, Adjective
}
