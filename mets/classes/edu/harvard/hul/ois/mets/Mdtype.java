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
 * This class encapsulates the metadata type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.2 2006-07-03
 */
public final class Mdtype
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** MARC. */
    public static final Mdtype MARC = new Mdtype ("MARC");

    /** MODS. */
    public static final Mdtype MODS = new Mdtype ("MODS");

    /** EAD. */
    public static final Mdtype EAD = new Mdtype ("EAD");

    /** Dublin Core. */
    public static final Mdtype DC = new Mdtype ("DC");

    /** NISO Image. */
    public static final Mdtype NISOIMG = new Mdtype ("NISOIMG");

    /** Library of Congress A/V. */
    public static final Mdtype LC_AV = new Mdtype ("LC-AV");

    /** Visual Resource Association. */
    public static final Mdtype VRA = new Mdtype ("VRA");

    /** TEI header. */
    public static final Mdtype TEIHDR = new Mdtype ("TEIHDR");

    /** DDI. */
    public static final Mdtype DDI = new Mdtype ("DDI");

    /** FGDC. */
    public static final Mdtype FGDC = new Mdtype ("FGDC");

    /** LOM. */
    public static final Mdtype LOM = new Mdtype ("LOM");

    /** PREMIS. */
    public static final Mdtype PREMIS = new Mdtype ("PREMIS");

    /** Other metadata type. */
    public static final Mdtype OTHER = new Mdtype ("OTHER");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Mdtype</tt> object.
     * @param value Metadata type value
     */
    public Mdtype (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the metadata type by name.
     * @param name Metadata type name
     */
    public static Mdtype parse (String name)
	throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Mdtype mdtype = (Mdtype) iter.next ();
	    if (name.equals (mdtype.toString ())) {
		return mdtype;
	    }
	}

	throw new MetsException ("Unsupported mdtype name: " + name);
    }
}
