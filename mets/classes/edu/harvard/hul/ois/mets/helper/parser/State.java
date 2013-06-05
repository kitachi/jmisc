/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

public class State
{
    /******************************************************************
     * PUBLIC CLASS FIELDS.
     ******************************************************************/

    public static final State ATTR_EQUALS = new State ("ATTR_EQUALS");
    public static final State ATTR_NAME = new State ("ATTR_NAME");
    public static final State ATTR_OPEN_VALUE1 = new State("ATTR_OPEN_VALUE1");
    public static final State ATTR_OPEN_VALUE2 = new State("ATTR_OPEN_VALUE2");
    public static final State ATTR_VALUE1 = new State ("ATTR_VALUE1");
    public static final State ATTR_VALUE2 = new State ("ATTR_VALUE2");
    public static final State ATTR_WS1 = new State ("ATTR_WS1");
    public static final State ATTR_WS2 = new State ("ATTR_WS2");
    public static final State CDATA = new State ("CDATA");
    public static final State CHAR_REF = new State ("CHAR_REF");
    public static final State CHAR_REF_HEX_VALUE =
	          new State ("CHAR_REF_HEX_VALUE");
    public static final State CHAR_REF_VALUE = new State ("CHAR_REF_VALUE");
    public static final State CLOSE_TAG = new State ("CLOSE_TAG");
    public static final State COMMENT = new State ("COMMENT");
    public static final State EMPTY_TAG = new State ("EMPTY_TAG");
    public static final State END_CDATA1 = new State ("END_CDATA1");
    public static final State END_CDATA2 = new State ("END_CDATA2");
    public static final State END_COMMENT1 = new State ("END_COMMENT1");
    public static final State END_COMMENT2 = new State ("END_COMMENT2");
    public static final State END_DOC = new State ("END_DOC");
    public static final State END_PI = new State ("END_PI");
    public static final State END_TAG = new State ("END_TAG");
    public static final State END_TAG_NAME = new State ("END_TAG_NAME");
    public static final State END_TAG_WS = new State ("END_TAG_WS");
    public static final State ENTITY_REF = new State ("ENTITY_REF");
    public static final State EOF = new State ("EOF");
    public static final State MISC = new State ("MISC");
    public static final State OPEN_TAG = new State ("OPEN_TAG");
    public static final State OPEN_TAG2 = new State ("OPEN_TAG2");
    public static final State OPEN_PI = new State ("OPEN_PI");
    public static final State PI = new State ("PI");
    public static final State PROLOG = new State ("PROLOG");
    public static final State READY = new State ("READY");
    public static final State REFERENCE = new State ("REFERENCE");
    public static final State START_CDATA1 = new State ("START_CDATA1");
    public static final State START_CDATA2 = new State ("START_CDATA2");
    public static final State START_CDATA3 = new State ("START_CDATA3");
    public static final State START_CDATA4 = new State ("START_CDATA4");
    public static final State START_CDATA5 = new State ("START_CDATA5");
    public static final State START_CDATA6 = new State ("START_CDATA6");
    public static final State START_COMMENT = new State ("START_COMMENT");
    public static final State START_DOC = new State ("START_DOC");
    public static final State START_PI_TARGET = new State ("START_PI_TARGET");
    public static final State START_PI_WS = new State ("START_PI_WS");
    public static final State START_TAG = new State ("START_TAG");
    public static final State START_TAG_NAME = new State ("START_TAG_NAME");
    public static final State START_TAG_WS = new State ("START_TAG_WS");
    public static final State TEXT = new State ("TEXT");
    public static final State WHITESPACE = new State ("WHITESPACE");
    public static final State XML_DECL = new State ("XML_DECL");

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    private String _name;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public State (String name)
    {
	_name  = name;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    public boolean equals (State state)
    {
	return this == state;
    }

    public String toString ()
    {
	return _name;
    }
}
