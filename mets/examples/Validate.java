/**********************************************************************
 * METS Java toolkit validate example
 * Copyright (c) 2005 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

import edu.harvard.hul.ois.mets.*;
import edu.harvard.hul.ois.mets.helper.*;
import java.io.*;

/**
 * Test procedural de-serializing and validation of a METS file using
 * the METS Java toolkit.  This application de-serializes an existing
 * METS file into an in-memory representation, and then validates it.
 *
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-23
 */
public class Validate
{
    /******************************************************************
     * APPLICATION MAIN ENTRY POINT.
     ******************************************************************/

    public static void main (String [] args)
    {
	if (args.length < 1) {
	    System.err.println ("usage: java Validate file [debug]");
	    System.exit (-1);
	}

	/* The presence of a second command line argument specifies that
	 * the application should be run in debug mode.
	 */
	boolean debug = false;
	if (args.length > 1) {
	    debug = true;
	}

	try {
	    FileInputStream in = new FileInputStream (args[0]);

	    Mets mets = Mets.reader (new MetsReader (in, debug));
	    in.close ();

	    mets.validate (new MetsValidator ());
	}
	catch (Exception e) {
	    e.printStackTrace ();	    
	}
    }
}
