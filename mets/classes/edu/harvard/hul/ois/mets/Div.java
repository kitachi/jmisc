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
 * This class encapsulates the &lt;<tt>div</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.4.1 2006-03-31
 */
public class Div
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable to
     * this division. */
    private Map _ADMID;

    /** List of content IDs (equivalent to DIDL DIIs). */
    private List _CONTENTIDS;

    /** Map of IDREFS to descriptive metadata sections applicable to
     * this division. */
    private Map _DMDID;

    /** Existence flag for ORDER. */
    private boolean _hasORDER;

    /** Division label. */
    private String _LABEL;

    /** Integer representation of this division's sequential order. */
    private int _ORDER;

    /** String representation of this division's sequential order. */
    private String _ORDERLABEL;

    /** Division type. */
    private String _TYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>Div</tt> object.
     */
    public Div ()
    {
	super ("div");
	_ADMID      = new HashMap ();
	_CONTENTIDS = new ArrayList ();
	_DMDID      = new HashMap ();
	_hasORDER   = false;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return map of IDREFS to administrative metadata.
     * @return Map
     */
    public Map getADMID ()
    {
	return _ADMID;
    }

    /**
     * Return list of CONTENTIDS.
     * @return List
     */
    public List getCONTENTIDS ()
    {
	return _CONTENTIDS;
    }

    /**
     * Return map of IDREFS to descriptive metadata.
     * @return Map
     */
    public Map getDMDID ()
    {
	return _DMDID;
    }

    /**
     * Return division label.
     * @return Label
     */
    public String getLABEL ()
    {
	return _LABEL;
    }

    /**
     * Return division order in integer representation.
     * @return Order
     */
    public int getORDER ()
	throws MetsException
    {
	if (!_hasORDER) {
	    throw new MetsException ("ORDER is null");
	}

	return _ORDER;
    }

    /**
     * Return division order label in string representation.
     * @return Label
     */
    public String getORDERLABEL ()
    {
	return _ORDERLABEL;
    }

    /**
     * Return division type.
     * @return Type
     */
    public String getTYPE ()
    {
	return _TYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set IDREF of AmdSec element.
     * @param IDREF Element IDREF
     */
    public void setADMID (String ADMID)
    {
	StringTokenizer tokenizer = new StringTokenizer (ADMID);
	while (tokenizer.hasMoreTokens ()) {
	    setADMID (tokenizer.nextToken (), null);
	}
    }

    /**
     * Set IDREF of AmdSec element, with referenced element.
     * @param IDREF Element IDREF
     * @param elem Element (or NULL)
     */
    public void setADMID (String ADMID, MetsIDElement elem)
    {
	_ADMID.put (ADMID, elem);
	_valid = false;
    }

    /**
     * Set CONTENTIDS.  Note: Currently there is no encoding/decoding
     * being performed on the URIs.  They are assummed to already be
     * in appropriate form.
     * @param CONTENTIDS
     */
    public void setCONTENTIDS (String CONTENTIDS)
    {
	StringTokenizer tokenizer = new StringTokenizer (CONTENTIDS);
	while (tokenizer.hasMoreTokens ()) {
	    _CONTENTIDS.add (tokenizer.nextToken ());
	}
    }

    /**
     * Set IDREF of DmdSec element.
     * @param IDREF Element IDREF
     */
    public void setDMDID (String DMDID)
    {
	StringTokenizer tokenizer = new StringTokenizer (DMDID);
	while (tokenizer.hasMoreTokens ()) {
	    setDMDID (tokenizer.nextToken (), null);
	}
    }

    /**
     * Set IDREF of DmdSec element, with referenced element.
     * @param IDREF Element IDREF
     * @param elem Element (or NULL)
     */
    public void setDMDID (String DMDID, DmdSec elem)
    {
	_DMDID.put (DMDID, elem);
	_valid = false;
    }

    /**
     * Set division label.
     * @param LABEL Label
     */
    public void setLABEL (String LABEL)
    {
	_LABEL = LABEL;
    }

    /**
     * Set division order label with integer representation.
     * @param ORDER Order
     */
    public void setORDER (int ORDER)
    {
	_ORDER = ORDER;
	_hasORDER = true;
    }

    /**
     * Set division order label with string representation.
     * @param ORDERLABEL Label
     */
    public void setORDERLABEL (String ORDERLABEL)
    {
	_ORDERLABEL = ORDERLABEL;
    }

    /**
     * Set division type.
     * @param TYPE Type
     */
    public void setTYPE (String TYPE)
    {
	_TYPE = TYPE;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Div</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Div object
     * @throws MetsException De-serializing exception
     */
    public static Div reader (MetsReader r)
	throws MetsException
    {
	Div div = new Div ();
	div.read (r);

	return div;
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
	    String name  = attr.getLocalName ();
	    String value = attr.getValue ();

	    if (name.equals ("ID")) {
		setID (value);
	    }
	    else if (name.equals ("ORDER")) {
		setORDER (Integer.parseInt (value));
	    }
	    else if (name.equals ("ORDERLABEL")) {
		setORDERLABEL (value);
	    }
	    else if (name.equals ("LABEL")) {
		setLABEL (value);
	    }
	    else if (name.equals ("DMDID")) {
		setDMDID (value);
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
	    }
	    else if (name.equals ("TYPE")) {
		setTYPE (value);
	    }
	    else if (name.equals ("CONTENTIDS")) {
		setCONTENTIDS (value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	edu.harvard.hul.ois.mets.helper.parser.Type type = token.getType ();
	if (!type.equals (edu.harvard.hul.ois.mets.helper.parser.Type.EMPTY_TAG)) {
	    while (r.atStart ("mptr")) {
		Mptr mptr = Mptr.reader (r);
		_content.add (mptr);
	    }
	    while (r.atStart ("fptr")) {
		Fptr fptr = Fptr.reader (r);
		_content.add (fptr);
	    }
	    while (r.atStart ("div")) {
		Div div = Div.reader (r);
		_content.add (div);
	    }

	    if (r.atEnd (_localName)) {
		r.getEnd (_localName);
	    }
	}
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
	if (_hasORDER) {
	    w.attribute ("ORDER", String.valueOf (_ORDER));
	}
	if (_ORDERLABEL != null) {
	    w.attribute ("ORDERLABEL", _ORDERLABEL);
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	if (!_DMDID.isEmpty ()) {
	    w.attributeName ("DMDID");
	    Iterator iter = _DMDID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	if (!_ADMID.isEmpty ()) {
	    w.attributeName ("ADMID");
	    Iterator iter = _ADMID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	if (_TYPE != null) {
	    w.attribute ("TYPE", _TYPE);
	}
	if (!_CONTENTIDS.isEmpty ()) {
	    w.attributeName ("CONTENTIDS");
	    int len = _CONTENTIDS.size ();
	    for (int i=0; i<len; i++) {
		w.attributeValue ((String) _CONTENTIDS.get (i));
	    }
	}
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}

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
     * Validate ID/IDREF consistency with the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThisIDREFs (MetsValidator v)
	throws MetsException
    {
	if (!_ADMID.isEmpty ()) {
	    Iterator ADMID = _ADMID.keySet ().iterator ();
	    while (ADMID.hasNext ()) {
		String IDREF = (String) ADMID.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setADMID (IDREF, (MetsIDElement) map.get (ID));
			break;
		    }
		}
		if (!ok) {
		    throw new MetsException ("Can't resolve IDREF: " + IDREF);
		}
	    }
	}
	if (!_DMDID.isEmpty ()) {
	    Iterator DMDID = _DMDID.keySet ().iterator ();
	    while (DMDID.hasNext ()) {
		String IDREF = (String) DMDID.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setDMDID (IDREF, (DmdSec) map.get (ID));
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
