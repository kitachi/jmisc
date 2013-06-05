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
 * This class encapsulates the agent role enumeration.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public final class Role
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** List of enumeration values. */
    private static List _list = new ArrayList ();

    /** Creator role. */
    public static final Role CREATOR = new Role ("CREATOR");

    /** Editor role. */
    public static final Role EDITOR = new Role ("EDITOR");

    /** Archivist role. */
    public static final Role ARCHIVIST = new Role ("ARCHIVIST");

    /** Preservation role. */
    public static final Role PRESERVATION = new Role ("PRESERVATION");

    /** Disseminator role. */
    public static final Role DISSEMINATOR = new Role ("DISSEMINATOR");

    /** Custodian role. */
    public static final Role CUSTODIAN = new Role ("CUSTODIAN");

    /** Intellectual property owner role. */
    public static final Role IPOWNER = new Role ("IPOWNER");

    /** Other agent role. */
    public static final Role OTHER = new Role ("OTHER");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Role</tt> object.
     * @param value Role value
     */
    public Role (String value)
    {
	super (value);
	_list.add (this);
    }

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return the agent role by name.
     * @param name Role name
     * @throws MetsException Unknown role
     */
    public static Role parse (String name)
        throws MetsException
    {
	Iterator iter = _list.iterator ();
	while (iter.hasNext ()) {
	    Role role = (Role) iter.next ();
	    if (name.equals (role.toString ())) {
		return role;
	    }
	}

	throw new MetsException ("Unsupported role: " + name);
    }
}
