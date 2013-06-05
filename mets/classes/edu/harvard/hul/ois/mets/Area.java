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
 * This class encapsulates the &lt;<tt>area</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.4.1 2006-04-03
 */
public class Area
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable
     * to this file. */
    private Map _ADMID;

    /** Beginning location. */
    private String _BEGIN;

    /** Being/end type. */
    private Betype _BETYPE;

    /** List of content IDs (equivalent to DIDL DIIs). */
    private List _CONTENTIDS;

    /** Visual coordinates within a two-dimensional area. */
    private String _COORDS;

    /** Ending location. */
    private String _END;
   
    /** Map of IDREF to the file element. */
    private Map _FILEID;

    /** Area extent. */
    private String _EXTENT;

    /** Extent type. */
    private Exttype _EXTTYPE;

    /** Area shape. */
    private Shape _SHAPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>Area</tt> object.
     */
    public Area ()
    {
	super ("area");

	_ADMID      = new HashMap ();
	_CONTENTIDS = new ArrayList ();
	_FILEID     = new HashMap ();
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
     * Return beginning location.
     * @return Beginning location
     */
    public String getBEGIN ()
    {
	return _BEGIN;
    }

    /**
     * Return area being/end type.
     * @return Begin/end type
     */
    public Betype getBETYPE ()
    {
	return _BETYPE;
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
     * Return area coordinates.
     * @return Coordinates
     */
    public String getCOORDS ()
    {
	return _COORDS;
    }

    /**
     * Return ending location.
     * @return Ending location
     */
    public String getEND ()
    {
	return _END;
    }

    /**
     * Return area extent
     * @return Extant
     */
    public String getEXTENT ()
    {
	return _EXTENT;
    }

    /**
     * Return area extent type.
     * @return Extent type
     */
    public Exttype getEXTTYPE ()
    {
	return _EXTTYPE;
    }

    /**
     * Return map of IDREF of file.
     * @return Map
     */
    public Map getFILEID ()
    {
	return _FILEID;
    }

    /**
     * Return area shape.
     * @return Area shape
     */
    public Shape getSHAPE ()
    {
	return _SHAPE;
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
     * Set area beginning location
     * @param BEGIN Beginning location
     */
    public void setBEGIN (String BEGIN)
    {
	_BEGIN = BEGIN;
    }

    /**
     * Set area begin/end type.
     * @param BETYPE Begin/end type
     */
    public void setBETYPE (Betype BETYPE)
    {
	_BETYPE = BETYPE;
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
     * Set area coordinates.
     * @param COORDS Area coordinates
     */
    public void setCOORDS (String COORDS)
    {
	_COORDS = COORDS;
    }

    /**
     * Set area ending location
     * @param END Ending location
     */
    public void setEND (String END)
    {
	_END = END;
    }

    /**
     * Set area extent
     * @param EXTANT Area extent
     */
    public void setEXTENT (String EXTENT)
    {
	_EXTENT = EXTENT;
    }

    /**
     * Set area extent type.
     * @param EXTTYPE Extent type
     */
    public void setEXTTYPE (Exttype EXTTYPE)
    {
	_EXTTYPE = EXTTYPE;
    }

    /**
     * Set IDREF of File.
     * @param IDREF File IDREF
     */
    public void setFILEID (String FILEID)
    {
	setFILEID (FILEID, null);
    }

    /**
     * Set IDREF of File, with referenced element.
     * The map is clear'ed first to ensure a single ID/IDREF pair.
     * @param IDREF File IDREF
     * @param file File element (or NULL)
     */
    public void setFILEID (String FILEID, File file)
    {
	_FILEID.clear ();
	_FILEID.put (FILEID, file);
	_valid = false;
    }

    /**
     * Set area shape.
     * @param SHAPE Area shape
     */
    public void setSHAPE (Shape SHAPE)
    {
	_SHAPE = SHAPE;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Area</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Area object
     * @throws MetsException De-serializing exception
     */
    public static Area reader (MetsReader r)
	throws MetsException
    {
	Area area = new Area ();
	area.read (r);

	return area;
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
	    else if (name.equals ("FILEID")) {
		setFILEID (value);
	    }
	    else if (name.equals ("SHAPE")) {
		setSHAPE (Shape.parse (value));
	    }
	    else if (name.equals ("COORDS")) {
		setCOORDS (value);
	    }
	    else if (name.equals ("BEGIN")) {
		setBEGIN (value);
	    }
	    else if (name.equals ("END")) {
		setEND (value);
	    }
	    else if (name.equals ("BETYPE")) {
		setBETYPE (Betype.parse (value));
	    }
	    else if (name.equals ("EXTENT")) {
		setEXTENT (value);
	    }
	    else if (name.equals ("EXTTYPE")) {
		setEXTTYPE (Exttype.parse (value));
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
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
	/*
	while (r.atStart ("area")) {
	    Area area = Area.reader (r);
	    _content.add (area);
	    }*/
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
	w.attributeName ("FILEID");
	Iterator iter = _FILEID.keySet ().iterator ();
	while (iter.hasNext ()) {
	    w.attributeValue ((String) iter.next ());
	}
	if (_SHAPE != null) {
	    w.attribute ("SHAPE", _SHAPE.toString ());
	}
	if (_COORDS != null) {
	    w.attribute ("COORDS", _COORDS);
	}
	if (_BEGIN != null) {
	    w.attribute ("BEGIN", _BEGIN);
	}
	if (_END != null) {
	    w.attribute ("END", _END);
	}
	if (_BETYPE != null) {
	    w.attribute ("BETYPE", _BETYPE.toString ());
	}
	if (_EXTENT != null) {
	    w.attribute ("EXTENT", _EXTENT);
	}
	if (_EXTTYPE != null) {
	    w.attribute ("EXTTYPE", _EXTTYPE.toString ());
	}
	if (!_ADMID.isEmpty ()) {
	    w.attributeName ("ADMID");
	    iter = _ADMID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
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
	if (_FILEID.isEmpty ()) {
	    throw new MetsException ("No FILEID value");
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
	if (!_FILEID.isEmpty ()) {
	    Iterator FILEID = _FILEID.keySet ().iterator ();
	    while (FILEID.hasNext ()) {
		String IDREF = (String) FILEID.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setFILEID (IDREF, (File) map.get (ID));
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
