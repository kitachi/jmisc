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
 * This class encapsulates the &lt;<tt>FContent</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 *
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-19
 */
public class FContent
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Use flag. */
    private String _USE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>FContent</tt>&gt; object.
     */
    public FContent ()
    {
	super ("FContent");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return use flag for this <tt>fileGrp</tt>.
     * @return Use
     */
    public String getUSE ()
    {
	return _USE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set use flag for this <tt>fileGrp</tt>.
     * @param use Use flag
     */
    public void setUSE (String use)
    {
	_USE = use;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>FContent</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return FContent object
     * @throws MetsException De-serializing exception
     */
    public static FContent reader (MetsReader r)
	throws MetsException
    {
	FContent fContent = new FContent ();
	fContent.read (r);

	return fContent;
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
	    String name  = attr.getLocalName ();
	    String value = attr.getValue ();

	    if (name.equals ("ID")) {
		setID (value);
	    }
	    else if (name.equals ("USE")) {
		setUSE (value);
	    }
	    else if  (qName.substring (0, 6).equals ("xmlns:")) {
		String prefix = qName.substring (6);
		setSchema (prefix, value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	if (r.atStart ("binData")) {
	    BinData binData = BinData.reader (r);
	    _content.add (binData);
	}
	if (r.atStart ("xmlData")) {
	    XmlData xmlData = XmlData.reader (r);
	    _content.add (xmlData);
	}

	r.getEnd (_localName);
    }

    /**
     * Serialize the content of this element using the given
     * writer.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	if (_USE != null) {
	    w.attribute ("USE", _USE);
	}
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
	    ((MetsElement) iter.next ()).write (w);
	}

	w.end (_qName);
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate this element using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No FContent content");
	}

	_valid = true;
    }
}
