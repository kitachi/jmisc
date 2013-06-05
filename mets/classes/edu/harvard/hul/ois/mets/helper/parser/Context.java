/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

import java.util.*;

public class Context implements Cloneable
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Current character value. */
    private int _c;

    /** Current character. */
    private char _ch;

    /** Current column number. */
    private int _columnNumber;

    /** True if parsing the XML declaration. */
    private boolean _inDecl;

    /** True if parsing the miscellaneous (trailer) portion of the
     * document [27]. */
    private boolean _inMisc;

    /** True if parsing the prolog portion of the document [22]. */
    private boolean _inProlog;

    /** Current element nesting level. */
    private int _nestingLevel;

    /** Current line number. */
    private int _lineNumber;

    /** Element nesting stack. */
    private Stack _stack;

    /** Current parse state. */
    private State _state;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Context</tt> object.
     */
    public Context ()
    {
	_columnNumber = 0;
	_lineNumber   = 1;

	_inDecl   = false;
	_inMisc   = false;
	_inProlog = true;
	_stack = new Stack ();
	_state = State.START_DOC;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Clone a <tt>context</tt> object.
     * @return Cloned objecte
     */
    public Object clone ()
	throws CloneNotSupportedException
    {
	return super.clone ();
    }

    /******************************************************************
     * Accessor methods.
     ******************************************************************/

    public String get (int index)
    {
	return (String) _stack.get (index);
    }

    public char getCharacter ()
    {
	return _ch;
    }

    public int getCharacterValue ()
    {
	return _c;
    }

    public int getColumnNumber ()
    {
	return _columnNumber;
    }

    public int getLineNumber ()
    {
	return _lineNumber;
    }

    public int getNestingLevel ()
    {
	return _stack.size ();
    }

    public State getState ()
    {
	return _state;
    }

    public boolean inDecl ()
    {
	return _inDecl;
    }

    public boolean inMisc ()
    {
	return _inMisc;
    }

    public boolean inProlog ()
    {
	return _inProlog;
    }

    public String peek ()
    {
	return (String) _stack.peek ();
    }

    public String pop ()
    {
	return (String) _stack.pop ();
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    public void inDecl (boolean inDecl)
    {
	_inDecl = inDecl;
    }

    public void inMisc (boolean inMisc)
    {
	_inMisc = inMisc;
    }

    public void inProlog (boolean inProlog)
    {
	_inProlog = inProlog;
    }

    public void push (String tag)
    {
	_stack.push (tag);
    }

    public void push (StringBuffer tag)
    {
	push (tag.toString ());
    }

    public void setCharacter (char ch)
    {
	_ch = ch;
    }

    public void setCharacterValue (int c)
    {
	_c = c;
    }

    public void setColumnNumber (int columnNumber)
    {
	_columnNumber = columnNumber;
    }

    public void setLineNumber (int lineNumber)
    {
	_lineNumber = lineNumber;
    }

    public void setState (State state)
    {
	_state = state;
    }
}
