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
 * This class encapsulates the &lt;<tt>transformFile</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.5.1 2006-04-03
 */
public class TransformFile
    extends MetsVElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Value flag for TRANSFORMORDER attribue. */
    private boolean _hasTRANSFORMORDER;

    /** Transformation algorithm. */
    private String _TRANSFORMALGORITHM;

    /** Map of IDREF to a transformation behavior. */
    private Map _TRANSFORMBEHAVIOR;

    /** Transformation key. */
    private String _TRANSFORMKEY;

    /** Transformation order. */
    private int _TRANSFORMORDER;

    /** Transformation type. */
    private Transformtype _TRANSFORMTYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a new <tt>TransformFile</tt> object.
     */
    public TransformFile ()
    {
	super ("transformFile");

	_TRANSFORMBEHAVIOR = new HashMap ();
	_hasTRANSFORMORDER = false;
    }

    /******************************************************************
     * Public INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return transformation algorithm.
     * @return Algorithm
     */
    public String getTRANSFORMALGORITHM ()
    {
	return _TRANSFORMALGORITHM;
    }

    /**
     * Return map of IDREF to transformation behavior.
     * @return Map
     */
    public Map getTRANSFORMBEHAVIOR ()
    {
	return _TRANSFORMBEHAVIOR;
    }

    /**
     * Return transformation key.
     * @return Key
     */
    public String getTRANSFORMKEY ()
    {
	return _TRANSFORMKEY;
    }

    /**
     * Return transformation order.
     * @return Order
     */
    public int getTRANSFORMORDER ()
	throws MetsException
    {
	if (_hasTRANSFORMORDER) {
	    throw new MetsException ("No transformFile TRANSFORMORDER value");
	}

	return _TRANSFORMORDER;
    }

    /**
     * Return transformation type.
     * @return Type
     */
    public Transformtype getTRANSFORMTYPE ()
    {
	return _TRANSFORMTYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Transformation algorithm.
     * @param algorithm Algorithm
     */
    public void setTRANSFORMALGORITHM (String algorithm)
    {
	_TRANSFORMALGORITHM = algorithm;
    }

    /**
     * Set IDREF of Behavior element.
     * @param IDREF Behavior IDREF
     */
    public void setTRANSFORMBEHAVIOR (String TRANSFORMBEHAVIOR)
    {
	setTRANSFORMBEHAVIOR (TRANSFORMBEHAVIOR, null);
    }

    /**
     * Set IDREF of Behavior element, with referenced element.
     * The map is clear'ed first to ensure a single ID/IDREF pair.
     * @param IDREF Behavior IDREF
     * @param behavior Behavior element (or NULL)
     */
    public void setTRANSFORMBEHAVIOR (String TRANSFORMBEHAVIOR,
				      Behavior behavior)
    {
	_TRANSFORMBEHAVIOR.clear ();
	_TRANSFORMBEHAVIOR.put (TRANSFORMBEHAVIOR, behavior);
	_valid = false;
    }

    /**
     * Transformation key.
     * @param key Key
     */
    public void setTRANSFORMKEY (String key)
    {
	_TRANSFORMKEY = key;
    }

    /**
     * Transformation order.
     * @param order Order
     */
    public void setTRANSFORMORDER (int order)
    {
	_TRANSFORMORDER = order;
	_hasTRANSFORMORDER = true;
    }

    /**
     * Transformation type.
     * @param type Type
     */
    public void setTRANSFORMTYPE (Transformtype type)
    {
	_TRANSFORMTYPE = type;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>TransformFile</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return TransformFile object
     * @throws MetsException De-serializing exception
     */
    public static TransformFile reader (MetsReader r)
	throws MetsException
    {
	TransformFile transformFile = new TransformFile ();
	transformFile.read (r);

	return transformFile;
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

	    if (name.equals ("TRANSFORMTYPE")) {
		setTRANSFORMTYPE (Transformtype.parse (value));
	    }
	    else if (name.equals ("TRANSFORMALGORITHM")) {
		setTRANSFORMALGORITHM (value);
	    }
	    else if (name.equals ("TRANSFORMKEY")) {
		setTRANSFORMKEY (value);
	    }
	    else if (name.equals ("TRANSFORMBEHAVIOR")) {
		setTRANSFORMBEHAVIOR (value);
	    }
	    else if (name.equals ("TRANSFORMORDER")) {
		setTRANSFORMORDER (Integer.parseInt (value));
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
	w.attribute ("TRANSFORMTYPE", _TRANSFORMTYPE.toString ());
	w.attribute ("TRANSFORMALGORITHM", _TRANSFORMALGORITHM);
	if (_TRANSFORMKEY != null) {
	    w.attribute ("TRANSFORMKEY", _TRANSFORMKEY);
	}
	if (!_TRANSFORMBEHAVIOR.isEmpty ()) {
	    w.attributeName ("TRANSFORMBEHAVIOR");
	    Iterator iter = _TRANSFORMBEHAVIOR.keySet ().iterator ();
	    while (iter.hasNext ()) {
		w.attributeValue ((String) iter.next ());
	    }
	}
	w.attribute ("TRANSFORMORDER", String.valueOf (_TRANSFORMORDER));
	if (_attrs != null) {
	    _attrs.reset ();
	    while (_attrs.hasNext ()) {
		Attribute attr = _attrs.next ();
		w.attribute (attr.getQName (), attr.getValue ());
	    }
	}

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
	if (_TRANSFORMALGORITHM == null) {
	    throw new MetsException ("No transformFile TRANSFORMALGORITHM " +
				     "value");
	}
	if (!_hasTRANSFORMORDER) {
	    throw new MetsException ("No transformFile TRANSFORMORDER value");
	}
	if (_TRANSFORMTYPE == null) {
	    throw new MetsException ("No transformFile TRANSFORMTYPE value");
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
	if (!_TRANSFORMBEHAVIOR.isEmpty ()) {
	    Iterator TRANSFORMBEHAVIOR =
		_TRANSFORMBEHAVIOR.keySet ().iterator ();
	    while (TRANSFORMBEHAVIOR.hasNext ()) {
		String IDREF = (String) TRANSFORMBEHAVIOR.next ();
		boolean ok = false;

		Map map = v.getIDs ();
		Iterator IDs = map.keySet ().iterator ();
		while (IDs.hasNext ()) {
		    String ID = (String) IDs.next ();
		    if (IDREF.equals (ID)) {
			ok = true;

			setTRANSFORMBEHAVIOR (IDREF, (Behavior) map.get (ID));
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
