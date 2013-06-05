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

import java.text.*;
import java.util.*;

/**
 * Wrapper class to format date/time strings according to the
 * <tt>xsd:dateTime</tt> format.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/03
 */
public class DateTime
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** Formatter for <tt>xsd:dateTime</tt> formatted date/time string. */
    private static SimpleDateFormat _dateTime =
	       new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");

    /******************************************************************
     * PUBLIC CLASS METHODS.
     ******************************************************************/

    /**
     * Return string representation of date.
     * @param date Date
     */
    public static String format (Date date)
    {
	return _dateTime.format (date);
    }

    /**
     * Return Date representation of string.
     * @param date Date
     */
    public static Date parse (String date) throws ParseException
    {
	return _dateTime.parse (date);
    }
}
