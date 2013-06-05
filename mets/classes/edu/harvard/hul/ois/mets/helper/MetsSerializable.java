/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Serializable interface.
 * @author Stephen Abrams
 * @version 1.1 2002/Dec/23
 */
public interface MetsSerializable
{
    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Get this object's content model.
     * @return Content model
     */
    public List getContent ();

    /**
     * Serialize this object and its content model.
     * @param w Writer
     */
    public abstract void write (MetsWriter w)
	throws MetsException;

    /**
     * Deserialize this object and its content model.
     * @param r Reader
     */
    public abstract void read (MetsReader r)
	throws MetsException;
}
