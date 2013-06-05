/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2004-2005 by the President and Fellows of Harvard College
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
 * This class encapsulates the checksum type enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Checksumtype
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** HAVAL. */
    public static final Checksumtype HAVAL = new Checksumtype ("HAVAL");

    /** MD5. */
    public static final Checksumtype MD5 = new Checksumtype ("MD5");

    /** SHA-1. */
    public static final Checksumtype SHA1 = new Checksumtype ("SHA-1");

    /** SHA-256. */
    public static final Checksumtype SHA256 = new Checksumtype ("SHA-256");

    /** SHA-384. */
    public static final Checksumtype SHA384 = new Checksumtype ("SHA-384");

    /** SHA-512. */
    public static final Checksumtype SHA512 = new Checksumtype ("SHA-512");

    /** TIGER. */
    public static final Checksumtype TIGER = new Checksumtype ("TIGER");

    /** WHIRLPOOL header. */
    public static final Checksumtype WHIRLPOOL = new Checksumtype ("WHIRLPOOL");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Checksumtype</tt> object.
     * @param value Checksum type value
     */
    public Checksumtype (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the checksum type by name.
     * @param name Checksum type name
     */
    public static Checksumtype parse (String name)
	throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Checksumtype type = (Checksumtype) iter.next ();
	    if (name.equals (type.toString ())) {
		return type;
	    }
	}

	throw new MetsException ("Unsupported checksumtype name: " + name);
    }
}
