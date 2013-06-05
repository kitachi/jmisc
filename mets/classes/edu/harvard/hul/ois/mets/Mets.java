/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the Digital
 * Library Federation <http://www.diglib.org> and is maintained by the
 * Network Development and MARC Standards Office of the Library of
 * Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets;

import edu.harvard.hul.ois.mets.helper.*;
import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

/**
 * METS (Metadata Encoding and Transmission Standard) is intended to
 * provide a standardized XML format for transmission of complex digital
 * library objects between systems.
 *
 * This class encapsulates the document root &lt;<tt>mets</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 *
 * @author Stephen Abrams
 * @version 1.3.7 2005-07-28
 */
public class Mets
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Title/text string identifying the METS document. */
    private String _LABEL;

    /** Primary identifier assigned to the original source document. */
    private String _OBJID;

    /** Registered profile to which whis METS document conforms. */
    private String _PROFILE;

    /** METS object type. */
    private String _TYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>Mets</tt> object.
     */
    public Mets ()
    {
	super ("mets");
	init (false);
    }

    /**
     * Instantiate a new <tt>Mets</tt> object.
     * @param noSchema If true, suppress the METS, XLink, and XSI schemas
     */
    public Mets (boolean noSchema)
    {
	super ("mets");
	init (noSchema);
    }

    /**
     * Initialize a new <tt>Mets</tt> object.
     * @param noSchema If true, suppress the METS, XLink, and XSI schemas
     */
    private void init (boolean noSchema)
    {
	if (!noSchema) {
	    setSchema (null, "http://www.loc.gov/METS/",
		       "http://www.loc.gov/standards/mets/mets.xsd");
	    setSchema ("xlink", "http://www.w3.org/1999/xlink");
	    setSchema ("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	}
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return document label.
     * @return Label
     */
    public String getLABEL ()
    {
	return _LABEL;
    }

    /**
     * Return document object ID
     * @return Object ID
     */
    public String getOBJID ()
    {
	return _OBJID;
    }

    /**
     * Return document profile.
     * @return Profile
     */
    public String getPROFILE ()
    {
	return _PROFILE;
    }

    /**
     * Return document object type
     * @return Object type
     */
    public String getTYPE ()
    {
	return _TYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set document object label.
     * @param LABEL Object label
     */
    public void setLABEL (String LABEL)
    {
	_LABEL = LABEL;
    }

    /**
     * Set document object ID.
     * @param OBJID Object id
     */
    public void setOBJID (String OBJID)
    {
	_OBJID = OBJID;
    }

    /**
     * Set document object profile.
     * @param PROFILE Object profile
     */
    public void setPROFILE (String PROFILE)
    {
	_PROFILE = PROFILE;
    }

    /**
     * Set document object type.
     * @param TYPE Object type
     */
    public void setTYPE (String TYPE)
    {
	_TYPE = TYPE;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Mets</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Mets object
     * @throws MetsException De-serializing exception
     */
    public static Mets reader (MetsReader r)
	throws MetsException
    {
	return reader (r, false);
    }

    /**
     * Instantiate a <tt>Mets</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @param noSchema If true, suppress the METS, XLink, and XSI schemas
     * @return Mets object
     * @throws MetsException De-serializing exception
     */
    public static Mets reader (MetsReader r, boolean noSchema)
	throws MetsException
    {
	Mets mets = new Mets (noSchema);
	mets.read (r);

	return mets;
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
	    String name  = attr.getQName ();
	    String value = attr.getValue ();

	    if (name.equals ("ID")) {
		setID (value);
	    }
	    else if (name.equals ("OBJID")) {
		setOBJID (value);
	    }
	    else if (name.equals ("LABEL")) {
		setLABEL (value);
	    }
	    else if (name.equals ("TYPE")) {
		setTYPE (value);
	    }
	    else if (name.equals ("PROFILE")) {
		setPROFILE (value);
	    }
	    else if (name.equals ("xmlns") ||
		     name.equals ("xmlns:xlink") ||
		     name.equals ("xmlns:xsi")) {
		/* Do nothing; these namespaces are added automatically
		 * when the file is serialized.
		 */
	    }
	    else if (name.equals ("xsi:schemaLocation")) {
		/* Do nothing; this attribute is added automatically
		 * when the file is serialized.
		 */
	    }
	    else if (name.substring (0, 6).equals ("xmlns:")) {
		String prefix = name.substring (6);
		setSchema (prefix, value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	if (r.atStart ("metsHdr")) {
	    MetsHdr metsHdr = MetsHdr.reader (r);
	    _content.add (metsHdr);
	}
	while (r.atStart ("dmdSec")) {
	    DmdSec dmdSec = DmdSec.reader (r);
	    _content.add (dmdSec);
	}
	while (r.atStart ("amdSec")) {
	    AmdSec amdSec = AmdSec.reader (r);
	    _content.add (amdSec);
	}
	if (r.atStart ("fileSec")) {
	    FileSec fileSec = FileSec.reader (r);
	    _content.add (fileSec);
	}
	while (r.atStart ("structMap")) {
	    StructMap structMap = StructMap.reader (r);
	    _content.add (structMap);
	}
	while (r.atStart ("structLink")) {
	    StructLink structLink = StructLink.reader (r);
	    _content.add (structLink);
	}
	while (r.atStart ("behaviorSec")) {
	    BehaviorSec behaviorSec = BehaviorSec.reader (r);
	    _content.add (behaviorSec);
	}
	r.getEnd (_localName);
    }

    /**
     * Serialize the content of this element using the given writer.
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	write (w, false);
    }

    /**
     * Serialize the content of this element using the given writer.
     * @param w Writer
     * @param noXmlDecl If true, suppress the XML declaration
     */
    public void write (MetsWriter w, boolean noXmlDecl)
	throws MetsException
    {
	if (!noXmlDecl) {
	    w.declaration ();
	}
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	if (_OBJID != null) {
	    w.attribute ("OBJID", _OBJID);
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	if (_TYPE != null) {
	    w.attribute ("TYPE", _TYPE);
	}
	if (_PROFILE != null) {
	    w.attribute ("PROFILE", _PROFILE);
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

	w.flush ();
    }

    /******************************************************************
     * Validation methods.
     ******************************************************************/

    /**
     * Validate this element using the given validator.  Include
     * global validation of all IDREFs to IDs.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No mets structMap");
	}

	v.validateIDREFs (this);

	_valid = true;
    }
}
