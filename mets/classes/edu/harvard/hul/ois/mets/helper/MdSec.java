/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
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
 * This class encapsulates a metadata section element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-19
 */
public abstract class MdSec
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable
     * to this metadata. */
    private Map _ADMID;

    /** Date/time the metadata was created. */
    private Date _CREATED;

    /** Metadata group ID. */
    private String _GROUPID;

    /** Metadata status. */
    private String _STATUS;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>MdSec</tt> object.
     * @param qName Element QName
     */
    public MdSec (String qName)
    {
	super (qName);

	_ADMID = new HashMap ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return map IDREFS to administrative metadata.
     * @return Map
     */
    public Map getADMID ()
    {
	return _ADMID;
    }

    /**
     * Return metadata creation date/time.
     * @return Date/time
     */
    public Date getCREATED ()
    {
	return _CREATED;
    }

    /**
     * Return metadata group ID.
     * @return ID
     */
    public String getGROUPID ()
    {
	return _GROUPID;
    }

    /**
     * Return metadata status.
     * @return Status
     */
    public String getSTATUS ()
    {
	return _STATUS;
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
     * Set metadata creation date/time.
     * @param date Creation date/time
     */
    public void setCREATED (Date date)
    {
	_CREATED = date;
    }

    /**
     * Set metadata group ID.
     * @param id ID
     */
    public void setGROUPID (String id)
    {
	_GROUPID = id;
    }

    /**
     * Set metadata status.
     * @param status Status
     */
    public void setSTATUS (String status)
    {
	_STATUS = status;
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
	    else if (name.equals ("GROUPID")) {
		setGROUPID (value);
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
	    }
	    else if (name.equals ("CREATED")) {
		try {
		    setCREATED (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("STATUS")) {
		setSTATUS (value);
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
	if (r.atStart ("mdRef")) {
	    MdRef mdRef = MdRef.reader (r);
	    _content.add (mdRef);
	}
	if (r.atStart ("mdWrap")) {
	    MdWrap mdWrap = MdWrap.reader (r);
	    _content.add (mdWrap);
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
	if (_CREATED != null ) {
	    w.attribute ("CREATED", DateTime.format (_CREATED));
	}
	if (_STATUS != null) {
	    w.attribute ("STATUS", _STATUS);
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

			MetsVElement elem = (MetsVElement) map.get (ID);
			setADMID (IDREF, (MdWrap) map.get (ID));
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
