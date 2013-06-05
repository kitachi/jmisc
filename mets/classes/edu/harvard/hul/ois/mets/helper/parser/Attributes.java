/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

import java.util.*;

public class Attributes
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** List of attributes. */
    private List _attrs;

    /** Index of current attribute in list. */ 
    private int _ptr;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public Attributes ()
    {
	_attrs = new ArrayList ();
	_ptr   = 0;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    public void add (Attribute attr)
    {
	_attrs.add (attr);
    }

    public Attribute get (int index)
    {
	Attribute attr = null;

	if (index < size ()) {
	    attr = (Attribute) _attrs.get (index);
	}

	return attr;
    }

    public boolean hasNext ()
    {
	return _ptr < size ();
    }

    public Attribute next ()
    {
	if (hasNext ()) {
	    return (Attribute) _attrs.get(_ptr++);
	}

	return null;
    }

    public void reset ()
    {
	_ptr = 0;
    }

    public int size ()
    {
	return _attrs.size ();
    }
}
