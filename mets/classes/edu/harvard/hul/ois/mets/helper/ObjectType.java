/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the
 * Digital Library Federation <http://www.diglib.org> and is
 * maintained by the Network Development and MARC Standards Office of
 * the Library of Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import edu.harvard.hul.ois.mets.*;
import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

/**
 * This class encapsulates the behavior section element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-06
 */
public class ObjectType
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Description of the linked-to object. */
    private String _LABEL;

    /** Pre-defined locator type. */
    private Loctype _LOCTYPE;

    /** Other locator type, if LOCTYPE="OTHER". */
    private String _OTHERLOCTYPE;

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
     * Instantiate a new &lt;<tt>ObjectType</tt>&gt; object.
     */
    public ObjectType (String qName)
    {
	super (qName);
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return description of linked-to object type.
     * @return Label
     */
    public String getLABEL ()
    {
	return _LABEL;
    }

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
     * Set description of linked-to type.
     * @param LABEL Description
     */
    public void setLABEL (String LABEL)
    {
	_LABEL = LABEL;
    }

    /**
     * Set pre-defined locator type.
     * @param LOCTYPE Pre-defined type
     */
    public void setLOCTYPE (Loctype LOCTYPE)
    {
	_LOCTYPE = LOCTYPE;
    }

    /**
     * Set other locator type.
     * @param OTHERLOCTYPE Other type
     */
    public void setOTHERLOCTYPE (String OTHERLOCTYPE)
    {
	_OTHERLOCTYPE = OTHERLOCTYPE;
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
	    else if (name.equals ("LABEL")) {
		setLABEL (value);
	    }
	    else if (name.equals ("LOCTYPE")) {
		setLOCTYPE (Loctype.parse (value));
	    }
	    else if (name.equals ("OTHERLOCTYPE")) {
		setOTHERLOCTYPE (value);
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
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (qName, value, '"'));
	    }
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
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	w.attribute ("LOCTYPE", _LOCTYPE.toString ());
	if (_OTHERLOCTYPE != null) {
	    w.attribute ("OTHERLOCTYPE", _OTHERLOCTYPE);
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
	    throw new MetsException ("No value for " + _localName + " LOCTYPE");
	}
	else {
	    if (_LOCTYPE.toString ().equals ("OTHER")) {
		if (_OTHERLOCTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No " + _localName +
					     " OTHERLOCTYPE value");
		}
	    }
	    else {
		if (_OTHERLOCTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println (_localName + " OTHERLOCTYPE " +
					     "only valid with " +
					     "LOCTYPE=\"OTHER\"");
		}
	    }
	}

	if (_xlinkHref == null) {
	    throw new MetsException ("No " + _localName + " xlink:href value");
	}

	_valid = true;
    }
}
