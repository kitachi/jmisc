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

/**
 * This class encapsulates the &lt;<tt>mdRef</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.2 2006-07-03
 */
public class MdRef
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Reference LABEL. */
    private String _LABEL;

    /** Pre-defined locator type. */
    private Loctype _LOCTYPE;

    /** Pre-defined metadata type. */
    private Mdtype _MDTYPE;

    /** File MIME type. */
    private String _MIMETYPE;

    /** Other locator type, if LOCTYPE="OTHER". */
    private String _OTHERLOCTYPE;

    /** Other metadata type, if MDTYPE="OTHER". */
    private String _OTHERMDTYPE;

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

    /** Reference XPTR. */
    private String _XPTR;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>mdRef</tt>&gt; object.
     */
    public MdRef ()
    {
	super ("mdRef");
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
     * Return pre-defined locator type.
     * @return Type
     */
    public Loctype getLOCTYPE ()
    {
	return _LOCTYPE;
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
     * Return other metadata locator type.
     * @return Type
     */
    public String getOTHERLOCTYPE ()
    {
	return _OTHERLOCTYPE;
    }

    /**
     * Return other metadata type.
     * @return Type
     */
    public String getOTHERMDTYPE ()
    {
	return _OTHERMDTYPE;
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

    /**
     * Return reference XPTR. 
     * @return XPTR
     */
    public String getXPTR ()
    {
	return _XPTR;
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
     * Set pre-defined locator type.
     * @param LOCTYPE Pre-defined type
     */
    public void setLOCTYPE (Loctype LOCTYPE)
    {
	_LOCTYPE = LOCTYPE;
    }

    /**
     * Set metadata type.
     * @param TYPE Type
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
     * Set other metadata locator type.
     * @param OTHERLOCTYPE Other type
     */
    public void setOTHERLOCTYPE (String OTHERLOCTYPE)
    {
	_OTHERLOCTYPE = OTHERLOCTYPE;
    }

    /**
     * Set other metadata type.
     * @param OTHERMDTYPE Other type
     */
    public void setOTHERMDTYPE (String OTHERMDTYPE)
    {
	_OTHERMDTYPE = OTHERMDTYPE;
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

    /**
     * Set metadata XPTR.
     * @param XPTR XPTR
     */
    public void setXPTR (String XPTR)
    {
	_XPTR = XPTR;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>MdRef</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return MdRef object
     * @throws MetsException De-serializing exception
     */
    public static MdRef reader (MetsReader r)
	throws MetsException
    {
	MdRef mdRef = new MdRef ();
	mdRef.read (r);

	return mdRef;
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
	    else if (name.equals ("XPTR")) {
		setXPTR (value);
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
	w.attribute ("MDTYPE", _MDTYPE.toString ());
	if (_OTHERMDTYPE != null) {
	    w.attribute ("OTHERMDTYPE", _OTHERMDTYPE);
	}
	if (_MIMETYPE != null) {
	    w.attribute ("MIMETYPE", _MIMETYPE);
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	if (_XPTR != null) {
	    w.attribute ("XPTR", _XPTR);
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
     * Validate this element with the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_LOCTYPE == null) {
	    throw new MetsException ("No mdRef LOCTYPE value");
	}
	else {
	    if (_LOCTYPE.toString ().equals ("OTHER")) {
		if (_OTHERLOCTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No mdRef OTHERLOCTYPE value");
		}
	    }
	    else {
		if (_OTHERLOCTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println ("mdRef OTHERLOCTYPE only valid " +
					     "with LOCTYPE=\"OTHER\"");
		}
	    }
	}

	if (_xlinkHref == null) {
	    throw new MetsException ("No value for mdRef xlink:href");
	}

	if (_MDTYPE == null) {
	    throw new MetsException ("No value for mdRef MDTYPE");
	}
	else {
	    if (_MDTYPE.toString ().equals ("OTHER")) {
		if (_OTHERMDTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No mdRef OTHERMDTYPE value");
		}
	    }
	    else {
		if (_OTHERMDTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println ("mdRef OTHERMDTYPE only valid " +
					     "with MDTYPE=\"OTHER\"");
		}
	    }
	}

	_valid = true;
    }
}
