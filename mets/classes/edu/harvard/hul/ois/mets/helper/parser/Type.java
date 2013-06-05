package edu.harvard.hul.ois.mets.helper.parser;

public class Type
{
    /******************************************************************
     * PUBLIC CLASS FIELDS.
     ******************************************************************/

    public static final Type CDATA = new Type ("CDATA");
    public static final Type CHAR_REF = new Type ("CHAR_REF");
    public static final Type COMMENT = new Type ("COMMENT");
    public static final Type EMPTY_TAG = new Type ("EMPTY_TAG");
    public static final Type END_DOC = new Type ("END_DOC");
    public static final Type END_TAG = new Type ("END_TAG");
    public static final Type ENTITY_REF = new Type ("ENTITY_REF");
    public static final Type PI = new Type ("PI");
    public static final Type START_DOC = new Type ("START_DOC");
    public static final Type START_TAG = new Type ("START_TAG");
    public static final Type TEXT = new Type ("TEXT");
    public static final Type WHITESPACE = new Type ("WHITESPACE");
    public static final Type XML_DECL = new Type ("XML_DECL");

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Type name. */
    private String _type;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Type</tt> object.
     * @param type Type name 
     */
    public Type (String type)
    {
	_type  = type;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Test Type equality.
     * @return True if type is this type
     */
    public boolean equals (Type type)
    {
	return this == type;
    }

    /**
     * Convert the type to a String.
     * @return Type name
     */
    public String toString ()
    {
	return _type;
    }
}
