/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>metsHdr</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-06
 */
public class MetsHdr
    extends MetsIDElement
{

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Date/time the METS document was created. */
    private Date _CREATEDATE;

    /** Date/time the METS document was last modified. */
    private Date _LASTMODDATE;

    /** Status of the METS document. */
    private String _RECORDSTATUS;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>MetsHdr</tt> object.
     */
    public MetsHdr ()
    {
	super ("metsHdr");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return create date/time.
     * @return Date/time
     */
    public Date getCREATEDATE ()
    {
	return _CREATEDATE;
    }

    /**
     * Return last modification date/time.
     * @return Date/time
     */
    public Date getLASTMODDATE ()
    {
	return _LASTMODDATE;
    }

    /**
     * Return record status.
     * @return Status
     */
    public String getRECORDSTATUS ()
    {
	return _RECORDSTATUS;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set create date/time.
     * @param date Date/time
     */
    public void setCREATEDATE (Date date)
    {
	_CREATEDATE = date;
    }

    /**
     * Set last modification date/time.
     * @param date Date/time
     */
    public void setLASTMODDATE (Date date)
    {
	_LASTMODDATE = date;
    }

    /**
     * Set record status.
     * @param status Status
     
     */
    public void setRECORDSTATUS (String status)
    {
	_RECORDSTATUS = status;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>MetsHdr</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return MetsHdr object
     * @throws MetsException De-serializing exception
     */
    public static MetsHdr reader (MetsReader r)
	throws MetsException
    {
	MetsHdr metsHdr = new MetsHdr ();
	metsHdr.read (r);

	return metsHdr;
    }

    /**
     * De-serialize the content of the file using the given reader.
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
	    else if (name.equals ("CREATEDATE")) {
		try {
		    setCREATEDATE (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("LASTMODDATE")) {
		try {
		    setLASTMODDATE (DateTime.parse (value));
		}
		catch (Exception e) {
		    throw new MetsException (e.toString ());
		}
	    }
	    else if (name.equals ("RECORDSTATUS")) {
		setRECORDSTATUS (value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	while (r.atStart ("agent")) {
	    Agent agent = Agent.reader (r);
	    _content.add (agent);
	}
	while (r.atStart ("altRecordID")) {
	    AltRecordID id = AltRecordID.reader (r);
	    _content.add (id);
	}

	if (r.atEnd (_localName)) {
	    r.getEnd (_localName);
	}
    }

    /**
     * Serialize the content of this element using the given writer.
     * @param w Writer
     * @throws MetsException I/O exception
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	if (_CREATEDATE != null) {
	    w.attribute ("CREATEDATE", DateTime.format (_CREATEDATE));
	}
	if (_LASTMODDATE != null) {
	    w.attribute ("LASTMODDATE", DateTime.format (_LASTMODDATE));
	}
	if (_RECORDSTATUS != null) {
	    w.attribute ("RECORDSTATUS", _RECORDSTATUS);
	}
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}

	if (_content != null) {
	    Iterator iter = _content.iterator ();
	    while (iter.hasNext ()) {
		((MetsElement) iter.next ()).write (w);
	    }
	}

	w.end (_qName); 
    }
}
