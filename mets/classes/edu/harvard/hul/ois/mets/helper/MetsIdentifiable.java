/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.util.*;

/**
 * Identifiable interface.
 * @author Stephen Abrams
 * @version 1.1 2003/Jan/06
 */
public interface MetsIdentifiable
{
    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     ******************************************************************/

    /**
     * Get the element ID.
     * @return Element ID
     */
    public String getID ();

    /**
     * Set the element ID
     * @param ID Element ID
     */
    public void setID (String ID);
}
