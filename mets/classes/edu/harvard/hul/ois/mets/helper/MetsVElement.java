/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Abstract validatable element class.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/06
 */
public abstract class MetsVElement
    extends MetsElement
    implements MetsValidatable
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Validation flag. */
    protected boolean _valid;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate an <tt>MetsVElement</tt> object.
     * @param qName Element local name
     */
    public MetsVElement (String qName)
    {
	super (qName);
	_valid = false;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get the validity flag.
     * @return Validity flag
     */
    public boolean isValid ()
    {
	return _valid;
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate this element and its content model using the given
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validate (MetsValidator v)
	throws MetsException
    {
	v.validate (this);
    }

    /**
     * Validate this element for ID/IDREF and its content model for
     * consistency using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateIDREFs (MetsValidator v)
	throws MetsException
    {
	v.validateIDREFs (this);
    }

    /**
     * Validate this element using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	_valid = true;
    }

    /**
     * Validate this element for ID/IDREF consistency using the given
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThisIDREFs (MetsValidator v)
	throws MetsException
    {
    }
}
