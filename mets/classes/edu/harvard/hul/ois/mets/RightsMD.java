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
 * This class encapsulates the &lt;<tt>rightsMD</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/03
 */
public class RightsMD
    extends MdSec
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>rightsMD</tt> object.
     */
    public RightsMD ()
    {
	super ("rightsMD");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>RightsMD</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return RightsMD object
     * @throws MetsException De-serializing exception
     */
    public static RightsMD reader (MetsReader r)
	throws MetsException
    {
	RightsMD rightsMD = new RightsMD ();
	rightsMD.read (r);

	return rightsMD;
    }
}
