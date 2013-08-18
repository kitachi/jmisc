package au.gov.nla.graph.frames;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;

import au.gov.nla.graph.blueprints.JellyGraph;
import static au.gov.nla.graph.blueprints.JellyGraphFactory.Relationship;

public class ThingFactory {
        // TODO: move the following to JellyGraphFactory
        // public enum Relationship {
        //   PARENT, CHILD, RELATED
        // }
	private static final FramedGraph<Graph> framedGraph;
	private static Long currentObjId = 1L;

        public synchronized static void loadGraph(String pi) {
           loadGraph(pi, 1); 
        }

        public synchronized static void loadGraph(String pi, int radius) {
           Relationship[] includes = { PARENT, CHILD };
           loadGraph(pi, radius, includes);
        }

        public synchronized static void loadGraph(String pi, int radius, Relationship[] includes) {
            // TODO: interface with the blueprint implementation of JellyGraph
            FramedGraphFactory factory = new FramedGraphFactory();
	    JellyGraph graph = JellyGraphFactory.createJellyGraph(pi, radius, includes);
            framedGraph = factory.create((Graph) graph.graph());
        }

        // Below is also handy for unit testing.
	public synchronized static Thing create(String collectionArea, String pi, JsonNode description, String link) {
	    framedGraph.addVertex(currentObjId, Thing.class);
	    Thing t = framedGraph.getVertex(currentObjId, Thing.class);
            t.fill(collectionArea, pi, description, link);
	    currentObjId++;
		
                /* no/longer/needed:
                 * replaced by t.fill(...)
		page.setCollectionArea("nla.tst");
		page.setDescription("a book page");
		page.setLink("325244");
		page.setPi("aaaaaa-aaaaa-aaaa");
		page.setSubType("page");
		page.setTType("work");
                */
		
	    return t;
	}

        public synchronized static Thing findById(long id) {
	   return framedGraph.getVertex(id, Thing.class);	
	}

        public synchronized static Thing findByPI(String pi) {
	   Iterable<Thing> res = frameVertices(gremlin().has('pi',pi));
           if (res == null || !res.hasNext()) throws ThingNotFound;
           if (res.size() > 1) throws TooManyThings;
           return res.next();
        }

        public synchronized static void delete(Set<Thing> things) {
           for (Thing thing : things) {
              framedGraph.removeVertex(thing);
           }
        }

}
