/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Abstract identifiable element class.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/06
 */
public abstract class MetsIDElement
    extends MetsVElement
    implements MetsIdentifiable
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Element ID. */
    protected String _ID;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate an <tt>MetsIDElement</tt> object.
     * @param qName Element local name
     */
    public MetsIDElement (String qName)
    {
	super (qName);
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get the element ID.
     * @return Element ID
     */
    public String getID ()
    {
	return _ID;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set the element ID.
     * @param ID Element ID
     */
    public void setID (String ID)
    {
	_ID = ID;
	_valid = false;
    }
}
