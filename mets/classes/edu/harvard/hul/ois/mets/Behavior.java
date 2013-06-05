/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2004 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>behavior</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.8 2005-09-23
 */
public class Behavior
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to <tt>AmdSec</tt> sections applicable to this
	behavior. */
    private Map _ADMID;

    /** Behavior type. */
    private String _BTYPE;

    /** Creation date/time of this <tt>behavior</tt>. */
    private Date _CREATED;

    /** Behavior group ID. */
    private String _GROUPID;

    /** Behavior label. */
    private String _LABEL;

    /** Map of IDREFS to <tt>structMap</tt> or <tt>div</tt> sections
	applicable to this behavior. */
    private Map _STRUCTID;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>behavior</tt> object.
     */
    public Behavior ()
    {
	super ("behavior");

	_ADMID    = new HashMap ();
	_STRUCTID = new HashMap ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return map of IDREFS to <tt>amdSec</tt>s.
     * @return Map
     */
    public Map getADMID ()
    {
	return _ADMID;
    }

    /**
     * Return behavior type.
     * @return Type
     */
    public String getBTYPE ()
    {
	return _BTYPE;
    }

    /**
     * Return map of IDREFS to <tt>structMap</tt> and <tt>div</tt>s.
     * @return Map
     */
    public Map getSTRUCTID ()
    {
	return _STRUCTID;
    }

    /**
     * Return creation date/time of this <tt>behavior</tt>.
     * @return Date/time
     */
    public Date getCREATED ()
    {
	return _CREATED;
    }

    /**
     * Return behavior group ID.
     * @return Group ID
     */
    public String getGROUPID ()
    {
	return _GROUPID;
    }

    /**
     * Return behavior label.
     * @param Label
     */
    public String getLABEL ()
    {
	return _LABEL;
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
     * Set behavior type.
     * @param type Type
     */
    public void setBTYPE (String type)
    {
	_BTYPE = type;
    }

    /**
     * Set creation date/time of this <tt>behavior</tt>.
     * @param date Date/time
     */
    public void setCREATED (Date date)
    {
	_CREATED = date;
    }

    /**
     * Set behavior group ID.
     * @param id Group ID
     */
    public void setGROUPID (String id)
    {
	_GROUPID = id;
    }

    /**
     * Set behavior label.
     * @param label Label
     */
    public void setLABEL (String label)
    {
	_LABEL = label;
    }

    /**
     * Set IDREF of StructMap.
     * @param IDREF StructMap IDREF
     */
    public void setSTRUCTID (String STRUCTID)
    {
	StringTokenizer tokenizer = new StringTokenizer (STRUCTID);
	while (tokenizer.hasMoreTokens ()) {
	    setSTRUCTID (tokenizer.nextToken (), null);
	}
    }

    /**
     * Set IDREF of StructMap element, with referenced element.
     * @param IDREF StructMap element IDREF
     * @param structMap elem Element (or NULL)
     */
    public void setSTRUCTID (String STRUCTID, MetsIDElement elem)
    {
	_STRUCTID.put (STRUCTID, elem);
	_valid = false;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Behavior</tt> object de-serialized from the
     * input stream.
     * @param r Reader
     * @return Behaviour object
     * @throws MetsException De-serializing exception
     */
    public static Behavior reader (MetsReader r)
	throws MetsException
    {
	Behavior behavior = new Behavior ();
	behavior.read (r);

	return behavior;
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
	    else if (name.equals ("STRUCTID")) {
		setSTRUCTID (value);
	    }
	    else if (name.equals ("BTYPE")) {
		setBTYPE (value);
	    }
	    else if (name.equals ("CREATED")) {
		try {
		    setCREATED (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("LABEL")) {
		setLABEL (value);
	    }
	    else if (name.equals ("GROUPID")) {
		setGROUPID (value);
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	if (r.atStart ("interfaceDef")) {
	    InterfaceDef interfaceDef = InterfaceDef.reader (r);
	    _content.add (interfaceDef);
	}
	Mechanism mechanism = Mechanism.reader (r);
	_content.add (mechanism);

	r.getEnd (_localName);
    }

    /**
     * Serialize this element and its content model using the given
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
	if (!_STRUCTID.isEmpty ()) {
	    w.attributeName ("STRUCTID");
	    Iterator iter = _STRUCTID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	if (_BTYPE != null) {
	    w.attribute ("BTYPE", _BTYPE);
	}
	if (_CREATED != null) {
	    w.attribute ("CREATED", DateTime.format (_CREATED));
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	if (_GROUPID != null) {
	    w.attribute ("GROUPID", _GROUPID);
	}
	if (!_ADMID.isEmpty ()) {
	    w.attributeName ("ADMID");
	    Iterator iter = _ADMID.keySet ().iterator ();
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
     * Validate this element and its content model using the given
     * validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {

	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No behavior mechanism");
	}

	_valid = true;
    }

    /**
     * Validate ID/IDREF consistency using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThisIDREFs (MetsValidator v)
	throws MetsException
    {
	if (!_STRUCTID.isEmpty ()) {
	    Iterator STRUCTID = _STRUCTID.keySet ().iterator ();
	    while (STRUCTID.hasNext ()) {
		String IDREF = (String) STRUCTID.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setSTRUCTID (IDREF, (MetsIDElement) map.get (ID));
			break;
		    }
		}
		if (!ok) {
		    throw new MetsException ("Can't resolve IDREF: " + IDREF);
		}
	    }
	}

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
    }
}
