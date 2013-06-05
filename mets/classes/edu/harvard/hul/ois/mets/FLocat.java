/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2006 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>FLocat</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.2 2006-07-03
 */
public class FLocat
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Pre-defined locator type. */
    private Loctype _LOCTYPE;

    /** Other locator type, if LOCTYPE="OTHER". */
    private String _OTHERLOCTYPE;

    /** Use flag. */
    private String _USE;

    /** XLink actuate attribute; pre-defined actuate behaviors. */
    private Actuate _xlinkActuate;

    /** XLink arcrole attribute. */
    private String _xlinkArcrole;

    /** XLink href attribute. */
    private String _xlinkHref;

    /** XLink role attribute. */
    private String _xlinkRole;

    /** XLink show attribute; pre-defined show behaviors. */
    private Show _xlinkShow;

    /** XLink title attribute. */
    private String _xlinkTitle;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>FLocat</tt>&gt; object.
     */
    public FLocat ()
    {
	super ("FLocat");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return pre-defined locator type.
     * @return Type
     */
    public Loctype getLOCTYPE ()
    {
	return _LOCTYPE;
    }

    /**
     * Return other locator type.
     * @return Type
     */
    public String getOTHERLOCTYPE ()
    {
	return _OTHERLOCTYPE;
    }

    /**
     * Return use flag for this <tt>fileGrp</tt>.
     * @return Use
     */
    public String getUSE ()
    {
	return _USE;
    }

    /**
     * Return pre-defined XLink actuate attribute.
     * @return Actuate
     */
    public Actuate getXlinkActuate ()
    {
	return _xlinkActuate;
    }

    /**
     * Return XLink arcrole attribute.
     * @return Arcrole
     */
    public String getXlinkArcrole ()
    {
	return _xlinkArcrole;
    }

    /**
     * Return XLink href attribute.
     * @return Href
     */
    public String getXlinkHref ()
    {
	return _xlinkHref;
    }

    /**
     * Return XLink role attribute.
     * @return Role
     */
    public String getXlinkRole ()
    {
	return _xlinkRole;
    }

    /**
     * Return pre-defined XLink show attribute.
     * @return Show
     */
    public Show getXlinkShow ()
    {
	return _xlinkShow;
    }

    /**
     * Return XLink title attribute.
     * @return Title
     */
    public String getXlinkTitle ()
    {
	return _xlinkTitle;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set pre-defined locator type.
     * @param type Pre-defined type
     */
    public void setLOCTYPE (Loctype type)
    {
	_LOCTYPE = type;
    }

    /**
     * Set other locator type.
     * @param type Other type
     */
    public void setOTHERLOCTYPE (String type)
    {
	_OTHERLOCTYPE = type;
    }

    /**
     * Set use flag for this <tt>fileGrp</tt>.
     * @param use Use flag
     */
    public void setUSE (String use)
    {
	_USE = use;
    }

    /**
     * Set pre-defined Xlink actuate behavior.
     * @param actuate Actuate behavior
     */
    public void setXlinkActuate (Actuate actuate)
    {
	_xlinkActuate = actuate;
    }

    /**
     * Set Xlink arcrole attribute.
     * @param arcrole Arcrole
     */
    public void setXlinkArcrole (String arcrole)
    {
	_xlinkArcrole = arcrole;
    }

    /**
     * Set Xlink href attribute.
     * @param href Href
     */
    public void setXlinkHref (String href)
    {
	_xlinkHref = href;
    }

    /**
     * Set Xlink role attribute.
     * @param role Role
     */
    public void setXlinkRole (String role)
    {
	_xlinkRole = role;
    }

    /**
     * Set pre-defined Xlink show behavior.
     * @param show Show behavior
     */
    public void setXlinkShow (Show show)
    {
	_xlinkShow = show;
    }

    /**
     * Set Xlink title attribute
     * @param title Title
     */
    public void setXlinkTitle (String title)
    {
	_xlinkTitle = title;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>FLocat</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return FLocat object
     * @throws MetsException De-serializing exception
     */
    public static FLocat reader (MetsReader r)
	throws MetsException
    {
	FLocat fLocat = new FLocat ();
	fLocat.read (r);

	return fLocat;
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
	    else if (name.equals ("LOCTYPE")) {
		setLOCTYPE (Loctype.parse (value));
	    }
	    else if (name.equals ("OTHERLOCTYPE")) {
		setOTHERLOCTYPE (value);
	    }
	    else if (name.equals ("USE")) {
		setUSE (value);
	    }
	    else if (qName.equals ("xlink:href")) {
		setXlinkHref (value);
	    }
	    else if (qName.equals ("xlink:role")) {
		setXlinkRole (value);
	    }
	    else if (qName.equals ("xlink:arcrole")) {
		setXlinkArcrole (value);
	    }
	    else if (qName.equals ("xlink:title")) {
		setXlinkTitle (value);
	    }
	    else if (qName.equals ("xlink:show")) {
		setXlinkShow (Show.parse (value));
	    }
	    else if (qName.equals ("xlink:actuate")) {
		setXlinkActuate (Actuate.parse (value));
	    }
	    else if (!qName.equals ("xlink:type")) {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (qName, value, '"'));
	    }
	}
	if (r.atEnd (_localName)) {
	    r.getEnd (_localName);
	}
    }

    /**
     * Serialize the content of this element using the give
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
	w.attribute ("LOCTYPE", _LOCTYPE.toString ());
	if (_OTHERLOCTYPE != null) {
	    w.attribute ("OTHERLOCTYPE", _OTHERLOCTYPE);
	}
	if (_USE != null) {
	    w.attribute ("USE", _USE);
	}
	w.attribute ("xlink:type", "simple");
	w.attribute ("xlink:href", _xlinkHref);
	if (_xlinkRole != null) {
	    w.attribute ("xlink:role", _xlinkRole);
	}
	if (_xlinkArcrole != null) {
	    w.attribute ("xlink:arcrole", _xlinkArcrole);
	}
	if (_xlinkTitle != null) {
	    w.attribute ("xlink:title", _xlinkTitle);
	}
	if (_xlinkShow != null) {
	    w.attribute ("xlink:show", _xlinkShow.toString ());
	}
	if (_xlinkActuate != null) {
	    w.attribute ("xlink:actuate", _xlinkActuate.toString ());
	}
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}

	w.end (_qName);
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate this element using the given validator.
     * @param v Validator
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_LOCTYPE == null) {
	    throw new MetsException ("No value for FLocat LOCTYPE");
	}
	else {
	    if (_LOCTYPE.toString ().equals ("OTHER")) {
		if (_OTHERLOCTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No FLocat OTHERLOCTYPE value");
		}
	    }
	    else {
		if (_OTHERLOCTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println ("FLocat OTHERLOCTYPE " +
					     "only valid with " +
					     "LOCTYPE=\"OTHER\"");
		}
	    }
	}

	if (_xlinkHref == null) {
	    throw new MetsException ("No FLocat xlink:href value");
	}

	_valid = true;
    }
}
