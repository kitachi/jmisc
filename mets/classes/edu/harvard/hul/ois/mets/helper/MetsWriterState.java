/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

/**
 * Write state.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-26
 */
public class MetsWriterState
    extends edu.harvard.hul.ois.mets.helper.Enum
{
    /******************************************************************
     * PUBLIC CLASS FIELDS.
     ******************************************************************/

    public static final MetsWriterState   ATTRIBUTE =
	            new MetsWriterState ("ATTRIBUTE");
    public static final MetsWriterState   ATTRIBUTE_NAME =
	            new MetsWriterState ("ATTRIBUTE_NAME");
    public static final MetsWriterState   ATTRIBUTE_VALUE =
	            new MetsWriterState ("ATTRIBUTE_VALUE");
    public static final MetsWriterState   BINARY =
	            new MetsWriterState ("BINARY");
    public static final MetsWriterState   CDATA =
	            new MetsWriterState ("CDATA");
    public static final MetsWriterState   CHAR_REF =
	            new MetsWriterState ("CHAR_REF");
    public static final MetsWriterState   COMMENT =
	            new MetsWriterState ("COMMENT");
    public static final MetsWriterState   END_TAG =
	            new MetsWriterState ("END_TAG");
    public static final MetsWriterState   ENTITY_REF =
	            new MetsWriterState ("ENTITY_REF");
    public static final MetsWriterState   INIT =
	            new MetsWriterState ("INIT");
    public static final MetsWriterState   PI =
	            new MetsWriterState ("PI");
    public static final MetsWriterState   PROLOG =
	            new MetsWriterState ("PROLOG");
    public static final MetsWriterState   START_TAG =
	            new MetsWriterState ("START_TAG");
    public static final MetsWriterState   TEXT =
	            new MetsWriterState ("TEXT");

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>MetsWriterState</tt> object.
     * @param value State value
     */
    public MetsWriterState (String value)
    {
	super (value);
    }
}
