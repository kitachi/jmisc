/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.io.*;
/**
 * Writer to serialize an in-memory representation of a METS document
 * to a file.
 * @author Stephen Abrams
 * @version 1.3.7 2005-07-28
 */
public class MetsWriter
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** Default encoding. */
    private static final String DEFAULT_ENCODING = "utf-8";

    /** XML version. */
    private static final String XML_VERSION = "1.0";

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Output encoding. */
    private String _encoding;

    /** Element nesting level. */
    private int _level;

    /** Write state. */
    private MetsWriterState _state;

    /** Character output stream writer. */
    private Writer _charWriter;

    /** Byte output stream writer. */
    private OutputStream _byteWriter;

    /** If true then suppress the escaping of PCDATA and attribute values. */
    private boolean _noEscape;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>MetsWriter</tt> object with an open output
     * stream.
     * @param out Open output stream
     * @throws MetsException I/O exception
     */
    public MetsWriter (OutputStream out)
	throws MetsException
    {
	init (out, DEFAULT_ENCODING);
    }

    /**
     * Instantiate a <tt>MetsWriter</tt> object for an open output
     * stream with an encoding.
     * @param out Open output stream
     * @param encoding Character encoding
     * @throws MetsException I/O exception
     */
    public MetsWriter (OutputStream out, String encoding)
	throws MetsException
    {
	init (out, encoding);
    }

    /**
     * Initialize a <tt>MetsWriter</tt> object for an open output
     * stream with an encoding.
     * @param out Open output stream
     * @param encoding Character encoding
     * @throws MetsException I/O exception
     */
    private void init (OutputStream out, String encoding)
	throws MetsException
    {
	try {
            _byteWriter = new BufferedOutputStream(out);
	    _encoding = encoding;
	    _charWriter = new BufferedWriter (new OutputStreamWriter (out,
                                                                      _encoding));
	    _noEscape = false;
	    _level = -1;
	    _state = MetsWriterState.INIT;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /******************************************************************
     * PRIVATE CLASS METHODS.
     ******************************************************************/

    /**
     * Escape illegal characters '&', '<', and '"' in an attribute value.
     * @param value Source value
     * @return Escaped value
     */
    private static String escapeValue (String value)
    {
	StringBuffer escape = new StringBuffer (escapeText (value));
	for (int i=0; (i = escape.toString ().indexOf ('"', i)) > -1; i+=6) {
	    escape.replace (i, i+1, "&quot;");
	}

	return escape.toString ();
    }

    /**
     * Escape illegal characters '&' and  '<' in content text.
     * @param text Source text
     * @return Escaped text
     */
    private static String escapeText (String text)
    {
	StringBuffer escape = new StringBuffer (text);
	for (int i=0; (i = escape.toString ().indexOf ('&', i)) > -1; i+=5) {
	    escape.replace (i, i+1, "&amp;");
	}
	for (int i=0; (i = escape.toString ().indexOf ('<', i)) > -1; i+=4) {
	    escape.replace (i, i+1, "&lt;");
	}

	return escape.toString ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return the value of the global "no escape" flag.
     */
    public boolean getNoEscape ()
    {
	return _noEscape;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set the value of the global "no escape" flag.
     * @param noEscape "No escape" flag
     */
    public void setNoEscape (boolean noEscape)
    {
	_noEscape = noEscape;
    }

    /******************************************************************
     * Serialization methods.
     ******************************************************************/

    /**
     * Write an attribute to the output stream.
     * @param qName Attribute QName
     * @param value Attribute value
     * @throws MetsException I/O exception
     */
    public void attribute (String qName, String value)
	throws MetsException
    {
	attribute (qName, value, false);
    }

    /**
     * Write an attribute to the output stream.
     * @param qName Attribute QName
     * @param value Attribute value
     * @param noEscape If true, no XML escaping is performed on the text
     * @throws MetsException I/O exception
     */
    public void attribute (String qName, String value, boolean noEscape)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\"");
	    }
	    if (qName != null && value != null) {
		if (_noEscape || noEscape) {
		    _charWriter.write (" " + qName + "=\"" + value + "\"");
		}
		else {
		    _charWriter.write (" " + qName + "=\"" +
				       escapeValue (value) + "\"");
		}
	    }
	    _state = MetsWriterState.ATTRIBUTE;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write an attribute name to the output stream.
     * @param qName Attribute QName
     * @throws MetsException I/O exception
     */
    public void attributeName (String qName)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\"");
	    }
	    _charWriter.write (" " + qName);
	    _state = MetsWriterState.ATTRIBUTE_NAME;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write an attribute value to the output stream.
     * @param value Attribute value
     * @throws MetsException I/O exception
     */
    public void attributeValue (String value)
	throws MetsException
    {
	attributeValue (value, false);
    }

    /**
     * Write an attribute value to the output stream.
     * @param value Attribute value
     * @param noEscape If true, no XML escaping is performed on the attribute
     * @throws MetsException I/O exception
     */
    public void attributeValue (String value, boolean noEscape)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.ATTRIBUTE_NAME)) {
		_charWriter.write ("=\"");
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write (" ");
	    }
	    if (value != null) {
		if (_noEscape || noEscape) {
		    _charWriter.write (value);
		}
		else {
		    _charWriter.write (escapeValue (value));
		}
	    }
	    _state = MetsWriterState.ATTRIBUTE_VALUE;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Writes b.length bytes from byte array to the output stream.  
     * @param b byte array data
     * @throws MetsException I/O exception
     */
    public void binary (byte[] b)
	throws MetsException
    {
        binary(b, 0, b.length);
    }

    /**
     * Writes b.length bytes from byte array to the output stream.  
     * @param b byte array data
     * @param off the start offset in the array
     * @param len the number of bytes to write
     * @throws MetsException I/O exception
     */
    public void binary (byte[] b, int off, int len)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    if (b != null) {
		_byteWriter.write (b,off,len);
	    }
	    _state = MetsWriterState.BINARY;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write CDATA to the output stream.
     * @param value CDATA value
     * @throws MetsException I/O exception
     */
    public void cdata (String value)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    _charWriter.write ("<![CDATA[" + value + "]]>");
	    _state = MetsWriterState.CDATA;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write character reference to the output stream.
     * @param value Reference value
     * @throws MetsException I/O exception
     */
    public void char_ref (int value)
	throws MetsException
    {
	char_ref (value, false);
    }

    /**
     * Write character reference to the output stream.
     * @param value Reference value
     * @param hex True if a hexadecimal value
     * @throws MetsException I/O exception
     */
    public void char_ref (int value, boolean hex)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    if (hex) {
		_charWriter.write ("&#x" + Integer.toHexString (value) + ";");
	    }
	    else {
		_charWriter.write ("&#" + value + ";");
	    }
	    _state = MetsWriterState.CHAR_REF;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write comment to the output stream.
     * @param value Comment value
     * @throws MetsException I/O exception
     */
    public void comment (String value)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    _charWriter.write ("<!--" + value + "-->");
	    _state = MetsWriterState.COMMENT;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write an XML declaration to the output stream.
     * @throws MetsException I/O exception
     */
    public void declaration ()
	throws MetsException
    {
	try {
	    _charWriter.write ("<?xml version=\"" + XML_VERSION +
			   "\" encoding=\"" + _encoding +
			   "\" standalone=\"no\"?>");
	    _state = MetsWriterState.PROLOG;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write an end tag to the output stream.
     * @param qName Element QName
     * @throws MetsException I/O exception
     */
    public void end (String qName)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ("/>");
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\"/>");
	    }
	    else {
		if (_state.equals (MetsWriterState.START_TAG)) {
		    _charWriter.write (">");
		}
		if (!_state.equals (MetsWriterState.TEXT) &&
                    !_state.equals (MetsWriterState.BINARY) &&
		    !_state.equals (MetsWriterState.CDATA) &&
		    !_state.equals (MetsWriterState.CHAR_REF) &&
		    !_state.equals (MetsWriterState.ENTITY_REF)) {
		    _charWriter.write ('\n');
		    for (int i=0; i<_level; i++) {
			_charWriter.write (' ');
		    }
		}
		_charWriter.write ("</" + qName + ">");
	    }

	    if (_level-- == 0) {
		_charWriter.write ('\n');
	    }
	    _state = MetsWriterState.END_TAG;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write entity reference to the output stream.
     * @param value Reference value
     * @throws MetsException I/O exception
     */
    public void entity_ref (String value)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    _charWriter.write ("&" + value + ";");
	    _state = MetsWriterState.ENTITY_REF;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Flush the output stream.
     * @throws MetsException I/O exception
     */
    public void flush ()
	throws MetsException
    {
	try {
	    _byteWriter.flush ();
	    _charWriter.flush ();
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write processing instruction to the output stream.
     * @param target PI target
     * @param value PI value
     * @throws MetsException I/O exception
     */
    public void pi (String target, String value)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    _charWriter.write ("<?" + target + " " + value + "?>");
	    _state = MetsWriterState.PI;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write a start tag to the output stream.
     * @param qName Element QName
     * @throws MetsException I/O exception
     */
    public void start (String qName)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }

	    _level++;
	    if (!_state.equals (MetsWriterState.INIT) &&
		!_state.equals (MetsWriterState.TEXT) &&
                !_state.equals (MetsWriterState.BINARY) &&
		!_state.equals (MetsWriterState.CDATA) &&
		!_state.equals (MetsWriterState.CHAR_REF) &&
		!_state.equals (MetsWriterState.ENTITY_REF)) {
		_charWriter.write ('\n');
		for (int i=0; i<_level; i++) {
		    _charWriter.write (' ');
		}
	    }
	    _charWriter.write ("<" + qName);
	    _state = MetsWriterState.START_TAG;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Write text to the output stream.
     * @param value Text value
     * @throws MetsException I/O exception
     */
    public void text (String value)
	throws MetsException
    {
	text (value, false);
    }

    /**
     * Write text to the output stream.
     * @param value Text value
     * @param noEscape If true, no XML escaping is performed on the text
     * @throws MetsException I/O exception
     */
    public void text (String value, boolean noEscape)
	throws MetsException
    {
	try {
	    if (_state.equals (MetsWriterState.START_TAG) ||
		_state.equals (MetsWriterState.ATTRIBUTE)) {
		_charWriter.write ('>');
	    }
	    else if (_state.equals (MetsWriterState.ATTRIBUTE_VALUE)) {
		_charWriter.write ("\">");
	    }
	    if (value != null) {
		if (_noEscape || noEscape) {
		    _charWriter.write (value);
		}
		else {
		    _charWriter.write (escapeText (value));
		}
	    }
	    _state = MetsWriterState.TEXT;
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }
}
