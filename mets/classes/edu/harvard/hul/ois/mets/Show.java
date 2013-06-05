/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2006 by the President and Fellows of Harvard College
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
 * This class encapsulates the XLink show behavior enumeration.
 * See &lt;http://www.w3.org/TR/2001/REC-xlink-20010627/&gt;.
 * @author Stephen Abrams
 * @version 1.5.1 2006-04-04
 */
public final class Show
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     *
     * Note: all behavior string values MUST be lowercase as defined
     * in the XLink standard
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** New window. */
    public static final Show NEW = new Show ("new");

    /** Replace in window. */
    public static final Show REPLACE = new Show ("replace");

    /** Embed in display. */
    public static final Show EMBED = new Show ("embed");

    /** Other show behavior. */
    public static final Show OTHER = new Show ("other");

    /** Don't show. */
    public static final Show NONE = new Show ("none");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Show</tt> object.
     * @param value Show value
     */
    public Show (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return show behavior by name.
     * @param name Behavior name
     */
    public static Show parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Show show = (Show) iter.next ();
	    if (name.equals (show.toString ())) {
		return show;
	    }
	}

	throw new MetsException ("Unsupported show behavior: " + name);
    }
}
