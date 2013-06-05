/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003-2004 by the President and Fellows of Harvard College
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
 * This class encapsulates the &lt;<tt>agent</tt>&gt; element.
 * See &lt;http://www.loc.gov/standards/mets/&gt;.
 * @author Stephen Abrams
 * @version 1.3.2 2004-07-6
 */
public class Agent
    extends MetsIDElement
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Other agent role, if ROLE="OTHER". */
    private String _OTHERROLE;

    /** Other agent type, if TYPE="OTHER". */
    private String _OTHERTYPE;

    /** Pre-defined agent role. */
    private Role _ROLE;

    /** Pre-defined agent type. */
    private Type _TYPE;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    /**
     * Instantiate an <tt>Agent</tt> object.
     */
    public Agent ()
    {
	super ("agent");
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Return other agent role.
     * @return Role
     */
    public String getOTHERROLE ()
    {
	return _OTHERROLE;
    }

    /**
     * Return other agent type.
     * @return Type
     */
    public String getOTHERTYPE ()
    {
	return _OTHERTYPE;
    }

    /**
     * Return pre-defined agent role.
     * @return Role
     */
    public Role getROLE ()
    {
	return _ROLE;
    }

    /**
     * Return pre-defined agent type.
     * @return Type
     */
    public Type getTYPE ()
    {
	return _TYPE;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Set other agent role.
     * @param role Role
     */
    public void setOTHERROLE (String role)
    {
	_OTHERROLE = role;
    }

    /**
     * Set other agent type.
     * @param type Type
     */
    public void setOTHERTYPE (String type)
    {
	_OTHERTYPE = type;
    }

    /**
     * Set pre-defined agent role.
     * @param role Role
     */
    public void setROLE (Role role)
    {
	_ROLE = role;
    }

    /**
     * Set pre-defined agent type.
     * @param type Type
     */
    public void setTYPE (Type type)
    {
	_TYPE = type;
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * Instantiate a <tt>Agent</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Agent object
     * @throws MetsException De-serializing exception
     */
    public static Agent reader (MetsReader r)
	throws MetsException
    {
	Agent agent = new Agent ();
	agent.read (r);

	return agent;
    }

    /**
     * De-serialize the content of the instance document into this
     * element using the given reader.
     * @param r Reader
     * @throws MetsException I/O exception
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
	    String name  = attr.getLocalName ();
	    String value = attr.getValue ();

	    if (name.equals ("ID")) {
		setID (value);
	    }
	    else if (name.equals ("ROLE")) {
		setROLE (Role.parse (value));
	    }
	    else if (name.equals ("OTHERROLE")) {
		setOTHERROLE (value);
	    }
	    else if (name.equals ("TYPE")) {
		setTYPE (Type.parse (value));
	    }
	    else if (name.equals ("OTHERTYPE")) {
		setOTHERTYPE (value);
	    }
	    else {
		if (_attrs == null) {
		    _attrs = new Attributes ();
		}
		_attrs.add (new Attribute (attr.getQName (), value, '"'));
	    }
	}

	if (r.atStart ("name")) {
	    Name name = Name.reader (r);
	    _content.add (name);
	}
	while (r.atStart ("note")) {
	    Note note = Note.reader (r);
	    _content.add (note);
	}

	r.getEnd (_localName);
    }

    /**
     * Serialize this element and its content model using the given
     * writer
     * @param w Writer
     * @throws MetsException I/O exception
     */
    public void write (MetsWriter w)
	throws MetsException
    {
	w.start (_qName);
	if (_ID != null) {
	    w.attribute ("ID", _ID);
	}
	w.attribute ("ROLE", _ROLE.toString ());
	if (_OTHERROLE != null) {
	    w.attribute ("OTHERROLE", _OTHERROLE);
	}
	if (_TYPE != null) {
	    w.attribute ("TYPE", _TYPE.toString ());
	}
	if (_OTHERTYPE != null) {
	    w.attribute ("OTHERTYPE", _OTHERTYPE);
	}
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
	if (_ROLE == null) {
	    throw new MetsException ("No agent ROLE");
	}
	else {
	    if (_ROLE.toString ().equals ("OTHER")) {
		if (_OTHERROLE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No agent OTHERROLE");
		}
	    }
	    else {
		if (_OTHERROLE != null) {
		    /*throw new MetsException*/
		    System.err.println ("Agent OTHERROLE only valid " +
					     "with ROLE=\"OTHER\"");
		}
	    }
	}
	
	if (_TYPE != null) {
	    if (_TYPE.toString ().equals ("OTHER")) {
		if (_OTHERTYPE == null) {
		    /* Don't throw an exception as this is a violation of
		     * METS semantics, not the METS Schema.
		     * SLA 2004-07-6
		     */
		    /*throw new MetsException*/
		    System.err.println ("No agent OTHERTYPE");
		}
	    }
	    else {
		if (_OTHERTYPE != null) {
		    /*throw new MetsException*/
		    System.err.println ("Agent OTHERTYPE only valid " +
					     "with TYPE=\"OTHER\"");
		}
	    }
	}

	if (_content == null || _content.isEmpty ()) {
	    throw new MetsException ("No agent name");
	}

	_valid = true;
    }
}
