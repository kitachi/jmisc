/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.io.*;
import java.util.*;

/**
 * Class encapsulating pre-formed XML.
 * When serialized, no XML escaping is performed on this data.
 * @author Stephen Abrams
 * @version 1.3 2004-Apr-08
 */
public class PreformedXML
    implements MetsSerializable
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** Content reader buffer size. */
    private static final int BUFFERSIZE = 8192;

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** PreformedXML content model. */
    private List _content;

    /** Content character reader. */
    private Reader _reader;

    /******************************************************************
     * CLASS CONSRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>PreformedXML</tt> object.
     */
    public PreformedXML ()
    {
	init ();
    }

    /**
     * Instantiate a <tt>PreformedXML</tt> object with string content.
     * @param content String content
     */
    public PreformedXML (String content)
    {
	init ();
	_content.add (content);
    }

    /**
     * Instantiate a <tt>PreformedXML</tt> object with a content reader.
     * Objects instantiated with this constructor must not attempt
     * to add content to the list returned by the <tt>getContent()</tt>
     * method.
     * @param reader Reader
     */
    public PreformedXML (Reader reader)
    {
	_reader = reader;
    }

    /**
     * Initialize a <tt>PreformedXML</tt> object.
     */
    private void init ()
    {
	_content = new ArrayList ();
    }

    /******************************************************************
     * INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get content model.
     * @return Null
     */
    public List getContent ()
    {
	return _content;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Add additional PreformedXML content.
     * @param content PreformedXML content
     */
    public void add (String content)
    {
	_content.add (content);
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * This method is provided to satisfy the MetsSerializable interface,
     * but it is never invoked.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
    }

    /**
     * Serialize this element, with no XML escaping, and its content model.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	if (_reader != null) {
	    char [] buffer = new char[BUFFERSIZE];
	    int n;
	    try {
		while ((n = _reader.read (buffer, 0, BUFFERSIZE)) > 0) {
		    w.text (new String (buffer, 0, n), true);
		}
	    }
	    catch (IOException e) {
		throw new MetsException (e.toString ());
	    }
	}
	else {
	    for (int i=0; i<_content.size (); i++) {
		w.text ((String) _content.get (i), true);
	    }
	}
    }
}
