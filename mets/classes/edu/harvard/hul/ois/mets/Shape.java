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
 * This class encapsulates the area shape enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Shape
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Rectangle shape. */
    public static final Shape RECT = new Shape ("RECT");

    /** Circle shape. */
    public static final Shape CIRCLE = new Shape ("CIRCLE");

    /** Polygon shape. */
    public static final Shape POLY = new Shape ("POLY");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Shape</tt> object.
     * @param value Shape value
     */
    public Shape (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the area shape by name.
     * @param name Shape name
     * @throws MetsException Unknown shape
     */
    public static Shape parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Shape shape = (Shape) iter.next ();
	    if (name.equals (shape.toString ())) {
		return shape;
	    }
	}

	throw new MetsException ("Unsupported shape: " + name);
    }
}
