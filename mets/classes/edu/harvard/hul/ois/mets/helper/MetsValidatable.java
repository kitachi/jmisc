/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Validatable interface.
 * @author Stephen Abrams
 * @version 1.1 2002/Dec/24
 */
public interface MetsValidatable
{
    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Get the validity flag.
     * @return Validity flag
     */
    public boolean isValid ();

    /**
     * Validate this element and its content model using the given
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validate (MetsValidator v)
	throws MetsException;

    /**
     * Validate this element using the given validator.
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException;
}
