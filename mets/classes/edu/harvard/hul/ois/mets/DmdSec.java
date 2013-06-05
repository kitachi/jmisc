/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>dmdSec</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-19
 */
public class DmdSec
    extends MdSec
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>dmdSec</tt> object.
     */
    public DmdSec ()
    {
	super ("dmdSec");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>DmdSec</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return DmdSec object
     * @throws MetsException De-serializing exception
     */
    public static DmdSec reader (MetsReader r)
	throws MetsException
    {
	DmdSec dmdSec = new DmdSec ();
	dmdSec.read (r);

	return dmdSec;
    }
}
