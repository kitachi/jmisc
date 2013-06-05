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
 * This class encapsulates the location type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Loctype
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** URN. */
    public static final Loctype URN = new Loctype ("URN");

    /** URL. */
    public static final Loctype URL = new Loctype ("URL");

    /** PURL. */
    public static final Loctype PURL = new Loctype ("PURL");

    /** Handle. */
    public static final Loctype HANDLE = new Loctype ("HANDLE");

    /** DOI. */
    public static final Loctype DOI = new Loctype ("DOI");

    /** Other location type. */
    public static final Loctype OTHER = new Loctype ("OTHER");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Loctype</tt> object.
     * @param value Loctype value
     */
    public Loctype (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return Loctype by name.
     * @param name Loctype name
     */
    public static Loctype parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Loctype loctype = (Loctype) iter.next ();
	    if (name.equals (loctype.toString ())) {
		return loctype;
	    }
	}

	throw new MetsException ("Unsupported loctype name: " + name);
    }
}
