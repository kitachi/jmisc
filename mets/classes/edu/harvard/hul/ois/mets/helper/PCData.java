/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.io.*;
import java.util.*;

/**
 * Class encapsulating a PCDATA text string.
 * Note that PCDATA content can be specified as either <tt>String</tt>
 * or <tt>Reader</tt> objects.
 * @author Stephen Abrams
 * @version 1.1 2002/Dec/24
 */
public class PCData
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

    /** PCDATA content model. */
    private List _content;

    /** Content character reader. */
    private Reader _reader;

    /******************************************************************
     * CLASS CONSRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>PCData</tt> object.
     */
    public PCData ()
    {
	init ();
    }

    /**
     * Instantiate a <tt>PCData</tt> object with string content.
     * @param content String content
     */
    public PCData (String content)
    {
	init ();
	_content.add (content);
    }

    /**
     * Instantiate a <tt>PCData</tt> object with a content reader.
     * Objects instantiated with this constructor must not attempt
     * to add content to the list returned by the <tt>getContent()</tt>
     * method.
     * @param reader Reader
     */
    public PCData (Reader reader)
    {
	_reader = reader;
    }

    /**
     * Initialize a <tt>PCData</tt> object.
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
     * Add additional PCDATA content.
     * @param content PCDATA content
     */
    public void add (String content)
    {
	_content.add (content);
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>PCData</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return PCData object
     * @throws MetsException De-serializing exception
     */
    public static PCData reader (MetsReader r)
	throws MetsException
    {
	PCData pcdata = new PCData ();
	pcdata.read (r);

	return pcdata;
    }

    /**
     * De-serialize this element and its content model.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
	String content = r.getText ();
	_content.add (content);
    }

    /**
     * Serialize this element and its content model.
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
		    w.text (new String (buffer, 0, n));
		}
	    }
	    catch (IOException e) {
		throw new MetsException (e.toString ());
	    }
	}
	else {
	    for (int i=0; i<_content.size (); i++) {
		w.text ((String) _content.get (i));
	    }
	}
    }
}
