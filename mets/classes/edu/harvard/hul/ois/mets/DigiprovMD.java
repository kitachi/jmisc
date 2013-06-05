/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets;

import edu.harvard.hul.ois.mets.helper.*;
import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

/**
 * This class encapsulates the &lt;<tt>digiprovMD</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/03
 */
public class DigiprovMD
    extends MdSec
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>digiprovMD</tt> object.
     */
    public DigiprovMD ()
    {
	super ("digiprovMD");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>DigiprovMD</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return DigiprovMD object
     * @throws MetsException De-serializing exception
     */
    public static DigiprovMD reader (MetsReader r)
	throws MetsException
    {
	DigiprovMD digiprovMD = new DigiprovMD ();
	digiprovMD.read (r);

	return digiprovMD;
    }
}
