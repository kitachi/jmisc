/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

public class Token
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Element attributes. */
    private Attributes _attrs;

    /** Character reference value. */
    private int _charValue;

    /** Current parser context. */
    private Context _context;

    /** Document encoding. */
    private String _encoding;

    /** True if character reference is a hexadecimal value. */
    private boolean _hexValue;

    /** Element local name. */
    private String _localName;

    /** Element namespace. */
    private String _namespace;

    /** Element tag QName, entity reference name, or processing instruction
     * target. */
    private String _qName;

    /** Stand-alone flag. */
    private boolean _standalone;

    /** Token type. */
    private Type _type;

    /** Token value. */
    private String _value;

    /** XML version. */
    private String _version;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public Token (Type type, Context context)
    {
	init (type, context);
    }

    public Token (Type type, Context context, int charValue)
    {
	init (type, context);
	_charValue = charValue;
	_hexValue  = false;
    }

    public Token (Type type, Context context, int charValue, boolean hexValue)
    {
	init (type, context);
	_charValue = charValue;
	_hexValue  = hexValue;
    }

    public Token (Type type, Context context, String value)
    {
	init (type, context, value);
    }

    public Token (Type type, Context context, StringBuffer value)
    {
	init (type, context, value.toString ());
    }

    public Token (Type type, Context context, String qName, Attributes attrs)
    {
	init (type, context, qName, attrs);
    }

    public Token (Type type, Context context, StringBuffer qName,
		  Attributes attrs)
    {
	init (type, context, qName.toString (), attrs);
    }

    public Token (Type type, Context context, String qName, String value)
    {
	init (type, context);
    }

    public Token (Type type, Context context, String qName, StringBuffer value)
    {
	init (type, context, qName, value.toString ());
    }

    public Token (Type type, Context context, StringBuffer qName, String value)
    {
	init (type, context, qName.toString (), value);
    }

    public Token (Type type, Context context, StringBuffer qName,
		  StringBuffer value)
    {
	init (type, context, qName.toString (), value.toString ());
    }

    private void init (Type type, Context context)
    {
	_type    = type;
	_context = context;
	_qName   = null;
	_attrs   = null;
	_value   = null;
	_charValue = 0;
	_hexValue  = false;

	_encoding   = "utf-8";
	_standalone = true;
	_version    = "1.0";
    }

    private void init (Type type, Context context, String value)
    {
	init (type, context);
	if (type.equals (Type.END_TAG) ||
	    type.equals (Type.ENTITY_REF)) {
	    _qName = value;

	    if (type.equals (Type.END_TAG)) {
		int i = _qName.indexOf (':');
		if (i == -1) {
		    _namespace = null;
		    _localName = _qName;
		}
		else {
		    _namespace = _qName.substring (0, i);
		    _localName = _qName.substring (i+1);
		}
	    }
	}
	else {
	    _value = value;
	}
    }

    private void init (Type type, Context context, String qName, String value)
    {
	init (type, context);
	_qName = qName;
	_value = value;
    }

    private void init (Type type, Context context, String qName,
		       Attributes attrs)
    {
	init (type, context);
	_qName = qName;
	_attrs = attrs;

	int i = _qName.indexOf (':');
	if (i == -1) {
	    _namespace = null;
	    _localName = _qName;
	}
	else {
	    _namespace = _qName.substring (0, i);
	    _localName = _qName.substring (i+1);
	}
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return element attributes.
     * @return Element attributes
     */
    public Attributes getAttributes ()
    {
	return _attrs;
    }

    /**
     * Return referenced character.
     * @return Character
     */
    public char getChar ()
    {
	return (char) _charValue;
    }

    /**
     * Return character reference value.
     * @return Character value
     */
    public int getCharValue ()
    {
	return _charValue;
    }

    /** Return token context.
     * @return Token context
     */
    public Context getContext ()
    {
	return _context;
    }

    /** Return document encoding.
     * @return document encoding
     */
    public String getEncoding ()
    {
	return _encoding;
    }

    /** Return element local name.
     * @return Element local name
     */
    public String getLocalName ()
    {
	return _localName;
    }

    /** Return element namespace.
     * @return Element namespace
     */
    public String getNamespace ()
    {
	return _namespace;
    }

    /** Return element QName, entity reference name, or processing
     * instruction target.
     * @return QName, name, or target
     */
    public String getQName ()
    {
	return _qName;
    }

    /** Return token type.
     * @return Token type
     */
    public Type getType ()
    {
	return _type;
    }

    /** Return token value.
     * @return Token value
     */
    public String getValue ()
    {
	return _value;
    }

    /** Return XML version.
     * @return XML version
     */
    public String getVersion ()
    {
	return _version;
    }

    /** Return true if character reference is a hexadecimal value.
     * @return True if character reference is a hexadecimal value
     */
    public boolean isHexValue ()
    {
	return _hexValue;
    }

    /** Return true if stand-alone.
     * @return True if stand-alone
     */
    public boolean isStandalone ()
    {
	return _standalone;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    public void isStandalone (boolean standalone)
    {
	_standalone = standalone;
    }

    public void setEncoding (String encoding)
    {
	_encoding = encoding;
    }

    public void setVersion (String version)
    {
	_version = version;
    }
}
