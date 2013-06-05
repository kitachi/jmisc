/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import edu.harvard.hul.ois.mets.helper.parser.*;
import java.io.*;

/**
 * Reader to de-serialize a METS file to an in-memory representation.
 * @author Stephen Abrams
 * @version 1.3.8 2005-08-22
 */
public class MetsReader
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Debug flag. */
    private boolean _debug;

    /** Input stream reader. */
    private Parser _parser;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>MetsReader</tt> object with an open input
     * stream.
     * @param in Open input stream
     * @throws MetsException I/O exception
     */
    public MetsReader (InputStream in)
	throws MetsException
    {
	init (in, false);
    }

    /**
     * Instantiate a <tt>MetsReader</tt> object with an open input
     * stream.
     * @param in Open input stream
     * @param debug Debug flag
     * @throws MetsException I/O exception
     */
    public MetsReader (InputStream in, boolean debug)
	throws MetsException
    {
	init (in, debug);
    }

    /**
     * Initialize a <tt>MetsReader</tt> object with an open input
     * stream.
     * @param in Open input stream
     * @param debug Debug flag
     * @throws MetsException I/O exception
     */
    private void init (InputStream in, boolean debug)
	throws MetsException
    {
	_debug = debug;
	try {
	    _parser = new Parser ();
	    _parser.setInput (in);
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Determine if the current parser token is child content.
     * @return True if current token is child content
     * @throws MetsException Parsing exception
     */
    public Token atContent ()
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("atContent()");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.TEXT) ||
		    type.equals (Type.CHAR_REF) ||
		    type.equals (Type.ENTITY_REF) ||
		    type.equals (Type.CDATA)) {
		    return token;
		}
		else if (type.equals (Type.START_TAG) ||
			 type.equals (Type.EMPTY_TAG)) {
		    return token;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    return null;
		}
	    }
	    throw new MetsException ("premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Determine if the current parser token is an end tag.
     * @param localName Element local name
     * @return True if current token is an end tag
     * @throws MetsException Parsing exception
     */
    public boolean atEnd (String localName)
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("atEnd(" + localName + ")");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.END_TAG)) {
		    if (token.getLocalName ().equals (localName)) {
			return true;
		    }
		    return false;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    return false;
		}
	    }
	    throw new MetsException ("premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Determine if the current parser token is an start tag.
     * @return True if current token is an start tag
     * @throws MetsException Parsing exception
     */
    public boolean atStart ()
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("atStart()");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.START_TAG) ||
		    type.equals (Type.EMPTY_TAG)) {
		    return true;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    return false;
		}
	    }
	    throw new MetsException ("premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Determine if the current parser token is an start tag.
     * @param localName Element local name
     * @return True if current token is an start tag
     * @throws MetsException Parsing exception
     */
    public boolean atStart (String localName)
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("atStart(" + localName + ")");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.START_TAG) ||
		    type.equals (Type.EMPTY_TAG)) {
		    if (token.getLocalName ().equals (localName)) {
			return true;
		    }
		    return false;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    return false;
		}
	    }
	    throw new MetsException ("premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Determine if the current parser token is text.
     * @return True if current token is text
     * @throws MetsException Parsing exception
     */
    public boolean atText ()
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("atText()");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.TEXT) ||
		    type.equals (Type.CHAR_REF) ||
		    type.equals (Type.CDATA)) {
		    return true;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI)) {
		    _parser.getToken (_debug);
		}
		else {
		    return false;
		}
	    }
	    throw new MetsException ("premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Consume an end tag.
     * @param localName Local element name
     * @throws MetsException Parsing exception
     */
    public void getEnd (String localName)
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("getEnd(" + localName + ")");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.END_TAG)) {
		    if (!token.getLocalName ().equals (localName)) {
			throw new MetsException ("expecting \"" + localName +
						 "\"; saw \"" +
						 token.getLocalName () + "\"");
		    }
		    _parser.getToken (_debug);

		    return;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE)) {
		    _parser.getToken (_debug);
		}
		else {
		    throw new MetsException ("Invalid parser token: " +
					     type.toString ());
		}
	    }

	    throw new MetsException ("Premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Consume a start tag.
     * @return Element token
     * @throws MetsException Parsing exception
     */
    public Token getStart ()
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("getStart()");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.START_TAG) ||
		    type.equals (Type.EMPTY_TAG)) {
		    _parser.getToken (_debug);
		    return token;
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    throw new MetsException ("Invalid parser token: " +
					     type.toString ());
		}
	    }

	    throw new MetsException ("Premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Consume a start tag.
     * @param localName Local element name
     * @return Element token
     * @throws MetsException Parsing exception
     */
    public Token getStart (String localName)
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("getStart(" + localName + ")");
	}
	Token token = null;

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.START_TAG) ||
		    type.equals (Type.EMPTY_TAG)) {
		    if (token.getLocalName ().equals (localName)) {
			_parser.getToken (_debug);

			return token;
		    }
		    else {
			throw new MetsException ("expecting \"" + localName +
						 "\"; saw \"" +
						 token.getLocalName () + "\"");
		    }
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI) ||
			 type.equals (Type.START_DOC) ||
			 type.equals (Type.TEXT) ||
			 type.equals (Type.WHITESPACE) ||
			 type.equals (Type.XML_DECL)) {
		    _parser.getToken (_debug);
		}
		else {
		    throw new MetsException ("Invalid parser token: " +
					     type.toString ());
		}
	    }

	    throw new MetsException ("Premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Consume text content.
     * @return Text
     * @throws MetsException Parsing exception
     */
    public String getText ()
	throws MetsException
    {
	if (_debug) {
	    System.out.println ("getText()");
	}
	Token token = null;
	StringBuffer buffer = new StringBuffer ();

	try {
	    while ((token = _parser.peekAtToken (_debug)) != null) {
		Type type = token.getType ();
		if (type.equals (Type.TEXT) ||
		    type.equals (Type.ENTITY_REF) ||
		    type.equals (Type.CDATA)) {
		    token = _parser.getToken (_debug);
		    buffer.append (token.getValue ());
		}
		else if (type.equals (Type.CHAR_REF)) {
		    token = _parser.getToken (_debug);
		    buffer.append (token.getChar ());
		}
		else if (type.equals (Type.COMMENT) ||
			 type.equals (Type.PI)) {
		    _parser.getToken (_debug);
		}
		else {
		    return buffer.toString ();
		}
	    }

	    throw new MetsException ("Premature end-of-file");
	}
	catch (Exception e) {
	    throw new MetsException (e.toString ());
	}
    }

    /**
     * Return status of the reader debug flag.
     * @return True if the debug flag is set
     */
    public boolean isDebug ()
    {
	return _debug;
    }
}
