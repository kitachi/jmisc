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
import java.util.*;

/**
 * This class encapsulates the XLink actuate behavior enumeration.
 * See &lt;http://www.w3.org/TR/2001/REC-xlink-20010627/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Actuate
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     *
     * Note: all behavior string values MUST be upper/lowercase as
     * defined in the XLink standard
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Actuate on load. */
    public static final Actuate ONLOAD = new Actuate ("onLoad");

    /** Actuate on request. */
    public static final Actuate ONREQUEST = new Actuate ("onRequest");

    /** Other actuate behavior. */
    public static final Actuate OTHER = new Actuate ("other");

    /** Don't actuate. */
    public static final Actuate NONE = new Actuate ("none");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Actuate</tt> object.
     * @param Value Actuate value
     */
    public Actuate (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return actuate behavior by name.
     * @param name Actute name
     */
    public static Actuate parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Actuate actuate = (Actuate) iter.next ();
	    if (name.equals (actuate.toString ())) {
		return actuate;
	    }
	}

	throw new MetsException ("Unsupported actuate behavior: " + name);
    }
}
