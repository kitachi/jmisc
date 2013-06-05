/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

/**
 * METS Java toolkit exception class.
 * @author Stephen Abrams
 * @version 1.1 2002/Dec/23
 */
public class MetsException
    extends Exception
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public MetsException (String message)
    {
	super (message);
    }
}
