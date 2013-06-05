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
 * This class encapsulates the area begin/end type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Betype
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Byte type. */
    public static final Betype BYTE = new Betype ("BYTE");

    /** IDREF type. */
    public static final Betype IDREF = new Betype ("IDREF");

    /** SMIL type. */
    public static final Betype SMIL = new Betype ("SMIL");

    /** MIDI type. */
    public static final Betype MIDI = new Betype ("MIDI");

    /** SMPTE-25 type. */
    public static final Betype SMPTE_25 = new Betype ("SMPTE-25");

    /** SMPTE-24 type. */
    public static final Betype SMPTE_24 = new Betype ("SMPTE-24");

    /** SMPTE-DF30 type. */
    public static final Betype SMPTE_DF30 = new Betype ("SMPTE-DF30");

    /** SMPTE-NDF30 type. */
    public static final Betype SMPTE_NDF30 = new Betype ("SMPTE-NDF30");

    /** SMPTE-DF29.97 type. */
    public static final Betype SMPTE_DF29_97 = new Betype ("SMPTE_DF29.97");

    /** SMPTE-NDF29.97 type. */
    public static final Betype SMPTE_NDF29_97 = new Betype ("SMPTE_NDF29.97");

    /** TIME type. */
    public static final Betype TIME = new Betype ("TIME");

    /** TCF type. */
    public static final Betype TCF = new Betype ("TCF");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Betype</tt> object.
     * @param value Shape value
     */
    public Betype (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the being/end type by name.
     * @param name begin/end type name
     * @throws MetsException Unknown extent
     */
    public static Betype parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Betype betype = (Betype) iter.next ();
	    if (name.equals (betype.toString ())) {
		return betype;
	    }
	}

	throw new MetsException ("Unsupported begin/end type: " + name);
    }
}
