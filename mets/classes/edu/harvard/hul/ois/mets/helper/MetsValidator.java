/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Validator
 * @author Stephen Abrams
 * @version 1.1 2002/Dec/23
 */
public class MetsValidator
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDs. */
    private Map _ID;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public MetsValidator ()
    {
	_ID = new Hashtable ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     *******************************************************************/

    /**
     * Return map of IDs.
     * @return Map
     */
    public Map getIDs ()
    {
	return _ID;
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate an element and its content model.
     * @param element Element
     * @throws MetsException Validation exception
     */
    public void validate (MetsVElement element)
	throws MetsException
    {
	if (element.isValid ()) {
	    return;
	}

	if (element instanceof MetsIdentifiable) {
	    String ID = ((MetsIdentifiable) element).getID ();
	    if (ID != null) {
		if (_ID.containsKey (ID)) {
		    throw new MetsException ("ID \"" + ID +
					     "\" already exists");
		}
		_ID.put (ID, element);
	    }
	}
	Iterator iter = element.getContent ().iterator ();
	while (iter.hasNext ()) {
	    Object obj = iter.next ();
	    if (obj instanceof MetsValidatable) {
		((MetsValidatable) obj).validate (this);
	    }
	}

	element.validateThis (this);
    }

    /**
     * Validate an element and its content model for ID/IDREF consistency.
     * @param element Element
     * @throws MetsException Validation exception
     */
    public void validateIDREFs (MetsVElement element)
	throws MetsException
    {
	Iterator iter = element.getContent ().iterator ();
	while (iter.hasNext ()) {
	    Object obj = iter.next ();
	    if (obj instanceof MetsVElement) {
		((MetsVElement) obj).validateIDREFs (this);
	    }
	}

	element.validateThisIDREFs (this);
    }
}
