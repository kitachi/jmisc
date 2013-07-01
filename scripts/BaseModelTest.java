package models;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import play.test.FakeApplication;
import play.test.Helpers;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import utils.DbUtil;

import com.avaje.ebean.Ebean;
import com.avaje.ebeaninternal.server.core.DefaultServer;

public class BaseModelTest {
    public static FakeApplication app;

    @BeforeClass
    public static void setupApp() {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @Before
    public void resetDb() throws IOException, SQLException {
        Assume.assumeNotNull(Ebean.getServer(null));
        if (!((DefaultServer)Ebean.getServer(null)).getDatabasePlatform().getName().equals("h2")) {
            System.err.println("Woah there cowboy! Do you really want to run destructive tests against a live database??");
            System.err.println("Cowardly skipping this test.");
            Assume.assumeTrue(false);
            return;
        }

        // FIXME: get rid of this once we have actual SQL migrations 
        DbUtil db = new DbUtil();
        db.executeUpdate("CREATE TABLE IF NOT EXISTS `dlRelationship` (`id` int(11) NOT NULL AUTO_INCREMENT,  `thing1Id` int(11) NOT NULL,  `relationship` int(11) NOT NULL,  `thing2Id` int(11) NOT NULL,  `relOrder` int(11) DEFAULT NULL)");   
        db.executeUpdate("CREATE TABLE IF NOT EXISTS `dlThing` (`id` int(11) NOT NULL AUTO_INCREMENT, `collectionArea` varchar(20) NOT NULL, `type` varchar(20) NOT NULL, `subType` varchar(20) DEFAULT NULL, `description` blob,  `link` varchar(50) DEFAULT NULL,  `pi` varchar(100) DEFAULT NULL,  `oldId` varchar(100) DEFAULT NULL, `createdTS` timestamp NULL,  `createdBy` varchar(20) NOT NULL DEFAULT '',  `updatedTS` timestamp NULL,  `updatedBy` varchar(20) DEFAULT '');");
        db.executeUpdate("create sequence dlThing_seq start with 1000");
    }    

   @Test
   public void testCreateItem() {
//	running(app, new Runnable() {
//		@Override
//		public void run() {
			System.out.println("testCreateItem");
			Thing t = new Thing();
                        t.collectionArea = "testArea";
			t.pi = "adbfafds";
			t.tType = "work";
			t.subType = "page";
			t.save();
			Thing t1 = new Thing();
                        t1.collectionArea = "testArea";
			t1.pi = "efbfafds";
			t1.tType = "work";
			t1.subType = "page";
			t1.save();
			System.out.println("thing id is " + t.id);
			System.out.println("thing id is " + t1.id);
//		}
 //       });
   }
}
