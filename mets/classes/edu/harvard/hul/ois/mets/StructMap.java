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
 * This class encapsulates the &lt;<tt>structMap</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-06
 */
public class StructMap
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Descriptive label. */
    private String _LABEL;

    /** Map type, e.g., "LOGICAL" or "PHYSICAL". */
    private String _TYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>StructMap</tt> object.
     */
    public StructMap ()
    {
	super ("structMap");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return descriptive label.
     * @return Label
     */
    public String getLABEL ()
    {
	return _LABEL;
    }

    /**
     * Return map type
     * @return Map type
     */
    public String getTYPE ()
    {
	return _TYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set descriptive label.
     * @param LABEL Label
     */
    public void setLABEL (String LABEL)
    {
	_LABEL = LABEL;
    }

    /**
     * Set map type.
     * @param TYPE Map type
     */
    public void setTYPE (String TYPE)
    {
	_TYPE = TYPE;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>StructMap</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return StructMap object
     * @throws MetsException De-serializing exception
     */
    public static StructMap reader (MetsReader r)
	throws MetsException
    {
	StructMap structMap = new StructMap ();
	structMap.read (r);

	return structMap;
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
	    else if (name.equals ("TYPE")) {
		setTYPE (value);
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

	Div div = Div.reader (r);
	_content.add (div);

	r.getEnd (_localName);
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
	if (_LABEL != null) {
	    w.attribute ("LABEL", _LABEL);
	}
	if (_TYPE != null) {
	    w.attribute ("TYPE", _TYPE);
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
     * Validate this element using the given validator.
     * @param v Validator
     * @throws MetsException Validation exception
     */
    public void validateThis (MetsValidator v)
	throws MetsException
    {
	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No structMap div");
	}

	_valid = true;
    }
}
