/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

/**
 * This class encapsulates a type-safe enumeration of values.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/02
 */
public abstract class Enum
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Enumeration value. */
    private String _value;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate an <tt>Enum</tt> object.
     * @param value Enumeration value
     */
    public Enum (String value)
    {
	_value = value;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Test for equality.
     * @return True, if equal
     */
    public boolean equals (Enum enum)
    {
	return this == enum;
    }

    /**
     * Return the enumeration value.
     * @return Value
     */
    public String toString ()
    {
	return _value;
    }
}
