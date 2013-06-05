/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

public class Attribute
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Value demarcation character. */
    private char _demarcation;

    /** Attribute local name. */
    private String _localName;

    /** Attribute namespace. */
    private String _namespace;

    /** Attribute QName. */
    private String _qName;

    /** Attribute value. */
    private String _value;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public Attribute (String qName, String value, char demarcation)
    {
	init (qName, value, demarcation);
    }

    public Attribute (String qName, StringBuffer value, char demarcation)
    {
	init (qName, value.toString (), demarcation);
    }

    public Attribute (StringBuffer qName, String value, char demarcation)
    {
	init (qName.toString (), value, demarcation);
    }

    public Attribute (StringBuffer qName, StringBuffer value, char demarcation)
    {
	init (qName.toString (), value.toString (), demarcation);
    }

    private void init (String qName, String value, char demarcation)
    {
	_qName = qName;

	int i = _qName.indexOf (':');
	if (i == -1) {
	    _namespace = null;
	    _localName = _qName;
	}
	else {
	    _namespace = _qName.substring (0, i);
	    _localName = _qName.substring (i+1);
	}

	_value = value;
	_demarcation = demarcation;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    public char getDemarcation ()
    {
	return _demarcation;
    }

    public String getLocalName ()
    {
	return _localName;
    }

    public String getNamespace ()
    {
	return _namespace;
    }

    public String getQName ()
    {
	return _qName;
    }

    public String getValue ()
    {
	return _value;
    }
}
