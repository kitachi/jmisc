/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets;

import edu.harvard.hul.ois.mets.helper.*;
import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

/**
 * This class encapsulates the &lt;<tt>xmlData</tt>&gt; element.
 *
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 *
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-18
 */
public class XmlData
    extends MetsVElement
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>xmlData</tt>&gt; object.
     */
    public XmlData ()
    {
	super ("xmlData");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>XmlData</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return XmlData object
     * @throws MetsException De-serializing exception
     */
    public static XmlData reader (MetsReader r)
	throws MetsException
    {
	XmlData xmlData = new XmlData ();
	xmlData.read (r);

	return xmlData;
    }

    /**
     * De-serialize the content of the file into this element using
     * the given reader.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
	Token token = r.getStart (_localName);
	_qName     = token.getQName ();
	_namespace = token.getNamespace ();
	_localName = token.getLocalName ();

	Attributes attrs = token.getAttributes ();
	if (attrs.hasNext ()) {
	    Attribute attr = attrs.next ();
	    String name = attr.getQName ();
	    String value = attr.getValue ();

	    if  (name.substring (0, 6).equals ("xmlns:")) {
		String prefix = name.substring (6);
		setSchema (prefix, value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	while (r.atStart ()) {
	    Any any = Any.reader (r);
	    _content.add (any);
	}

	r.getEnd (_localName);
    }

    /**
     * Serialize the content of this element using the given writer.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}
	writeSchemas (w);

	Iterator iter = _content.iterator ();
	while (iter.hasNext ()) {
	    ((MetsSerializable) iter.next ()).write (w);
	}
	w.end (_qName);
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate this element using the given validator.
     * @param v Validator
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No xmlData content");
	}
	_valid = true;
    }
}
