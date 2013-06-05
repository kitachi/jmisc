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
 * This class encapsulates the &lt;<tt>file</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.1 2006-03-31
 */
public class File
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable
     * to this file. */
    private Map _ADMID;

    /** File checksum. */
    private String _CHECKSUM;

    /** File checksum type. */
    private Checksumtype _CHECKSUMTYPE;

    /** Date/time this <tt>file</tt> was created. */
    private Date _CREATED;

    /** Map of IDREFS to descriptive metadata sections applicable
     * to this file. */
    private Map _DMDID;

    /** File group ID. */
    private String _GROUPID;

    /** Value flag for SEQ attribute. */
    private boolean _hasSEQ;

    /** Value flag for SIZE attribute. */
    private boolean _hasSIZE;

    /** File MIME type. */
    private String _MIMETYPE;

    /** File owner-specific ID. */
    private String _OWNERID;

    /** File integer sequence number, relative to the parent
     * <tt>fileGrp</tt>. */
    private int _SEQ;

    /** File size in bytes. */
    private long _SIZE;

    /** Use flag. */
    private String _USE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>File</tt> object.
     */
    public File ()
    {
	super ("file");

	_ADMID   = new HashMap ();
	_DMDID   = new HashMap ();
	_hasSEQ  = false;
	_hasSIZE = false;
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
     * Return file checksum.
     * @return Checksum
     */
    public String getCHECKSUM ()
    {
	return _CHECKSUM;
    }

    /**
     * Return file checksum type.
     * @return Checksum type
     */
    public Checksumtype getCHECKSUMTYPE ()
    {
	return _CHECKSUMTYPE;
    }

    /**
     * Return file creation date/time.
     * @return Date/time
     */
    public Date getCREATED ()
    {
	return _CREATED;
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
     * Return file group ID.
     * @return Group ID
     */
    public String getGROUPID ()
    {
	return _GROUPID;
    }

    /**
     * Return file MIME type.
     * @return MIME type
     */
    public String getMIMETYPE ()
    {
	return _MIMETYPE;
    }

    /**
     * Return file owner-specific ID.
     * @return Owner ID
     */
    public String getOWNERID ()
    {
	return _OWNERID;
    }

    /**
     * Return file sequence number in this <tt>fileGrp</tt>.
     * @return Sequence number
     * @throws MetsException If SEQ has not been set
     */
    public int getSEQ ()
	throws MetsException
    {
	if (!_hasSEQ) {
	    throw new MetsException ("No file SEQ value");
	}

	return _SEQ;
    }

    /**
     * Return file size in bytes.
     * @return File size
     * @throws MetsException If SIZE has not been set
     */
    public long getSIZE ()
	throws MetsException
    {
	if (!_hasSIZE) {
	    throw new MetsException ("No file SIZE value");
	}

	return _SIZE;
    }

    /**
     * Return use flag for this <tt>fileGrp</tt>.
     * @return Use
     */
    public String getUSE ()
    {
	return _USE;
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
     * Set file checksum.
     * @param checksum Checksum
     */
    public void setCHECKSUM (String checksum)
    {
	_CHECKSUM = checksum;
    }

    /**
     * Set file checksum type.
     * @param type Checksum type
     */
    public void setCHECKSUMTYPE (Checksumtype type)
    {
	_CHECKSUMTYPE = type;
    }

    /**
     * Set file creation date/time.
     * @param date Creation date/time
     */
    public void setCREATED (Date date)
    {
	_CREATED = date;
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
     * Set file group ID.
     * @param id Group ID
     */
    public void setGROUPID (String id)
    {
	_GROUPID = id;
    }

    /**
     * Set file MIME type.
     * @param mime MIME type
     */
    public void setMIMETYPE (String mime)
    {
	_MIMETYPE = mime;
    }

    /**
     * Set file owner-specific ID.
     * @param id Owner ID
     */
    public void setOWNERID (String id)
    {
	_OWNERID = id;
    }

    /**
     * Set file sequence order in this <tt>fileGrp</tt>.
     * @param seq Sequence order
     */
    public void setSEQ (int seq)
    {
	_SEQ    = seq;
	_hasSEQ = true;
    }

    /**
     * Set file size in bytes.
     * @param size File size
     */
    public void setSIZE (long size)
    {
	_SIZE    = size;
	_hasSIZE = true;
    }

    /**
     * Set use flag for this <tt>fileGrp</tt>.
     * @param use Use flag
     */
    public void setUSE (String use)
    {
	_USE = use;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>File</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return File object
     * @throws MetsException De-serializing exception
     */
    public static File reader (MetsReader r)
	throws MetsException
    {
	File file = new File ();
	file.read (r);

	return file;
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
	    else if (name.equals ("MIMETYPE")) {
		setMIMETYPE (value);
	    }
	    else if (name.equals ("SEQ")) {
		setSEQ (Integer.parseInt (value));
	    }
	    else if (name.equals ("SIZE")) {
		setSIZE (Long.parseLong (value));
	    }
	    else if (name.equals ("CREATED")) {
		try {
		    setCREATED (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("CHECKSUM")) {
		setCHECKSUM (value);
	    }
	    else if (name.equals ("CHECKSUMTYPE")) {
		setCHECKSUMTYPE (Checksumtype.parse (value));
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
	    else if (name.equals ("GROUPID")) {
		setGROUPID (value);
	    }
	    else if (name.equals ("USE")) {
		setUSE (value);
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

	while (r.atStart ("FLocat")) {
	    FLocat fLocat = FLocat.reader (r);
	    _content.add (fLocat);
	}
	if (r.atStart ("FContent")) {
	    FContent fContent= FContent.reader (r);
	    _content.add (fContent);
	}
	while (r.atStart ("stream")) {
	    Stream stream = Stream.reader (r);
	    _content.add (stream);
	}
	while (r.atStart ("transformFile")) {
	    TransformFile transformFile = TransformFile.reader (r);
	    _content.add (transformFile);
	}
	while (r.atStart ("file")) {
	    File file = File.reader (r);
	    _content.add (file);
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
	w.attribute ("ID", _ID);
	if (_MIMETYPE != null) {
	    w.attribute ("MIMETYPE", _MIMETYPE);
	}
	if (_hasSEQ) {
	    w.attribute ("SEQ", String.valueOf (_SEQ));
	}
	if (_hasSIZE) {
	    w.attribute ("SIZE", String.valueOf (_SIZE));
	}
	if (_CREATED != null ) {
	    w.attribute ("CREATED", DateTime.format (_CREATED));
	}
	if (_CHECKSUM != null) {
	    w.attribute ("CHECKSUM", _CHECKSUM);
	}
	if (_CHECKSUMTYPE != null) {
	    w.attribute ("CHECKSUMTYPE", _CHECKSUMTYPE.toString ());
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
	if (_GROUPID != null) {
	    w.attribute ("GROUPID", _GROUPID);
	}
	if (_USE != null) {
	    w.attribute ("USE", _USE);
	}
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}
	writeSchemas (w);

	Iterator iter = _content.iterator ();
	while (iter.hasNext ()) {
	    ((MetsSerializable) iter.next ()).write (w);
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
	if (_ID == null) {
	    throw new MetsException ("No file ID value");
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
