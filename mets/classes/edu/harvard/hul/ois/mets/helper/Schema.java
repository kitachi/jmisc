/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
 *
 * The METS specification was developed as an initiative of the Digital
 * Library Federation <http://www.diglib.org> and is maintained by the
 * Network Development and MARC Standards Office of the Library of
 * Congress <http://www.loc.gov/standards/mets/>.
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

/**
 * Encapsulate the prefix, namespace URI, and schema location URI of a
 * namespace-qualified schema.
 * @author Stephen Abrams
 * @version 1.3.3 2005-01-06 
 */
public class Schema
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Schema location URI. */
    String _locationURI;

    /** Schema namespace URI. */
    String _namespaceURI;

    /** Schema namespace prefix. */
    String _namespacePrefix;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Schema</tt> object.
     * @param namespacePrefix Schema namespace prefix
     * @param namespaceURI    Schema namespace URI
     */
    public Schema (String namespacePrefix, String namespaceURI)
    {
	init (namespacePrefix, namespaceURI, null);
    }

    /**
     * Instantiate a <tt>Schema</tt> object.
     * @param namespacePrefix Schema namespace prefix
     * @param namespaceURI    Schema namespace URI
     * @param locationURI     Schema location URI
     */
    public Schema (String namespacePrefix, String namespaceURI,
		   String locationURI)
    {
	init (namespacePrefix, namespaceURI, locationURI);
    }

    /**
     * Initialize a <tt>Schema</tt> object.
     * @param namespacePrefix Schema namespace prefix
     * @param namespaceURI    Schema namespace URI
     * @param locationURI     Schema location URI
     */
    private void init (String namespacePrefix, String namespaceURI,
		       String locationURI)
    {
	_namespacePrefix = namespacePrefix;
	_namespaceURI    = namespaceURI;
	_locationURI     = locationURI;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get schema location URI.
     * @return Location URI
     */
    public String getLocationURI ()
    {
	return _locationURI;
    }

    /**
     * Get schema namespace prefix.
     * @return Namespace prefix
     */
    public String getNamespacePrefix ()
    {
	return _namespacePrefix;
    }

    /**
     * Get schema namespace URI.
     * @return Namespace URI
     */
    public String getNamespaceURI ()
    {
	return _namespaceURI;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set schema location URI.
     * @param uri Location URI
     */
    public void setLocationURI (String uri)
    {
	_locationURI = uri;
    }

    /**
     * Set schema namespace prefix.
     * @param prefix  Namespace prefix
     */
    public void setNamespacePrefix (String prefix)
    {
	_namespacePrefix = prefix;
    }

    /**
     * Set schema namespace URI.
     * @param uri Namespace URI
     */
    public void setNamespaceURI (String uri)
    {
	_namespaceURI = uri;
    }
}

