/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2005 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper.parser;

import java.io.*;
import java.util.*;

/**
 * XML non-validating pull parser.
 * <p>
 * The parser does not preserve all white space in an instance document;
 * round-tripping may not be possible.  Except for the contents of attribute
 * values, none of the whitespace within tag bodies (i.e., between the '<'
 * and '>') is reported back to the invoking system.  Also, the use of
 * quotation marks (") or apostrophes (') to demarcate attribute values is
 * not reported back to the invoking system.
 * <p>
 * References to productions in the XML grammar are bracketed, e.g. "[3]",
 * and are defined in <em>Extensible Markup Language (XML) 1.0 (Second
 * Edition)</em>, W3C Recommendation 6 October 2000
 * &lt;http://www.w3.org/TR/2000/REC-xml-20001006&gt.
 * <p>
 * Unicode character values are defined in <em>The Unicode Standard,
 * Version 3.0</em> (Reading, MA: Addison-Wesley, 2000)
 * &lt;http://www.unicode.org/unicode/uni2book/u2.html&gt;.
 */
public class Parser
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** Unicode value for LATIN CAPITAL A, 'A'. */
    private static final int A = 0x41;

    /** Unicode value for LATIN SMALL A, 'a'. */
    private static final int a = 0x61;

    /** Unicode value for AMPERSAND, '&'. */
    private static final int AMPERSAND = 0x26;

    /** Unicode value for APOSTROPHE, '''. */
    private static final int APOSTROPHE = 0x27;

    /** Unicode value for LATIN CAPITAL C, 'C'. */
    private static final int C = 0x43;

    /** Unicode value for CARRIAGE RETURN. */
    private static final int CR = 0x0D;

    /** Unicode value for COLON, ':'. */
    private static final int COLON = 0x3A;

    /** Unicode value for LATIN CAPITAL D, 'D'. */
    private static final int D = 0x44;

    /** Unicode value for EQUALS SIGN, '='. */
    private static final int EQUALS_SIGN = 0x3D;

    /** Unicode value for EXCLAMATION MARK, '!'. */
    private static final int EXCLAMATION_MARK = 0x21;

    /** Unicode value for LATIN SMALL E, 'e'. */
    private static final int e = 0x65;

    /** Unicode value for LATIN CAPITAL F, 'F'. */
    private static final int F = 0x46;

    /** Unicode value for LATIN SMALL F, 'f'. */
    private static final int f = 0x66;

    /** Unicode value for GREATER-THAN SIGN, '>'. */
    private static final int GREATER_THAN = 0x3E;

    /** Unicode value for HORIZONTAL TABULATION. */
    private static final int HT = 0x09;

    /** Unicode value for HYPEHN, '-'. */
    private static final int HYPHEN = 0x2D;

    /** Unicode value for LEFT SQUARE BRACKET, '['. */
    private static final int LEFT_BRACKET = 0x5B;

    /** Unicode value for LESS-THAN SIGN, '<'. */
    private static final int LESS_THAN = 0x3C;

    /** Unicode value for LINE FEED. */
    private static final int LF = 0x0A;

    /** Unicode value for NINE, '9'. */
    private static final int NINE = 0x39;

    /** Unicode value for NUMBER SIGN. */
    private static final int NUMBER_SIGN = 0x23;

    /** Unicode value for PERIOD, '.'. */
    private static final int PERIOD = 0x2E;

    /** Unicode value for QUESTION_MARK, '?'. */
    private static final int QUESTION_MARK = 0x3F;

    /** Unicode value for QUOTATION MARK '"'. */
    private static final int QUOTATION_MARK = 0x22;

    /** Unicode value for RIGHT SQUARE BRACKET, ']'. */
    private static final int RIGHT_BRACKET = 0x5D;

    /** Unicode value for LATIN SMALL S, 's'. */
    private static final int s = 0x73;

    /** Unicode value for SEMICOLON, ';'. */
    private static final int SEMICOLON = 0x3B;

    /** Unicode value for SLASH, '/'. */
    private static final int SLASH = 0x2F;

    /** Unicode value for SPACE, ' '. */
    private static final int SP = 0x20;

    /** Unicode value for LATIN CAPITAL T, 'T'. */
    private static final int T = 0x54;

    /** Unicode value for UNDERSCORE, '_'. */
    private static final int UNDERSCORE = 0x5F;

    /** Unicode value for LATIN SMALL X, 'x'. */
    private static final int x = 0x78;

    /** Unicode value for ZERO, '0'. */
    private static final int ZERO = 0x30;

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Parser context. */
    private Context _context;

    /** Instance document reader. */
    private Reader _reader;

    /** Parsed token. */
    private Token _token;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Parser</tt> object.
     */
    public Parser ()
    {
	_context = new Context ();
	_context.inDecl   (false);
	_context.inMisc   (false);
	_context.inProlog (true);
	_context.setColumnNumber (0);
	_context.setLineNumber   (1);
	_context.setState (State.START_DOC);
	_token = null;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Consume current token.
     * <strong>Note</strong>: This method is consumming; it can be invoked
     * for a given token once.
     * If no more tokens are present in the document, the method returns
     * null;
     * @return Current token or null
     */
    public Token getToken ()
	throws ParserException
    {
	return getToken (false);
    }

    /**
     * Consume current token.
     * <strong>Note</strong>: This method is consumming; it can be invoked
     * for a given token once.
     * If no more tokens are present in the document, the method returns
     * null;
     * @return Current token or null
     */
    public Token getToken (boolean debug)
	throws ParserException
    {
	if (_token == null) {
	    peekAtToken (debug);
	}
	Token token = _token;
	_token = null;          /* Mark this token as consummed. */

	if (debug) {
	    System.out.println ("At " + _context.getLineNumber () + ":" +
				_context.getColumnNumber () + ":" +
				_context.getState().toString() +
				" get.return " + token.getType().toString());
	}
	return token;
    }

    /**
     * Return current token.
     * <strong>Note</strong>: This method is non-consumming; repeated
     * invocations will return the same type for the same current token.
     * If no more tokens are present in the document, the method returns
     * null.
     * @return Current token or null
     */
    public Token peekAtToken ()
	throws ParserException
    {
	return peekAtToken (false);
    }

    /**
     * Return current token.
     * <strong>Note</strong>: This method is non-consumming; repeated
     * invocations will return the same type for the same current token.
     * If no more tokens are present in the document, the method returns
     * null.
     * @return Current token or null
     */
    public Token peekAtToken (boolean debug)
	throws ParserException
    {
	if (_token != null) {
	    if (debug) {
		System.out.println ("At " + _context.getLineNumber () + ":" +
				    _context.getColumnNumber () + ":" +
				    _context.getState().toString() +
				    " peek.return " +
				    _token.getType().toString());
	    }
	    return _token;
	}

	StringBuffer qName  = null;   /* Element tag name buffer. */
	StringBuffer qAttr  = null;   /* Attribute name buffer.*/
	StringBuffer value  = null;   /* Attribute value buffer. */
	int charValue = 0;            /* Character reference value. */

	Attributes attrs    = null;   /* List of attributes. */

	try {
	    State state = _context.getState ();
	    if (state.equals (State.START_DOC)) {
		_token = new Token (Type.START_DOC, _context);
		_context.setState (State.PROLOG);

		if (debug) {
		    System.out.println ("At " + _context.getLineNumber () +
					":" + _context.getColumnNumber () +
					":" + _context.getState().toString () +
					" peek.return START_DOC");
		}
		return _token;
	    }

	    int c;
	    for (long chars=0; (c = _reader.read ()) > -1; chars++) {
		char ch = (char) c;
		_context.setCharacter (ch);
		_context.setCharacterValue (c);

		if (debug) {
		    System.out.println ("At " + _context.getLineNumber () +
					":" + _context.getColumnNumber () +
					":" + _context.getState().toString () +
					" saw '" + ch + "'");
		}

		if (c == LF) {
		    _context.setColumnNumber (0);
		    _context.setLineNumber (_context.getLineNumber () + 1);
		}
		else {
		    _context.setColumnNumber (_context.getColumnNumber () + 1);
		}

		state = _context.getState (); 
		if (state.equals (State.PROLOG) ||
		    state.equals (State.MISC)) {
		    if (c == LESS_THAN) {
			_context.setState (State.OPEN_TAG);
		    }
		    else if (isWhitespace (c)) {
			_context.setState (State.WHITESPACE);

			value = new StringBuffer ();
			value.append (ch);
		    }
		    else {
			throw new ParserException ("expecting '<' or WS",
						   _context);
		    }
		}
		else if (state.equals (State.OPEN_TAG)) {
		    if (isLetter (c) || c == UNDERSCORE || c == COLON) {
			_context.setState (State.START_TAG_NAME);
			qName = new StringBuffer ().append (ch);
			attrs = new Attributes ();
		    }
		    else if (c == SLASH) {
			_context.setState (State.END_TAG);
		    }
		    else if (c == EXCLAMATION_MARK) {
			_context.setState (State.OPEN_TAG2);
		    }
		    else if (c == QUESTION_MARK) {
			_context.setState (State.OPEN_PI);
		    }
		    else {
			throw new ParserException ("expecting Letter, '_', " +
						   "':', '/', or '!'",
						   _context);
		    }

		    if (_context.inProlog ()) {
			_context.inProlog (false);
		    }
		}
		else if (state.equals (State.OPEN_TAG2)) {
		    if (c == HYPHEN) {
			_context.setState (State.START_COMMENT);
		    }
		    else if (c == LEFT_BRACKET) {
			_context.setState (State.START_CDATA1);
		    }
		    else {
			throw new ParserException ("expecting '-' or '[",
						   _context);
		    }
		}
		else if (state.equals (State.OPEN_PI)) {
		    if (isLetter (c) || c == UNDERSCORE || c == COLON) {
			_context.setState (State.START_PI_TARGET);
			qName = new StringBuffer ();
			qName.append (ch);
		    }
		    else {
			throw new ParserException ("expecting Letter, '_', " +
						   "or ':'", _context);
		    }
		}
		else if (state.equals (State.START_TAG_NAME) ||
			 state.equals (State.START_TAG_WS)) {
		    if (state.equals (State.START_TAG_NAME) &&
			isNameChar (c)) {
			_context.setState (State.START_TAG_NAME);
			qName.append (ch);
		    }
		    else if (state.equals(State.START_TAG_WS) &&
			     (isLetter (c) || c == UNDERSCORE || c == COLON)) {
			_context.setState (State.ATTR_NAME);
			qAttr = new StringBuffer ();
			qAttr.append (ch);
		    }
		    else if (isWhitespace (c)) {
			_context.setState (State.START_TAG_WS);
		    }
		    else if (c == SLASH) {
			_context.setState (State.EMPTY_TAG);
		    }
		    else if (c == GREATER_THAN) {
			_context.push (qName);
			_token = new Token (Type.START_TAG, _context, qName,
					    attrs);
			qName = null;
			attrs = null;
			_context.setState (State.READY);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return START_TAG " +
						_token.getQName ());
						
			}
			return _token;
		    }
		    else if (c == QUESTION_MARK && _context.inDecl ()) {
			_context.setState (State.XML_DECL);
		    }
		    else {
			throw new ParserException ("expecting NameChar, WS, " +
						   "'/', or '>'", _context);
		    }
		}
		else if (state.equals (State.XML_DECL)) {
		    if (c == GREATER_THAN) {
			_token = new Token (Type.XML_DECL, _context, qName,
					    attrs);
			int size = attrs.size ();
			if (size > 0) {
			    Attribute attr = attrs.get (0);
			    if (attr.getQName ().equals ("version")) {
				_token.setVersion (attr.getValue ());

				if (attrs.hasNext ()) {
				    attrs.next ();
				    while (attrs.hasNext ()) {
					attr = attrs.next ();

					if (attr.getQName ().equals ("encoding")) {
					    _token.setEncoding (attr.getValue ());
					}
					else if (attr.getQName ().equals ("standalone")) {
					    _token.isStandalone (attr.getValue ().equals ("yes") ? true : false);
					}
					else {
					    throw new ParserException (
					       "optional XML declaration " +
					       "attributes must be " +
					       "\"encoding\" or \"" +
					       "standlone\"", _context);
					}
				    }
				}
			    }
			    else {
				throw new ParserException ("first XML " +
							   "declaration " +
							   "attribute must " +
							   "be \"version\"",
							   _context);
			    }
			    attrs.reset ();
			}
			else {
			    throw new ParserException ("XML declaration " +
						       "must have a version " +
						       "attribute", _context);
			}

			qName = null;
			attrs = null;
			_context.setState (State.PROLOG);
			_context.inDecl   (false);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return XML_DECL");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expecting '>'", _context);
		    }
		}
		else if (state.equals (State.ATTR_NAME)) {
		    if (isNameChar (c)) {
			_context.setState (State.ATTR_NAME);
			qAttr.append (ch);
		    }
		    else if (isWhitespace (c)) {
			_context.setState (State.ATTR_WS1);
		    }
		    else if (c == EQUALS_SIGN) {
			_context.setState (State.ATTR_EQUALS);
		    }
		    else {
			throw new ParserException ("expecting NameChar or WS",
						   _context);
		    }
		}
		else if (state.equals (State.ATTR_WS1)) {
		    if (isWhitespace (c)) {
			_context.setState (State.ATTR_WS1);
		    }
		    else if (c == EQUALS_SIGN) {
			_context.setState (State.ATTR_EQUALS);
		    }
		    else {
			throw new ParserException ("expecting WS or '='",
						   _context);
		    }
		}
		else if (state.equals (State.ATTR_EQUALS)) {
		    if (isWhitespace (c)) {
			_context.setState (State.ATTR_WS2);
		    }
		    else if (c == APOSTROPHE) {
			_context.setState (State.ATTR_VALUE1);
			value = new StringBuffer ();
		    }
		    else if (c == QUOTATION_MARK) {
			_context.setState (State.ATTR_VALUE2);
			value = new StringBuffer ();
		    }
		    else {
			throw new ParserException ("expecting '\"' or '''",
						   _context);
		    }
		}
		else if (state.equals (State.ATTR_WS2)) {
		    if (isWhitespace (c)) {
			_context.setState (State.ATTR_WS2);
		    }
		    else if (c == APOSTROPHE) {
			_context.setState (State.ATTR_VALUE1);
			value = new StringBuffer ();
		    }
		    else if (c == QUOTATION_MARK){
			_context.setState (State.ATTR_VALUE2);
			value = new StringBuffer ();
		    }
		    else {
			throw new ParserException ("expecting WS, '\"', or " +
						   "'''", _context);
		    }
		}
		else if (state.equals (State.ATTR_VALUE1)) {
		    if (c == APOSTROPHE) {
			_context.setState (State.START_TAG_WS);
			Attribute attr = new Attribute (qAttr, unescape(value),
							'\'');
			attrs.add (attr);
			qAttr = null;
			value = null;
		    }
		    else {
			_context.setState (State.ATTR_VALUE1);
			value.append (ch);
		    }
		}
		else if (state.equals (State.ATTR_VALUE2)) {
		    if (c == QUOTATION_MARK) {
			_context.setState (State.START_TAG_WS);
			Attribute attr = new Attribute (qAttr, unescape(value),
							'"');
			attrs.add (attr);
			qAttr = null;
			value = null;
		    }
		    else {
			_context.setState (State.ATTR_VALUE2);
			value.append (ch);
		    }
		}
		else if (state.equals (State.EMPTY_TAG)) {
		    _context.push (qName);
		    _token = new Token(Type.EMPTY_TAG, _context, qName, attrs);
		    qName = null;
		    attrs = null;

		    _context.pop ();
		    _context.inMisc (_context.getNestingLevel () == 0);
		    _context.setState (_context.inMisc () ? State.MISC :
				       State.READY);
		    if (debug) {
			System.out.println ("At " + _context.getLineNumber () +
					    ":" + _context.getColumnNumber () +
					    ":" +
					    _context.getState().toString()+
					    " peek.return EMPTY_TAG " +
					    _token.getQName ());
		    }
		    return _token;
		}
		else if (state.equals (State.READY)) {
		    if (c == AMPERSAND) {
			_context.setState (State.REFERENCE);
		    }
		    else if (c == LESS_THAN) {
			_context.setState (State.OPEN_TAG);
		    }
		    else {
			_context.setState (State.TEXT);
			value = new StringBuffer ();
			value.append (ch);
		    }
		}
		else if (state.equals (State.TEXT)) {
		    if (c == AMPERSAND ||
			c == LESS_THAN) {
			_token = new Token (Type.TEXT, _context, value);
			value = null;
			if (c == AMPERSAND) {
			    _context.setState (State.REFERENCE);
			}
			else {
			    _context.setState (State.OPEN_TAG);
			}

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return TEXT \"" +
						_token.getValue () + "\"");
			}
			return _token;
		    }
		    else {
			_context.setState (State.TEXT);
			value.append (ch);
		    }
		}
		else if (state.equals (State.REFERENCE)) {
		    if (c == NUMBER_SIGN) {
			_context.setState (State.CHAR_REF);
		    }
		    else if (isLetter (c) || c == UNDERSCORE || c == COLON) {
			_context.setState (State.ENTITY_REF);
			qName = new StringBuffer ();
			qName.append (ch);
		    }
		    else {
			throw new ParserException ("expected Letter, '_', " +
						   "or ':'", _context);
		    }
		}
		else if (state.equals (State.CHAR_REF)) {
		    if (c == x) {
			_context.setState (State.CHAR_REF_HEX_VALUE);
			charValue = 0;
		    }
		    else if (ZERO <= c && c <= NINE) {
			_context.setState (State.CHAR_REF_VALUE);
			charValue = c - ZERO;
		    }
		    else {
			throw new ParserException ("expecting '0'-'9'",
						   _context);
		    }
		}
		else if (state.equals (State.CHAR_REF_VALUE)) {
		    if (ZERO <= c && c <= NINE) {
			_context.setState (State.CHAR_REF_VALUE);
			charValue = charValue*10 + c - ZERO;
		    }
		    else if (c == SEMICOLON) {
			_token = new Token (Type.CHAR_REF, _context,charValue);
			charValue = 0;
			_context.setState (State.READY);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return CHAR_REF '" +
						_token.getChar () + "'");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expecting '0'-'9' or ';'",
						   _context);
		    }
		}
		else if (state.equals (State.CHAR_REF_HEX_VALUE)) {
		    if (ZERO <= c && c <= NINE) {
			_context.setState (State.CHAR_REF_HEX_VALUE);
			charValue = charValue*16 + c - ZERO;
		    }
		    else if (A <= c && c <= F) {
			_context.setState (State.CHAR_REF_HEX_VALUE);
			charValue = charValue*16 + c - A + 10;
		    }
		    else if (a <= c && c <= f) {
			_context.setState (State.CHAR_REF_HEX_VALUE);
			charValue = charValue*16 + c - a + 10;
		    }
		    else if (c == SEMICOLON) {
			_token = new Token (Type.CHAR_REF, _context, charValue,
					    true);
			charValue = 0;
			_context.setState (State.READY);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return CHAR_REF '" +
						_token.getChar () + "'");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expecting '0'-'9' or ';'",
						   _context);
		    }
		}
		else if (state.equals (State.ENTITY_REF)) {
		    if (isNameChar (c)) {
			_context.setState (State.ENTITY_REF);
			qName.append (ch);
		    }
		    else if (c == SEMICOLON) {

			/* Test for predefined entity names. */

			String entity = qName.toString ();
			if (entity.equals ("amp")) {
			    entity = "&";
			}
			else if (entity.equals ("apos")) {
			    entity = "'";
			}
			else if (entity.equals ("gt")) {
			    entity = ">";
			}
			else if (entity.equals ("lt")) {
			    entity = "<";
			}
			else if (entity.equals ("quot")) {
			    entity = "\"";
			}

			_token = new Token (Type.ENTITY_REF, _context, qName,
					    entity);
			qName = null;
			_context.setState (State.READY);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return ENTITY_REF \"" +
						entity + "\"");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expecting NameChar",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA1)) {
		    if (c == C) {
			_context.setState (State.START_CDATA2);
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA2)) {
		    if (c == D) {
			_context.setState (State.START_CDATA3);
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA3)) {
		    if (c == A) {
			_context.setState (State.START_CDATA4);
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA4)) {
		    if (c == T) {
			_context.setState (State.START_CDATA5);
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA5)) {
		    if (c == A) {
			_context.setState (State.START_CDATA6);
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.START_CDATA6)) {
		    if (c == LEFT_BRACKET) {
			_context.setState (State.CDATA);
			value = new StringBuffer ();
		    }
		    else {
			throw new ParserException ("expecting \"CDATA[\"",
						   _context);
		    }
		}
		else if (state.equals (State.CDATA)) {
		    if (c == RIGHT_BRACKET) {
			_context.setState (State.END_CDATA1);
		    }
		    else {
			_context.setState (State.CDATA);
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_CDATA1)) {
		    if (c == RIGHT_BRACKET) {
			_context.setState (State.END_CDATA2);
		    }
		    else {
			_context.setState (State.CDATA);
			value.append (']');
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_CDATA2)) {
		    if (c == GREATER_THAN) {
			_token = new Token (Type.CDATA,_context, value);
			value = null;
			_context.setState (State.READY);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return CDATA \"" +
						_token.getValue () + "\"");
			}
			return _token;
		    }
		    else {
			_context.setState (State.CDATA);
			value.append ("]]");
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_TAG)) {
		    if (isNameChar (c)) {
			_context.setState (State.END_TAG_NAME);
			qName = new StringBuffer ().append (ch);
		    }
		    else {
			throw new ParserException ("expecting NameChar",
						   _context);
		    }
		}
		else if (state.equals (State.END_TAG_NAME) ||
			 state.equals (State.END_TAG_WS)) {
		    if (state.equals (State.END_TAG_NAME) &&
			isNameChar (c)) {
			qName.append (ch);
		    }
		    else if (isWhitespace (c)) {
			_context.setState (State.END_TAG_WS);
		    }
		    else if (c == GREATER_THAN) {
			if (!_context.peek ().equals (qName.toString ())) {
			    throw new ParserException ("expected </" +
						       _context.peek () +
						       ">, not </" + qName +
						       ">", _context);
			}

			_token = new Token (Type.END_TAG, _context, qName);
			qName = null;

			_context.pop ();
			_context.inMisc (_context.getNestingLevel () == 0);
			_context.setState (_context.inMisc () ? State.MISC :
					   State.READY);
			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return END_TAG " +
						_token.getQName ());
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expected NameChar, WS, " +
						   "or '>'", _context);
		    }
		}
		else if (state.equals (State.START_COMMENT)) {
		    if (c == HYPHEN) {
			_context.setState (State.COMMENT);
			value = new StringBuffer ();
		    }
		    else {
			throw new ParserException ("expected '-'", _context);
		    }
		}
		else if (state.equals (State.COMMENT)) {
		    if (c == HYPHEN) {
			_context.setState (State.END_COMMENT1);
		    }
		    else {
			_context.setState (State.COMMENT);
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_COMMENT1)) {
		    if (c == HYPHEN) {
			_context.setState (State.END_COMMENT2);
		    }
		    else {
			_context.setState (State.COMMENT);
			value.append ('-');
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_COMMENT2)) {
		    if (c == GREATER_THAN) {
			_token = new Token (Type.COMMENT, _context, value);
			value = null;
			if (_context.inProlog ()) {
			    _context.setState (State.PROLOG);
			}
			else if (_context.inMisc ()) {
			    _context.setState (State.MISC);
			}
			else {
			    _context.setState (State.READY);
			}

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return COMMENT");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expected '>'", _context);
		    }
		}
		else if (state.equals (State.WHITESPACE)) {
		    if (isWhitespace (c)) {
			_context.setState (State.WHITESPACE);

			value.append (ch);
		    }
		    else if (c == LESS_THAN) {
			_token = new Token (Type.WHITESPACE, _context, value);
			value = null;
			_context.setState (State.OPEN_TAG);

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return WHITESPACE");
			}
			return _token;
		    }
		    else {
			throw new ParserException ("expected WS or '<'",
						   _context);
		    }
		}
		else if (state.equals (State.START_PI_TARGET)) {
		    if (isNameChar (c)) {
			_context.setState (State.START_PI_TARGET);
			qName.append (ch);
		    }
		    else if (isWhitespace (c)) {
			if (qName.toString ().equals ("xml")) {
			    _context.setState (State.START_TAG_WS);
			    _context.inDecl (true);
			    attrs = new Attributes ();
			}
			else {
			    _context.setState (State.START_PI_WS);
			}
		    }
		    else if (c == QUESTION_MARK) {
			_context.setState (State.END_PI);
			value = new StringBuffer ();
		    }
		    else {
			throw new ParserException ("expected NameChar, WS, " +
						   "or '?'", _context);
		    }
		}
		else if (state.equals (State.START_PI_WS)) {
		    if (isWhitespace (c)) {
			_context.setState (State.START_PI_WS);
		    }
		    else {
			_context.setState (State.PI);
			value = new StringBuffer ();
			value.append (ch);
		    }
		}
		else if (state.equals (State.PI)) {
		    if (c == QUESTION_MARK) {
			_context.setState (State.END_PI);
		    }
		    else {
			_context.setState (State.PI);
			value.append (ch);
		    }
		}
		else if (state.equals (State.END_PI)) {
		    if (c == GREATER_THAN) {
			_token = new Token (Type.PI, _context, qName, value);
			qName = null;
			value  = null;
			if (_context.inProlog ()) {
			    _context.setState (State.PROLOG);
			}
			else if (_context.inMisc ()) {
			    _context.setState (State.MISC);
			}
			else {
			    _context.setState (State.READY);
			}

			if (debug) {
			    System.out.println ("At " +
						_context.getLineNumber () +
						":" +
						_context.getColumnNumber () +
						":" +
						_context.getState().toString()+
						" peek.return PI");
			}
			return _token;
		    }
		    else {
			_context.setState (State.PI);
			value.append ("?");
			value.append (ch);
		    }
		}
	    }
	    state = _context.getState ();

	    if (state.equals (State.WHITESPACE)) {
		_token = new Token (Type.WHITESPACE, _context, value);
		value = null;
		_context.setState (State.END_DOC);

		if (debug) {
		    System.out.println ("At " +	_context.getLineNumber () +
					":" + _context.getColumnNumber () +
					":" + _context.getState().toString () +
					" peek.return END_DOC");
		}
		return _token;
	    }
	    else if (!state.equals (State.EOF)) {
		_token = new Token (Type.END_DOC,_context);
		_context.setState (State.EOF);

		if (debug) {
		    System.out.println ("At " +	_context.getLineNumber () +
					":" + _context.getColumnNumber () +
					":" + _context.getState().toString () +
					" peek.return EOF");
		}
		return _token;
	    }
	}
	catch (ParserException e) {
	    throw e;
	}
	catch (Exception e) {
	    throw new ParserException (e.toString ());
	}

	return null;
    }

    /******************************************************************
     * Mutator functions
     ******************************************************************/

    /**
     * Set the input file.
     * @param file Input file
     * @throws ParserException I/O exception
     */
    public void setInput (File file)
	throws ParserException
    {
	try {
	    setInput (new FileInputStream (file));
	}
	catch (Exception e) {
	    throw new ParserException (e.getMessage ());
	}
    }

    /**
     * Set the input stream.
     * @param in Open input stream
     * @throws ParserException I/O exception
     */
    public void setInput (InputStream in)
	throws ParserException
    {
	try {
	    InputStreamReader is = new InputStreamReader (in, "utf-8");
	    _reader = new BufferedReader (is);
	}
	catch (Exception e) {
	    throw new ParserException (e.getMessage ());
	}
    }

    /**
     * Set the input reader.
     * @param reader Open reader
     * @throws ParserException I/O exception
     */
    public void setInput (Reader reader)
	throws ParserException
    {
	try {
	    if (reader instanceof BufferedReader) {
		_reader = reader;
	    }
	    else {
		_reader = new BufferedReader (reader);
	    }
	}
	catch (Exception e) {
	    throw new ParserException (e.getMessage ());
	}
    }

    /**
     * Set the input file name.
     * @param name File name
     * @throws ParserException I/O exception
     */
    public void setInput (String name)
	throws ParserException
    {
	try {
	    setInput (new FileInputStream (name));
	}
	catch (Exception e) {
	    throw new ParserException (e.getMessage ());
	}
    }

    /******************************************************************
     * PRIVATE CLASS METHODS.
     ******************************************************************/

    /** Determine if character is a combining character, as defined by [87].
     * @param c Unicode value of character
     * @return true if character is a combining character
     */
    private static boolean isCombiningChar (int c)
    {
	if ((0x0300 <= c && c <= 0x0345) || (0x0360 <= c && c <= 0x0361) ||
	    (0x0483 <= c && c <= 0x0486) || (0x0591 <= c && c <= 0x05A1) ||
	    (0x05A3 <= c && c <= 0x05B9) || (0x05BB <= c && c <= 0x05BD) ||
	     c == 0x05BF                 || (0x05C1 <= c && c <= 0x05C2) ||
	     c == 0x05C4                 || (0x064B <= c && c <= 0x0652) ||
	     c == 0x0670                 || (0x06D6 <= c && c <= 0x06DC) ||
	    (0x06DD <= c && c <= 0x06DF) || (0x06E0 <= c && c <= 0x06E4) ||
	    (0x06E7 <= c && c <= 0x06E8) || (0x06EA <= c && c <= 0x06ED) ||
	    (0x0901 <= c && c <= 0x0903) ||  c == 0x093C ||
	    (0x093E <= c && c <= 0x094C) ||  c == 0x094D ||
	    (0x0951 <= c && c <= 0x0954) || (0x0962 <= c && c <= 0x0963) ||
	    (0x0981 <= c && c <= 0x0983) ||  c == 0x09BC || c == 0x09BE  ||
	     c == 0x09BF                 || (0x09C0 <= c && c <= 0x09C4) ||
	    (0x09C7 <= c && c <= 0x09C8) || (0x09CB <= c && c <= 0x09CD) ||
	     c == 0x09D7                 || (0x09E2 <= c && c <= 0x09E3) ||
	     c == 0x0A02 || c == 0x0A3C  ||  c == 0x0A3E || c == 0x0A3F  ||
	    (0x0A40 <= c && c <= 0x0A42) || (0x0A47 <= c && c <= 0x0A48) ||
	    (0x0A4B <= c && c <= 0x0A4D) || (0x0A70 <= c && c <= 0x0A71) ||
	    (0x0A81 <= c && c <= 0x0A83) ||  c == 0x0ABC ||
	    (0x0ABE <= c && c <= 0x0AC5) || (0x0AC7 <= c && c <= 0x0AC9) ||
	    (0x0ACB <= c && c <= 0x0ACD) || (0x0B01 <= c && c <= 0x0B03) ||
	     c == 0x0B3C                 || (0x0B3E <= c && c <= 0x0B43) ||
	    (0x0B47 <= c && c <= 0x0B48) || (0x0B4B <= c && c <= 0x0B4D) ||
	    (0x0B56 <= c && c <= 0x0B57) || (0x0B82 <= c && c <= 0x0B83) ||
	    (0x0BBE <= c && c <= 0x0BC2) || (0x0BC6 <= c && c <= 0x0BC8) ||
	    (0x0BCA <= c && c <= 0x0BCD) ||  c == 0x0BD7 ||
	    (0x0C01 <= c && c <= 0x0C03) || (0x0C3E <= c && c <= 0x0C44) ||
	    (0x0C46 <= c && c <= 0x0C48) || (0x0C4A <= c && c <= 0x0C4D) ||
	    (0x0C55 <= c && c <= 0x0C56) || (0x0C82 <= c && c <= 0x0C83) ||
	    (0x0CBE <= c && c <= 0x0CC4) || (0x0CC6 <= c && c <= 0x0CC8) ||
	    (0x0CCA <= c && c <= 0x0CCD) || (0x0CD5 <= c && c <= 0x0CD6) ||
	    (0x0D02 <= c && c <= 0x0D03) || (0x0D3E <= c && c <= 0x0D43) ||
	    (0x0D46 <= c && c <= 0x0D48) || (0x0D4A <= c && c <= 0x0D4D) ||
	     c == 0x0D57 || c == 0x0E31  || (0x0E34 <= c && c <= 0x0E3A) ||
	    (0x0E47 <= c && c <= 0x0E4E) ||  c == 0x0EB1 ||
	    (0x0EB4 <= c && c <= 0x0EB9) || (0x0EBB <= c && c <= 0x0EBC) ||
	    (0x0EC8 <= c && c <= 0x0ECD) || (0x0F18 <= c && c <= 0x0F19) ||
	     c == 0x0F35 || c == 0x0F37  ||  c == 0x0F39 || c == 0x0F3E  ||
	     c == 0x0F3F                 || (0x0F71 <= c && c <= 0x0F84) ||
	    (0x0F86 <= c && c <= 0x0F8B) || (0x0F90 <= c && c <= 0x0F95) ||
	     c == 0x0F97                 || (0x0F99 <= c && c <= 0x0FAD) ||
	    (0x0FB1 <= c && c <= 0x0FB7) ||  c == 0x0FB9 ||
	    (0x20D0 <= c && c <= 0x20DC) ||  c == 0x20E1 ||
	    (0x302A <= c && c <= 0x302F) ||  c == 0x3099 || c == 0x309A) {
	    return true;
	}

	return false;
    }

    /** Determine if character is a digit, as defined by [88].
     * @param c Unicode value of character
     * @return true if character is a digit
     */
    private static boolean isDigit (int c)
    {
	if ((0x0030 <= c && c <= 0x0039) || (0x0660 <= c && c <= 0x0669) ||
	    (0x06F0 <= c && c <= 0x06F9) || (0x0966 <= c && c <= 0x096F) ||
	    (0x09E6 <= c && c <= 0x09EF) || (0x0A66 <= c && c <= 0x0A6F) ||
	    (0x0AE6 <= c && c <= 0x0AEF) || (0x0B66 <= c && c <= 0x0B6F) ||
	    (0x0BE7 <= c && c <= 0x0BEF) || (0x0C66 <= c && c <= 0x0C6F) ||
	    (0x0CE6 <= c && c <= 0x0CEF) || (0x0D66 <= c && c <= 0x0D6F) ||
	    (0x0E50 <= c && c <= 0x0E59) || (0x0ED0 <= c && c <= 0x0ED9) ||
	    (0x0F20 <= c && c <= 0x0F29)) {
	    return true;
	}

	return false;
    }

    /** Determine if character is a extender, as defined by [89].
     * @param c Unicode value of character
     * @return true if character is an extender
     */
    private static boolean isExtender (int c)
    {
	if ( c == 0x00B7 || c == 0x02D0  ||  c == 0x02D1 || c == 0x0387  ||
	     c == 0x0640 || c == 0x0E46  ||  c == 0x0EC6 || c == 0x3005  ||
	    (0x3031 <= c && c <= 0x3035) || (0x309D <= c && c <= 0x309E) ||
	    (0x30FC <= c && c <= 0x30FE)) {
	    return true;
	}

	return false;
    }

    /**
     * Determine if character is a Letter, as defined by [84].
     * @param c Unicode value of character
     * @return True if character is a letter
     */
    private static boolean isLetter (int c)
    {
	if (
	    /* Base characters [85] */
	    (0x0041 <= c && c <= 0x005A) || (0x0061 <= c && c <= 0x007A) ||
	    (0x00C0 <= c && c <= 0x00D6) || (0x00D8 <= c && c <= 0x00F6) ||
	    (0x00F8 <= c && c <= 0x00FF) || (0x0100 <= c && c <= 0x0131) ||
	    (0x0134 <= c && c <= 0x013E) || (0x0141 <= c && c <= 0x0148) ||
	    (0x014A <= c && c <= 0x017E) || (0x0180 <= c && c <= 0x01C3) ||
	    (0x01CD <= c && c <= 0x01F0) || (0x01F4 <= c && c <= 0x01F5) ||
	    (0x01FA <= c && c <= 0x0217) || (0x0250 <= c && c <= 0x02A8) ||
	    (0x02BB <= c && c <= 0x02C1) ||  c == 0x0386 ||
	    (0x0388 <= c && c <= 0x038A) ||  c == 0x038C ||
	    (0x038E <= c && c <= 0x03A1) || (0x03A3 <= c && c <= 0x03CE) ||
	    (0x03D0 <= c && c <= 0x03D6) ||  c == 0x03DA || c == 0x03DC  ||
	     c == 0x03DE || c == 0x03E0  || (0x03E2 <= c && c <= 0x03F3) ||
	    (0x0401 <= c && c <= 0x040C) || (0x040E <= c && c <= 0x044F) ||
	    (0x0451 <= c && c <= 0x045C) || (0x045E <= c && c <= 0x0481) || 
	    (0x0490 <= c && c <= 0x04C4) || (0x04C7 <= c && c <= 0x04C8) ||
	    (0x04CB <= c && c <= 0x04CC) || (0x04D0 <= c && c <= 0x04EB) ||
	    (0x04EE <= c && c <= 0x04F5) || (0x04F8 <= c && c <= 0x04F9) ||
	    (0x0531 <= c && c <= 0x0556) ||  c == 0x0559 ||
	    (0x0561 <= c && c <= 0x0586) || (0x05D0 <= c && c <= 0x05EA) ||
	    (0x05F0 <= c && c <= 0x05F2) || (0x0621 <= c && c <= 0x063A) ||
	    (0x0641 <= c && c <= 0x064A) || (0x0671 <= c && c <= 0x06B7) ||
	    (0x06BA <= c && c <= 0x06BE) || (0x06C0 <= c && c <= 0x06CE) ||
	    (0x06D0 <= c && c <= 0x06D3) ||  c == 0x06D5 ||
	    (0x06E5 <= c && c <= 0x06E6) || (0x0905 <= c && c <= 0x0939) ||
	     c == 0x093D                 || (0x0958 <= c && c <= 0x0961) ||
	    (0x0985 <= c && c <= 0x098C) || (0x098F <= c && c <= 0x0990) ||
	    (0x0993 <= c && c <= 0x09A8) || (0x09AA <= c && c <= 0x09B0) ||
	     c == 0x09B2                 || (0x09B6 <= c && c <= 0x09B9) ||
	    (0x09DC <= c && c <= 0x09DD) || (0x09DF <= c && c <= 0x09E1) ||
	    (0x09F0 <= c && c <= 0x09F1) || (0x0A05 <= c && c <= 0x0A0A) ||
	    (0x0A0F <= c && c <= 0x0A10) || (0x0A13 <= c && c <= 0x0A28) ||
	    (0x0A2A <= c && c <= 0x0A30) || (0x0A32 <= c && c <= 0x0A33) ||
	    (0x0A35 <= c && c <= 0x0A36) || (0x0A38 <= c && c <= 0x0A39) ||
	    (0x0A59 <= c && c <= 0x0A5C) ||  c == 0x0A5E ||
	    (0x0A72 <= c && c <= 0x0A74) || (0x0A85 <= c && c <= 0x0A8B) ||
	     c == 0x0A8D                 || (0x0A8F <= c && c <= 0x0A91) ||
	    (0x0A93 <= c && c <= 0x0AA8) || (0x0AAA <= c && c <= 0x0AB0) ||
	    (0x0AB2 <= c && c <= 0x0AB3) || (0x0AB5 <= c && c <= 0x0AB9) ||
	     c == 0x0ABD || c == 0x0AE0  || (0x0B05 <= c && c <= 0x0B0C) ||
	    (0x0B0F <= c && c <= 0x0B10) || (0x0B13 <= c && c <= 0x0B28) ||
	    (0x0B2A <= c && c <= 0x0B30) || (0x0B32 <= c && c <= 0x0B33) ||
	    (0x0B36 <= c && c <= 0x0B39) ||  c == 0x0B3D ||
	    (0x0B5C <= c && c <= 0x0B5D) || (0x0B5F <= c && c <= 0x0B61) ||
	    (0x0B85 <= c && c <= 0x0B8A) || (0x0B8E <= c && c <= 0x0B90) ||
	    (0x0B92 <= c && c <= 0x0B95) || (0x0B99 <= c && c <= 0x0B9A) ||
	     c == 0x0B9C                 || (0x0B9E <= c && c <= 0x0B9F) ||
	    (0x0BA3 <= c && c <= 0x0BA4) || (0x0BA8 <= c && c <= 0x0BAA) ||
	    (0x0BAE <= c && c <= 0x0BB5) || (0x0BB7 <= c && c <= 0x0BB9) ||
	    (0x0C05 <= c && c <= 0x0C0C) || (0x0C0E <= c && c <= 0x0C10) ||
	    (0x0C12 <= c && c <= 0x0C28) || (0x0C2A <= c && c <= 0x0C33) ||
	    (0x0C35 <= c && c <= 0x0C39) || (0x0C60 <= c && c <= 0x0C61) ||
	    (0x0C85 <= c && c <= 0x0C8C) || (0x0C8E <= c && c <= 0x0C90) ||
	    (0x0C92 <= c && c <= 0x0CA8) || (0x0CAA <= c && c <= 0x0CB3) ||
	    (0x0CB5 <= c && c <= 0x0CB9) ||  c == 0x0CDE ||
	    (0x0CE0 <= c && c <= 0x0CE1) || (0x0D05 <= c && c <= 0x0D0C) ||
	    (0x0D0E <= c && c <= 0x0D10) || (0x0D12 <= c && c <= 0x0D28) ||
	    (0x0D2A <= c && c <= 0x0D39) || (0x0D60 <= c && c <= 0x0D61) ||
	    (0x0E01 <= c && c <= 0x0E2E) ||  c == 0x0E30 ||
	    (0x0E32 <= c && c <= 0x0E33) || (0x0E40 <= c && c <= 0x0E45) ||
	    (0x0E81 <= c && c <= 0x0E82) ||  c == 0x0E84 ||
	    (0x0E87 <= c && c <= 0x0E88) ||  c == 0x0E8A || c == 0x0E8D  ||
	    (0x0E94 <= c && c <= 0x0E97) || (0x0E99 <= c && c <= 0x0E9F) ||
	    (0x0EA1 <= c && c <= 0x0EA3) ||  c == 0x0EA5 || c == 0x0EA7  ||
	    (0x0EAA <= c && c <= 0x0EAB) || (0x0EAD <= c && c <= 0x0EAE) ||
	     c == 0x0EB0                 || (0x0EB2 <= c && c <= 0x0EB3) ||
	     c == 0x0EBD                 || (0x0EC0 <= c && c <= 0x0EC4) ||
	    (0x0F40 <= c && c <= 0x0F47) || (0x0F49 <= c && c <= 0x0F69) ||
	    (0x10A0 <= c && c <= 0x10C5) || (0x10D0 <= c && c <= 0x10F6) ||
	     c == 0x1100                 || (0x1102 <= c && c <= 0x1103) ||
	    (0x1105 <= c && c <= 0x1107) ||  c == 0x1109 ||
	    (0x110B <= c && c <= 0x110C) || (0x110E <= c && c <= 0x1112) ||
	     c == 0x113C || c == 0x113E  ||  c == 0x1140 || c == 0x114C  ||
	     c == 0x114E || c == 0x1150  || (0x1154 <= c && c <= 0x1155) ||
	     c == 0x1159                 || (0x115F <= c && c <= 0x1161) ||
	     c == 0x1163 || c == 0x1165  ||  c == 0x1167 || c == 0x1169  ||
	    (0x116D <= c && c <= 0x116E) || (0x1172 <= c && c <= 0x1173) ||
	     c == 0x1175 || c == 0x119E  ||  c == 0x11A8 || c == 0x11AB  ||
	    (0x11AE <= c && c <= 0x11AF) || (0x11B7 <= c && c <= 0x11B8) ||
	     c == 0x11BA                 || (0x11BC <= c && c <= 0x11C2) ||
	     c == 0x11EB || c == 0x11F0  ||  c == 0x11F9 ||
	    (0x1E00 <= c && c <= 0x1E9B) || (0x1EA0 <= c && c <= 0x1EF9) ||
	    (0x1F00 <= c && c <= 0x1F15) || (0x1F18 <= c && c <= 0x1F1D) ||
	    (0x1F20 <= c && c <= 0x1F45) || (0x1F48 <= c && c <= 0x1F4D) ||
	    (0x1F50 <= c && c <= 0x1F57) ||  c == 0x1F59 || c == 0x1F5B  ||
	     c == 0x1F5D                 || (0x1F5F <= c && c <= 0x1F7D) ||
	    (0x1F80 <= c && c <= 0x1FB4) || (0x1FB6 <= c && c <= 0x1FBC) ||
	     c == 0x1FBE                 || (0x1FC2 <= c && c <= 0x1FC4) ||
	    (0x1FC6 <= c && c <= 0x1FCC) || (0x1FD0 <= c && c <= 0x1FD3) ||
	    (0x1FD6 <= c && c <= 0x1FDB) || (0x1FE0 <= c && c <= 0x1FEC) ||
	    (0x1FF2 <= c && c <= 0x1FF4) || (0x1FF6 <= c && c <= 0x1FFC) ||
 	     c == 0x2126                 || (0x212A <= c && c <= 0x212B) ||
	     c == 0x212E                 || (0x2180 <= c && c <= 0x2182) ||
	    (0x3041 <= c && c <= 0x3094) || (0x30A1 <= c && c <= 0x30FA) ||
	    (0x3105 <= c && c <= 0x312C) || (0xAC00 <= c && c <= 0xD7A3) ||
	    /* Ideographic characters [86] */
	    (0x4E00 <= c && c <= 0x9FA5) || c == 0x3007 ||
	    (0x3021 <= c && c <= 0x3029)) {
	    return true;
	}

	return false;
    }

    /** Determine if character is a name character, as defined by [4].
     * @param c Unicode value of character
     * @return true if character is a name character
     */
    private static boolean isNameChar (int c)
    {
	if (isLetter (c) || isDigit (c) || c == PERIOD || c == HYPHEN ||
	    c == UNDERSCORE || c == COLON || isCombiningChar (c) ||
	    isExtender (c)) {
	    return true;
	}

	return false;
    }

    /** Determine if character is white space, as defined by [3].
     * @param c Unicode value of character
     * @return true if character is white space
     */
    private static boolean isWhitespace (int c)
    {
	if (c == SP || c == HT || c == CR || c == LF) {
	    return true;
	}

	return false;
    }

    /** Unescape any character entities or references in the attribute value.
     * @param value Attribute value
     * @return Unescaped equivalent value
     */
    private static StringBuffer unescape (StringBuffer value)
    {
	/* Unescape the standard entity references. */

	for (int i=0; (i = value.indexOf ("&amp;", i)) > -1; i++) {
	    value.replace (i, i+5, "&");
	}
	for (int i=0; (i = value.indexOf ("&apos;", i)) > -1; i++) {
	    value.replace (i, i+6, "'");
	}
	for (int i=0; (i = value.indexOf ("&gt;", i)) > -1; i++) {
	    value.replace (i, i+4, ">");
	}
	for (int i=0; (i = value.indexOf ("&lt;", i)) > -1; i++) {
	    value.replace (i, i+4, "<");
	}
	for (int i=0; (i = value.indexOf ("&quot;", i)) > -1; i++) {
	    value.replace (i, i+6, "\"");
	}

	/* Unescape any character references. */

	int i = 0;
	while ((i = value.indexOf ("&#", i)) > -1) {
	    int n = value.indexOf (";", i+2);
	    if (n > -1) {
		int charValue = 0;
		if (value.charAt (i+2) == 'x') { /* Hexadecimal value */
		    for (int k=i+3; k<n; k++) {
			int c = (char) value.charAt (k);
			if (ZERO <= c && c <= NINE) {
			    charValue = charValue*16 + c - ZERO;
			}
			else if (A <= c && c <= F) {
			    charValue = charValue*16 + c - A + 10;
			}
			else if (a <= c && a <= f) {
			    charValue = charValue*16 + c - a + 10;
			}
		    }
		}
		else {                           /* Decimal value */
		    for (int k=i+2; k<n; k++) {
			int c = (char) value.charAt (k);
			charValue = charValue*10 + c - ZERO;
		    }
		}
		value.replace (i, n+1, Character.toString ((char) charValue));
		i += 1;
	    }
	    else {
		/* Something has gone horribly wrong here; the character
		 * reference is not terminated. Should be of the form:
		 * "&#NNNN;" or &#xNNNN;", but the trailing semicolon is
		 * missing.
		 */
		break;
	    }
	}

	return value;
    }
}
