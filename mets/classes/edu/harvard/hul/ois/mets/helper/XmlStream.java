/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import edu.harvard.hul.ois.mets.helper.parser.*;
import java.io.*;
import java.util.*;

/**
 * Class encapsulating an XML stream, either a String or a Reader.
 * @author Stephen Abrams
 * @version 1.1 2003/Apr/16
 */
public class XmlStream
    implements MetsSerializable
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** XML string content. */
    private List _content;

    /** XML character reader. */
    private Reader _reader;

    /******************************************************************
     * CLASS CONSRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>XmlStream</tt> object with string content.
     * @param content String content
     */
    public XmlStream (String content)
    {
	_content = new ArrayList ();
	_content.add (content);
    }

    /**
     * Instantiate a <tt>XmlStream</tt> object with a content reader.
     * @param reader Reader
     */
    public XmlStream (Reader reader)
    {
	_reader = reader;
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
     * Instantiate a <tt>XmlStream</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return XmlStream object
     * @throws MetsException De-serializing exception
     */
    public static XmlStream reader (MetsReader r)
	throws MetsException
    {
	return null;
    }

    /**
     * De-serialize this element and its content model.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
    }

    /**
     * Serialize this element and its content model.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	if (_reader != null) {
	    Parser parser = new Parser ();
	    try {
		parser.setInput (_reader);
		Token token = null;
		while ((token = parser.getToken ()) != null) {
		    Type type = token.getType ();

		    if (type.equals (Type.EMPTY_TAG) ||
			type.equals (Type.START_TAG)) {
			w.start (token.getQName ());
			Attributes attrs = token.getAttributes ();
			while (attrs.hasNext ()) {
			    Attribute attr = attrs.next ();
			    w.attribute (attr.getQName (), attr.getValue ());
			}

			if (type.equals (Type.EMPTY_TAG)) {
			    w.end (token.getQName ());
			}
		    }
		    else if (type.equals (Type.TEXT)) {
			w.text (token.getValue ());
		    }
		    else if (type.equals (Type.CHAR_REF)) {
			w.char_ref (token.getCharValue (),
				    token.isHexValue ());
		    }
		    else if (type.equals (Type.ENTITY_REF)) {
			w.entity_ref (token.getQName ());
		    }
		    else if (type.equals (Type.CDATA)) {
			w.cdata (token.getValue ());
		    }
		    else if (type.equals (Type.COMMENT)) {
			w.comment (token.getValue ());
		    }
		    else if (type.equals (Type.END_TAG)) {
			w.end (token.getQName ());
		    }
		    else if (type.equals (Type.PI)) {
			w.pi (token.getQName (), token.getValue ());
		    }
		}
	    }
	    catch (Exception e) {
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
