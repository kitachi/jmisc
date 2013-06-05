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
 * This class encapsulates the area extent type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Exttype
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Byte type. */
    public static final Exttype BYTE = new Exttype ("BYTE");

    /** SMIL type. */
    public static final Exttype SMIL = new Exttype ("SMIL");

    /** MIDI type. */
    public static final Exttype MIDI = new Exttype ("MIDI");

    /** SMPTE-25 type. */
    public static final Exttype SMPTE_25 = new Exttype ("SMPTE-25");

    /** SMPTE-24 type. */
    public static final Exttype SMPTE_24 = new Exttype ("SMPTE-24");

    /** SMPTE-DF30 type. */
    public static final Exttype SMPTE_DF30 = new Exttype ("SMPTE-DF30");

    /** SMPTE-NDF30 type. */
    public static final Exttype SMPTE_NDF30 = new Exttype ("SMPTE-NDF30");

    /** SMPTE-DF29.97 type. */
    public static final Exttype SMPTE_DF29_97 = new Exttype ("SMPTE_DF29.97");

    /** SMPTE-NDF29.97 type. */
    public static final Exttype SMPTE_NDF29_97 = new Exttype ("SMPTE_NDF29.97");

    /** TIME type. */
    public static final Exttype TIME = new Exttype ("TIME");

    /** TCF type. */
    public static final Exttype TCF = new Exttype ("TCF");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Exttype</tt> object.
     * @param value Shape value
     */
    public Exttype (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the extent type by name.
     * @param name Extent name
     * @throws MetsException Unknown extent
     */
    public static Exttype parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Exttype exttype = (Exttype) iter.next ();
	    if (name.equals (exttype.toString ())) {
		return exttype;
	    }
	}

	throw new MetsException ("Unsupported extent: " + name);
    }
}
