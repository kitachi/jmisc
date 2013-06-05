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
 * This class encapsulates the &lt;<tt>smLink</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.1 2006-04-03
 */
public class SmLink
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREF of From attribute. */
    private Map _from;

    /** Map of IDREF of To attribute. */
    private Map _to;

    /** XLink actuate attribute. */
    private Actuate _xlinkActuate;

    /** XLink arcrole attribute. */
    private String _xlinkArcrole;

    /** XLink from attribute. */
    private String _xlinkFrom;

    /** XLink show attribute; pre-defined show behaviors. */
    private Show _xlinkShow;

    /** XLink title attribute. */
    private String _xlinkTitle;

    /** XLink to attribute. */
    private String _xlinkTo;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>smLink</tt>&gt; object.
     */
    public SmLink ()
    {
	super ("smLink");

	_from = new HashMap ();
	_to   = new HashMap ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return map of IDREF of "from" attribute.
     * @return From attribute
     */
    public Map getFrom ()
    {
	return _from;
    }

    /**
     * Return map of IDREF of "to" attribute.
     * @return Map
     */
    public Map getTo ()
    {
	return _to;
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
     * Return pre-defined XLink show attribute.
     * @return Show
     */
    public Show getXlinkShow ()
    {
	return _xlinkShow;
    }

    /**
     * Return pre-defined XLink from attribute.
     * @return From
     */
    public String getXlinkFrom ()
    {
	return _xlinkFrom;
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
     * Return pre-defined XLink to attribute.
     * @return To
     */
    public String getXlinkTo ()
    {
	return _xlinkTo;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set IDREF of "from" attribute StructMap element.
     * @param from IDREF of element
     */
    public void setFrom (String from)
    {
	setFrom (from, null);
    }

    /**
     * Set IDREF of StructMap element, with referenced element.
     * The map is clear'ed first to ensure a single ID/IDREF pair.
     * @param from IDREF of element
     * @param element Element (or NULL)
     */
    public void setFrom (String from, MetsIDElement element)
    {
	_from.clear ();
	_from.put (from, element);
	_valid = false;
    }

    /**
     * Set IDREF of "to" attribute StructMap element.
     * @param to IDREF of element
     */
    public void setTo (String to)
    {
	setTo (to, null);
    }

    /**
     * Set IDREF of StructMap element, with referenced element.
     * The map is clear'ed first to ensure a single ID/IDREF pair.
     * @param to IDREF of element
     * @param element Element (or NULL)
     */
    public void setTo (String to, MetsIDElement element)
    {
	_to.clear ();
	_to.put (to, element);
	_valid = false;
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
     * Set pre-defined Xlink from attribute.
     * @param from From
     */
    public void setXlinkFrom (String from)
    {
	_xlinkFrom = from;
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
     * Set pre-defined Xlink to attribute.
     * @param to To
     */
    public void setXlinkTo (String to)
    {
	_xlinkTo = to;
    }

    /******************************************************************
     * Marshalling methods.
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>SmLink</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return SmLink object
     * @throws MetsException De-serializing exception
     */
    public static SmLink reader (MetsReader r)
	throws MetsException
    {
	SmLink smLink = new SmLink ();
	smLink.read (r);

	return smLink;
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
	    else if (name.equals ("to")) {
		setTo (value);
	    }
	    else if (qName.equals ("xlink:to")) {
		setXlinkTo (value);
	    }
	    else if (name.equals ("from")) {
		setFrom (value);
	    }
	    else if (qName.equals ("xlink:from")) {
		setXlinkFrom (value);
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
     * Serialize the content model of this element using the given
     * writer
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
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
	if (_xlinkTo != null) {
	    w.attribute ("xlink:to", _xlinkTo);
	}
	else {
	    w.attributeName ("to");
	    Iterator iter = _to.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	if (_xlinkFrom != null) {
	    w.attribute ("xlink:from", _xlinkFrom);
	}
	else {
	    w.attributeName ("from");
	    Iterator iter = _from.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
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
     * Validate this element and its content model  using the given
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_to.isEmpty () && _xlinkTo == null) {
	    if (_xlinkTo == null) {
		throw new MetsException ("No smLink xlink:to value");
	    }
	    else {
		throw new MetsException ("No smLink to value");
	    }
	}

	if (_from.isEmpty () && _xlinkFrom == null) {
	    if (_xlinkFrom == null) {
		throw new MetsException ("No smLink xlink:from value");
	    }
	    else {
		throw new MetsException ("No smLink from value");
	    }
	}

	_valid = true;
    }

    /**
     * Validate ID/IDREF consistency with the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThisIDREFs (MetsValidator v)
	throws MetsException
    {
	if (!_from.isEmpty ()) {
	    Iterator from = _from.keySet ().iterator ();
	    while (from.hasNext ()) {
		String IDREF = (String) from.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setFrom (IDREF, (MetsIDElement) map.get (ID));
			break;
		    }
		}
		if (!ok) {
		    throw new MetsException ("Can't resolve IDREF: " + IDREF);
		}
	    }
	}
	if (!_to.isEmpty ()) {
	    Iterator to = _to.keySet ().iterator ();
	    while (to.hasNext ()) {
		String IDREF = (String) to.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setTo (IDREF, (MetsIDElement) map.get (ID));
			break;
		    }
		}
		if (!ok) {
		    throw new MetsException ("Can't resolve IDREF: " + IDREF);
		}
	    }
	}
    }
}
