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
 * This class encapsulates the &lt;<tt>fileGrp</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.10 2006-03-31
 */
public class FileGrp
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Map of IDREFS to administrative metadata sections applicable
     * to this file group. */
    private Map _ADMID;

    /** Use flag. */
    private String _USE;

    /** Version date/time of this <tt>fileGrp</tt>. */
    private Date _VERSDATE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>FileGrp</tt> object.
     */
    public FileGrp ()
    {
	super ("fileGrp");

	_ADMID = new HashMap ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return map of IDREFS of administrative metadata.
     * @return Map
     */
    public Map getADMID ()
    {
	return _ADMID;
    }

    /**
     * Return use flag for this <tt>fileGrp</tt>.
     * @return Use
     */
    public String getUSE ()
    {
	return _USE;
    }

    /**
     * Return version date/time of this <tt>fileGrp</tt>.
     * @return Date/time
     */
    public Date getVERSDATE ()
    {
	return _VERSDATE;
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
     * Set use flag for this <tt>fileGrp</tt>.
     * @param use Use flag
     */
    public void setUSE (String use)
    {
	_USE = use;
    }

    /**
     * Set version date/time of this <tt>fileGrp</tt>.
     * @param date Date/time
     */
    public void setVERSDATE (Date date)
    {
	_VERSDATE = date;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>FileGrp</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return FileGrp object
     * @throws MetsException De-serializing exception
     */
    public static FileGrp reader (MetsReader r)
	throws MetsException
    {
	FileGrp fileGrp = new FileGrp ();
	fileGrp.read (r);

	return fileGrp;
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
	    else if (name.equals ("VERSDATE")) {
		try {
		    setVERSDATE (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("ADMID")) {
		setADMID (value);
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

	edu.harvard.hul.ois.mets.helper.parser.Type type = token.getType ();
	if (!type.equals (edu.harvard.hul.ois.mets.helper.parser.Type.EMPTY_TAG)) {
	    while (r.atStart ("fileGrp")) {
		FileGrp fileGrp = FileGrp.reader (r);
		_content.add (fileGrp);
	    }
	    while (r.atStart ("file")) {
		File file = File.reader (r);
		_content.add (file);
	    }

	    if (r.atEnd (_localName)) {
		r.getEnd (_localName);
	    }
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
	if (_VERSDATE != null) {
	    w.attribute ("VERSDATE", DateTime.format (_VERSDATE));
	}
	if (!_ADMID.isEmpty ()) {
	    w.attributeName ("ADMID");
	    Iterator iter = _ADMID.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
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
	    ((MetsElement) iter.next ()).write (w);
	}

	w.end (_qName);
    }
}
