package graph.domain;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import models.ThingNotFoundException;
import graph.blueprints.JellyGraph;
import graph.blueprints.SampleJellyGraphFactory;
import graph.domain.annotations.MDIncidenceMethodHandler;
import graph.domain.classes.Copy;
import graph.domain.classes.File;
import graph.domain.classes.Thing;
import graph.domain.classes.Work;
import graph.domain.incidences.Relates;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.AbstractModule;
import com.tinkerpop.frames.modules.gremlingroovy.GremlinGroovyModule;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;

/**
 * Repository provides functions to create
 * and/or locate a JellyTx.
 */
public class Repository {
    public static Long currentObjId = 999L;
    private static FramedGraphFactory factory = null;
    private static FramedGraph<Graph> framedGraph = null;
    private static JellyGraph jellyGraph = null;
    
    private static void loadGraph() {
        // TODO: interface with the blueprint implementation of JellyGraph
        factory = new FramedGraphFactory(new AbstractModule() {
            public void doConfigure(FramedGraphConfiguration config) {
                config.addMethodHandler(new MDIncidenceMethodHandler());
              }
            },
                                                            new JavaHandlerModule(), 
                                                            new GremlinGroovyModule());
        jellyGraph = new SampleJellyGraphFactory().createJellyGraph();
        framedGraph = factory.create((Graph) jellyGraph.graph());
        // framedGraph.getConfig().addMethodHandler(new MDIncidenceMethodHandler());
    }
    
    public static FramedGraphFactory getFactory() {
        if (factory == null)
            loadGraph();
        return factory;
    }
    
    public static FramedGraph<Graph> getGraph() {
        if (framedGraph == null) 
            loadGraph();
        return framedGraph;
    }
    
    public synchronized static Long nextObjId() {
        return currentObjId++;
    }
    
    protected static Thing validate(Vertex v) throws IllegalArgumentException {
        // TODO
        return null;
    }
    
    protected static Thing createThing(String collectionArea, String pi, String tType, String subType, JsonNode description, String link) {
        Long objId = nextObjId();
        framedGraph.addVertex(objId, Thing.class);
        Thing t = framedGraph.getVertex(objId, Thing.class);

        // TODO: t.update not working, have to individually set properties instead
        // t.update(collectionArea, pi, tType, subType, description.toString(), link);
        
        // setting properties of thing.
        t.setCollectionArea(collectionArea);
        t.setLink(link);
        t.setPi(pi);
        t.setTType(tType);
        t.setSubType(subType);
        if (description != null)
            t.setDescription(description.toString());
      
        objId++;
        return t;
    }

    protected static Thing cloneThing(Thing ft) {
        String pi = java.util.UUID.randomUUID().toString();
        return createThing(ft.getCollectionArea(), pi, ft.getTType(), ft.getSubType(), null, ft.getLink());
    }
    
    protected static Thing findThingById(long id) {
        return framedGraph.getVertex(id, Thing.class);
    }

    protected static Thing findThingByPI(String pi) {
        Iterable<Thing> res = framedGraph.getVertices("pi", pi, Thing.class);
        if (res == null || !res.iterator().hasNext())
            throw new ThingNotFoundException(String.format("No thing is found for pi: %s", pi));

        return res.iterator().next();
    }
    
    public static Work findWorkById(long id) {
        return framedGraph.getVertex(id, Work.class);
    }

    public static Work findWorkByPI(String pi) {
        Iterable<Work> res = framedGraph.getVertices("pi", pi, Work.class);
        if (res == null || !res.iterator().hasNext())
            throw new ThingNotFoundException(String.format("No thing is found for pi: %s", pi));

        return res.iterator().next();
    }

    public synchronized static void delete(Set<Thing> things) {
        for (Thing t : things) {            
            Iterable<Relates> relates = t.getRelates();
            for (Relates relate : relates) {
                framedGraph.removeEdge(relate.asEdge());
            }
            framedGraph.removeVertex(t.asVertex());
        }
    }
    
    protected synchronized static Thing newVersionThing(List<Thing> fromThings) {
        // TODO:
        return null;
    }
    
    protected synchronized static Relates newVersionRelates(List<Relates> fromRelates) {
        // TODO:
        return null;
    }
}
