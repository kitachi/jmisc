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
 * This class encapsulates the &lt;<tt>fptr</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.4.1 2006-03-31
 */
public class Fptr
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** List of content IDs (equivalent to DIDL DIIs). */
    private List _CONTENTIDS;

    /** Map of IDREF to File. */
    private Map _FILEID;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new &lt;<tt>fptr</tt>&gt; object.
     */
    public Fptr ()
    {
	super ("fptr");

	_CONTENTIDS = new ArrayList ();
	_FILEID     = new HashMap ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return list of CONTENTIDS.
     * @return List
     */
    public List getCONTENTIDS ()
    {
	return _CONTENTIDS;
    }

    /**
     * Return map of IDREF to File.
     * @return Map
     */
    public Map getFILEID ()
    {
	return _FILEID;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

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
     * Set IDREF of File.
     * @param FILEID File IDREF
     */
    public void setFILEID (String FILEID)
    {
	setFILEID (FILEID, null);
    }

    /**
     * Set IDREF of File, with referenced element.
     * The map is clear'ed first to ensure a single ID/IDREF pair.
     * @param FILEID File IDREF
     * @param file File element (or NULL)
     */
    public void setFILEID (String FILEID, File file)
    {
	_FILEID.clear ();
	_FILEID.put (FILEID, file);
	_valid = false;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Fptr</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Fptr object
     * @throws MetsException De-serializing exception
     */
    public static Fptr reader (MetsReader r)
	throws MetsException
    {
	Fptr fptr = new Fptr ();
	fptr.read (r);

	return fptr;
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

	while (r.atStart ("par")) {
	    Par par = Par.reader (r);
	    _content.add (par);
	}
	while (r.atStart ("seq")) {
	    Seq seq = Seq.reader (r);
	    _content.add (seq);
	}
	while (r.atStart ("area")) {
	    Area area = Area.reader (r);
	    _content.add (area);
	}

	if (r.atEnd (_localName)) {
	    r.getEnd (_localName);
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
	if (!_FILEID.isEmpty ()) {
	    w.attributeName ("FILEID");
	    Iterator iter = _FILEID.keySet ().iterator ();
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
     * Validate ID/IDREF consistency using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThisIDREFs (MetsValidator v)
	throws MetsException
    {
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
