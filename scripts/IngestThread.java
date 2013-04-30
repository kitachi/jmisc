package controllers.helpers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import play.Play;
import models.IngestParams;
import models.ingest.JellyGraph;

public class IngestThread extends Thread {
    private static BlockingQueue<IngestParams> ingestReqs;
    private static final IngestParams SHUTDOWN_REQ = new IngestParams();
    private static final IngestThread _thread = new IngestThread();
    private static boolean threadTerminated = false;
    private static int count = 0;
    
    public static IngestThread getInstance() {
        return _thread;
    }
    
    private IngestThread() {
        init();
        start();
    }
    
    private synchronized void init() {
        int maxQsize = Play.application().configuration().getInt("ingestQSize");
        ingestReqs = new LinkedBlockingQueue<IngestParams>(maxQsize);
    }
    
    public void run() {
        JellyGraph md = new JellyGraph();
        Long jobId = 90L;
        String ts = IngestUnion.getTimestamp();
        
        while (!(threadTerminated)) {
            try {
                IngestParams req;
                
                while ((req = ingestReqs.take()) != SHUTDOWN_REQ) {
                    count--;
                    md.logIngest(jobId, ts, "nla.test-ingest-thread");
                    jobId++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                threadTerminated = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized void shutdown() throws InterruptedException {
        threadTerminated = true;
        ingestReqs.put(SHUTDOWN_REQ);
    }
    
    public synchronized int queue(IngestParams req) throws InterruptedException {
        ingestReqs.put(req);
        count++;
        return count;
    }
    
    public synchronized int getReqsCount() {
        return count;
    }
}
