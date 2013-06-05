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

/**
 * This class encapsulates the &lt;<tt>mechanism</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/17
 */
public class Mechanism
    extends ObjectType
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>mechanism</tt>&gt; object.
     */
    public Mechanism ()
    {
	super ("mechanism");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate an <tt>Mechanism</tt> object de-serialized from the
     * input stream.
     * @param r Reader
     * @return BehaviourSec object
     * @throws MetsException De-serializing exception
     */
    public static Mechanism reader (MetsReader r)
	throws MetsException
    {
	Mechanism mechanism = new Mechanism ();
	mechanism.read (r);

	return mechanism;
    }
}
