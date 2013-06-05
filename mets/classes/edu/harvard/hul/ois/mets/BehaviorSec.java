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
 * This class encapsulates the &lt;<tt>behaviorSec</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-06
 */
public class BehaviorSec
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Creation date/time of this <tt>behaviorSec</tt>. */
    private Date _CREATED;

    /** Behavior label. */
    private String _LABEL;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>behaviorSec</tt> object.
     */
    public BehaviorSec ()
    {
	super ("behaviorSec");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return creation date/time of this <tt>behaviorSec</tt>.
     * @return Date/time
     */
    public Date getCREATED ()
    {
	return _CREATED;
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
     * Set creation date/time of this <tt>behaviorSec</tt>.
     * @param date Date/time
     */
    public void setCREATED (Date date)
    {
	_CREATED = date;
    }

    /**
     * Set behavior label.
     * @param label Label
     */
    public void setLABEL (String label)
    {
	_LABEL = label;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>BehaviorSec</tt> object de-serialized from the
     * input stream.
     * @param r Reader
     * @return BehaviourSec object
     * @throws MetsException De-serializing exception
     */
    public static BehaviorSec reader (MetsReader r)
	throws MetsException
    {
	BehaviorSec behaviorSec = new BehaviorSec ();
	behaviorSec.read (r);

	return behaviorSec;
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
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	while (r.atStart ("behaviorSec")) {
	    BehaviorSec behaviorSec = BehaviorSec.reader (r);
	    _content.add (behaviorSec);
	}
	while (r.atStart ("behavior")) {
	    Behavior behavior = Behavior.reader (r);
	    _content.add (behavior);
	}

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
	if (_CREATED != null) {
	    w.attribute ("CREATED", DateTime.format (_CREATED));
	}
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
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
}
