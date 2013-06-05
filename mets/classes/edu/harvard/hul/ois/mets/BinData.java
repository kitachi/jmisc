/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>binData</tt>&gt; element.
 * Note that all content must be Base64 encoded, as defined by RFC 2045.
 *
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 *
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-06
 */
public class BinData
    extends MetsVElement
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>binData</tt>&gt; object.
     */
    public BinData ()
    {
	super ("binData");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>BinData</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return BinData object
     * @throws MetsException De-serializing exception
     */
    public static BinData reader (MetsReader r)
	throws MetsException
    {
	BinData binData = new BinData ();
	binData.read (r);

	return binData;
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
	while (attrs.hasNext ()) {
	    Attribute attr = attrs.next ();
	    String qName = attr.getQName ();
	    String value = attr.getValue ();

	    if (_attrs == null) {
		_attrs = new Attributes ();
	    }
	    _attrs.add (new Attribute (qName, value, '"'));
	}

	while (r.atText ()) {
	    PCData data = PCData.reader (r);
	    _content.add (data);
	}
	 
	r.getEnd (_localName);
    }

    /**
     * De-serialize the content of this element using the given writer.
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
     * Validate this element the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No binData content");
	}

	_valid = true;
    }
}
