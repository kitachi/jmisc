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
 * This class encapsulates the &lt;<tt>mdWrap</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-19
 */
public class MdWrap
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Reference LABEL. */
    private String _LABEL;

    /** Pre-defined metadata type. */
    private Mdtype _MDTYPE;

    /** File MIME type. */
    private String _MIMETYPE;

    /** Reference XPTR. */
    private String _OTHERMDTYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>mdRef</tt>&gt; object.
     */
    public MdWrap ()
    {
	super ("mdWrap");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return metadata label.
     * @return Label
     */
    public String getLABEL ()
    {
	return _LABEL;
    }

    /**
     * Return pre-defined metadata type.
     * @return Type
     */
    public Mdtype getMDTYPE ()
    {
	return _MDTYPE;
    }

    /**
     * Return metadata MIME type.
     * @return MIME type
     */
    public String getMIMETYPE ()
    {
	return _MIMETYPE;
    }

    /**
     * Return other metadata type.
     * @return Type
     */
    public String getOTHERMDTYPE ()
    {
	return _OTHERMDTYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set metadata label.
     * @param LABEL Label
     */
    public void setLABEL (String LABEL)
    {
	_LABEL = LABEL;
    }

    /**
     * Set metadata type.
     * @param MDTYPE Metadata type
     */
    public void setMDTYPE (Mdtype MDTYPE)
    {
	_MDTYPE = MDTYPE;
    }

    /**
     * Set file MIME type.
     * @param MIMETYPE MIME type
     */
    public void setMIMETYPE (String MIMETYPE)
    {
	_MIMETYPE = MIMETYPE;
    }

    /**
     * Set other metadata type.
     * @param OTHERMDTYPE Other metadata type
     */
    public void setOTHERMDTYPE (String OTHERMDTYPE)
    {
	_OTHERMDTYPE = OTHERMDTYPE;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>MdWrap</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return MdWrap object
     * @throws MetsException De-serializing exception
     */
    public static MdWrap reader (MetsReader r)
	throws MetsException
    {
	MdWrap mdWrap = new MdWrap ();
	mdWrap.read (r);

	return mdWrap;
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
	    else if (name.equals ("MIMETYPE")) {
		setMIMETYPE (value);
	    }
	    else if (name.equals ("MDTYPE")) {
		setMDTYPE (Mdtype.parse (value));
	    }
	    else if (name.equals ("OTHERMDTYPE")) {
		setOTHERMDTYPE (value);
	    }
	    else if (name.equals ("LABEL")) {
		setLABEL (value);
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
     * Serialize the content of this element using the given writer.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	if (_MIMETYPE != null) {
	    w.attribute ("MIMETYPE", _MIMETYPE);
	}
	w.attribute ("MDTYPE", _MDTYPE.toString ());
	if (_OTHERMDTYPE != null) {
	    w.attribute ("OTHERMDTYPE", _OTHERMDTYPE);
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
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
	if (_MDTYPE == null) {
	    throw new MetsException ("No mdWrap MDTYPE value");
	}
	else {
	    if (_MDTYPE.toString ().equals ("OTHER")) {
		if (_OTHERMDTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No mdWrap OTHERMDTYPE value");
		}
	    }
	    else {
		if (_OTHERMDTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println ("mdWrap OTHERMDTYPE only valid " +
					     "with MDTYPE=\"OTHER\"");
		}
	    }
	}

	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No mdWrap binData or xmlData");
	}

	_valid = true;
    }
}
