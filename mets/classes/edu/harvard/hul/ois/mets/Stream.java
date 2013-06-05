/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2006 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>stream</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.1 2006-04-03
 */
public class Stream
    extends MetsVElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable
     * to this stream. */
    private Map _ADMID;

    /** Map of IDREFS to descriptive metadata sections applicable
     * to this stream. */
    private Map _DMDID;

    /** Stream OWNERID. */
    private String _OWNERID;

    /** Stream type. */
    private String _streamType;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>Stream</tt> object.
     */
    public Stream ()
    {
	super ("stream");

	_ADMID   = new HashMap ();
	_DMDID   = new HashMap ();
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
     * Return map IDREFS to descriptive metadata.
     * @return Map
     */
    public Map getDMDID ()
    {
	return _DMDID;
    }

    /**
     * Return stream OWNERID.
     * @return OWNERID
     */
    public String getOWNERID ()
    {
	return _OWNERID;
    }

    /**
     * Return stream type.
     * @return Stream type
     */
    public String getStreamType ()
    {
	return _streamType;
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
     * Set stream OWNERID.
     * @param id OWNERID
     */
    public void setOWNERID (String id)
    {
	_OWNERID = id;
    }

    /**
     * Set stream type.
     * @param type Stream type
     */
    public void setStreamType (String type)
    {
	_streamType = type;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Stream</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Stream object
     * @throws MetsException De-serializing exception
     */
    public static Stream reader (MetsReader r)
	throws MetsException
    {
	Stream stream = new Stream ();
	stream.read (r);

	return stream;
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

	    if (name.equals ("streamType")) {
		setStreamType (value);
	    }
	    else if (name.equals ("OWNERID")) {
		setOWNERID (value);
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
	    }
	    else if (name.equals ("DMDID")) {
		setDMDID (value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	while (r.atStart ()) {
	    Any any = Any.reader (r);
	    _content.add (any);
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
	if (_streamType != null) {
	    w.attribute ("streaType", _streamType);
	}
	if (_OWNERID != null) {
	    w.attribute ("OWNERID", _OWNERID);
	}
	if (!_ADMID.isEmpty ()) {
	    w.attributeName ("ADMID");
	    Iterator iter = _ADMID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	if (!_DMDID.isEmpty ()) {
	    w.attributeName ("DMDID");
	    Iterator iter = _DMDID.keySet ().iterator ();
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
