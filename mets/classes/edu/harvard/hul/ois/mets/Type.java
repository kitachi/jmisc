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
 * This class encapsulates the agent type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Type
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Individual agent. */
    public static final Type INDIVIDUAL = new Type ("INDIVIDUAL");

    /** Organization agent. */
    public static final Type ORGANIZATION = new Type ("ORGANIZATION");

    /** Other agent type. */
    public static final Type OTHER = new Type ("OTHER");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Type</tt> object.
     * @param value Type value
     */
    public Type (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return agent type by name.
     * @param name Name
     */
    public static Type parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Type type = (Type) iter.next ();
	    if (name.equals (type.toString ())) {
		return type;
	    }
	}

	throw new MetsException ("Unsupported type name: " + name);
    }
}

