/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>amdSec</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-19
 */
public class AmdSec
    extends MetsIDElement
{
    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>AmdSec</tt> object.
     */
    public AmdSec ()
    {
	super ("amdSec");
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>AmdSec</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return AmdSec object
     * @throws MetsException De-serializing exception
     */
    public static AmdSec reader (MetsReader r)
	throws MetsException
    {
	AmdSec amdSec = new AmdSec ();
	amdSec.read (r);

	return amdSec;
    }

    /**
     * De-serialize the content of the amd into this element using
     * the given reader.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
	Token token = r.getStart (_localName);
	_qName = token.getLocalName ();

	Attributes attrs = token.getAttributes ();
	if (attrs.hasNext ()) {
	    Attribute attr = attrs.next ();
	    String qName = attr.getQName ();
	    String name  = attr.getLocalName ();
	    String value = attr.getValue ();

	    if (name.equals ("ID")) {
		setID (value);
	    }
	    else if  (qName.substring (0, 6).equals ("xmlns:")) {
		String prefix = qName.substring (6);
		setSchema (prefix, value);
	    }
	}

	while (r.atStart ("techMD")) {
	    _content.add (TechMD.reader (r));
	}
	while (r.atStart ("rightsMD")) {
	    _content.add (RightsMD.reader (r));
	}
	while (r.atStart ("sourceMD")) {
	    _content.add (SourceMD.reader (r));
	}
	while (r.atStart ("digiprovMD")) {
	    _content.add (DigiprovMD.reader (r));
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
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	writeSchemas (w);

	Iterator iter = _content.iterator ();
	while (iter.hasNext ()) {
	    ((MetsElement) iter.next ()).write (w);
	}

	w.end (_qName);
    }
}
