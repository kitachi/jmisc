/**********************************************************************
 * METS Java toolkit serializing example
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

public class Any
    extends MetsElement
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a name-less <tt>Any</tt> object.
     */
    public Any ()
    {
	super ();
    }

    /**
     * Instantiate a named <tt>Any</tt> object.
     * @param qName Element QName
     */
    public Any (String qName)
    {
	super (qName);
    }

    /**
     * Instantiate a named <tt>Any</tt> object with text content.
     * @param qName Element QName
     * @param content Text content
     */
    public Any (String qName, String content)
    {
	super (qName);
	_content.add (new PCData (content));
    }

    /**
     * Instantiate a named <tt>Any</tt> object with attributes.
     * @param qName Element QName
     * @param attrs Attributes
     */
    public Any (String qName, Attributes attrs)
    {
	super (qName);

	_attrs = attrs;
    }

    /**
     * Instantiate a named <tt>Any</tt> object with attributes and text
     * content.
     * @param qName Element QName
     * @param attrs Attributes
     * @param content Text content
     */
    public Any (String qName, Attributes attrs, String content)
    {
	super (qName);

	_attrs = attrs;
	_content.add (new PCData (content));
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Any</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Any object
     * @throws MetsException De-serializing exception
     */
    public static Any reader (MetsReader r)
	throws MetsException
    {
	Any any = new Any ();
	any.read (r);

	return any;
    }

    /**
     * De-serialize the content of the instance document into this
     * element using the given reader
     * @param r Reader
     * @throws MetsException I/O exception
     */
    public void read (MetsReader r)
	throws MetsException
    {
	Token token = r.getStart ();
	_qName     = token.getQName ();
	_namespace = token.getNamespace ();
	_localName = token.getLocalName ();
	_attrs     = token.getAttributes ();

        if (token.getType().equals (Type.EMPTY_TAG)) {
            return;
        }

	while ((token = r.atContent ()) != null) {
	    Type type = token.getType ();
	    if (type.equals (Type.START_TAG) ||
		type.equals (Type.EMPTY_TAG)) {
		_content.add (Any.reader (r));
	    }
	    else if (type.equals (Type.TEXT) ||
		     type.equals (Type.CHAR_REF) ||
		     type.equals (Type.CDATA) ||
		     type.equals (Type.ENTITY_REF)) {
		_content.add (PCData.reader (r));
            }
        }

        if (r.atEnd (_localName)) {
            r.getEnd (_localName);
        }
    }

    /**
     * Serialize this element and its content model using the given
     * writer.
     * @param w Writer
     * @throws MetsException I/O exception
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_attrs != null) {
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}
	Iterator iter = _content.iterator ();
	while (iter.hasNext ()) {
	    ((MetsSerializable) iter.next ()).write (w);
	}
	w.end (_qName);
    }
}

