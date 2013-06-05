/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import edu.harvard.hul.ois.mets.helper.parser.*;
import java.util.*;

/**
 * Abstract element class.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-18
 */
public abstract class MetsElement
    implements MetsSerializable
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** List of attributes. */
    protected Attributes _attrs;

    /** Element content model. */
    protected List _content;

    /** Element local name. */
    protected String _localName;

    /** Element namespace. */
    protected String _namespace;

    /** Element qName. */
    protected String _qName;

    /** List of namespace-qualified schema to declare. */
    protected List _schema;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a name-less <tt>MetsElement</tt> object.
     */
    public MetsElement ()
    {
	init ();
    }

    /**
     * Instantiate a named <tt>MetsElement</tt> object.
     * @param qName Element local name
     */
    public MetsElement (String qName)
    {
	_qName   = qName;
	int i = _qName.indexOf (':');
	if (i == -1) {
	    _namespace = null;
	    _localName = _qName;
	}
	else {
	    _namespace = _qName.substring (0, i);
	    _localName = _qName.substring (i+1);
	}
	init ();
    }

    /**
     * Initialize a <tt>MetsElement</tt> object with an empty content
     * model.
     */
    public void init ()
    {
	_content = new ArrayList ();
	_schema  = new ArrayList ();
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get the element content model.
     * @return Element content model
     */
    public List getContent ()
    {
	return _content;
    }

    /**
     * Get the element local name.
     * @return Element local name
     */
    public String getLocalName ()
    {
	return _localName;
    }

    /**
     * Get the element namespace.
     * @return Element namespace
     */
    public String getNamespace ()
    {
	return _namespace;
    }

    /**
     * Get the element QName.
     * @return Element QName
     */
    public String getQName ()
    {
	return _qName;
    }

    /**
     * Return the list of namespace-qualified schema.
     */
    public List getSchemas ()
    {
	return _schema;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set additional namespace-qualified schemas to declare.
     * For more than one schema, invoke multiple times.
     * <strong>Note</strong>: The METS, XLink, and Schema (instance) schemas
     * are defined by default; they should <em>not</em> be added using
     * this method.
     * @param namespacePrefix Schema namespace prefix
     * @param namespaceURI    Schema namespace URI
     */
    public void setSchema (String namespacePrefix, String namespaceURI)
    {
	_schema.add (new Schema (namespacePrefix, namespaceURI));
    }

    /**
     * Set additional namespace-qualified schemas to declare.
     * For more than one schema, invoke multiple times.
     * <strong>Note</strong>: The METS, XLink, and Schema (instance) schemas
     * are defined by default; they should <em>not</em> be added using
     * this method.
     * @param namespacePrefix Schema namespace prefix
     * @param namespaceURI    Schema namespace URI
     * @param locationURI     Schema location URI
     */
    public void setSchema (String namespacePrefix, String namespaceURI,
			   String locationURI)
    {
	_schema.add (new Schema (namespacePrefix, namespaceURI, locationURI));
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * De-serialize this element and its content model.
     * @param r Reader
     */
    public abstract void read (MetsReader r)
	throws MetsException;

    /**
     * Serialize this element and its content model.
     * @param w Writer
     */
    public abstract void write (MetsWriter w)
	throws MetsException;

    /**
     * Serialize any namespace-qualified schemas.
     * @param w Writer
     */
    public void writeSchemas (MetsWriter w)
	throws MetsException
    {
	Iterator iter = _schema.listIterator ();
	while (iter.hasNext ()) {
	    Schema schema = (Schema) iter.next ();
	    String prefix = schema.getNamespacePrefix ();
	    String uri    = schema.getNamespaceURI ();
	    if (prefix == null) {
		w.attribute ("xmlns", uri);
	    }
	    else {
		w.attribute ("xmlns:" + prefix, uri);
	    }
	}

	StringBuffer buffer = null;
	iter = _schema.listIterator ();
	while (iter.hasNext ()) {
	    Schema schema = (Schema) iter.next ();
	    String location = schema.getLocationURI ();
	    if (location != null) {
		if (buffer == null) {
		    buffer = new StringBuffer ();
		}
		else {
		    buffer.append (" ");
		}
		buffer.append (schema.getNamespaceURI ());
		buffer.append (" ");
		buffer.append (location);
	    }
	}

	if (buffer != null) {
	    w.attribute ("xsi:schemaLocation", buffer.toString ());
	}
    }
}
