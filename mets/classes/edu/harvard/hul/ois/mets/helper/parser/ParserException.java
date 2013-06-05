package edu.harvard.hul.ois.mets.helper.parser;

public class ParserException
    extends Exception
{
    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    private Context _context;

    /******************************************************************
     * CLASS CONSTRUCTOR.
     ******************************************************************/

    public ParserException (String message)
    {
	super (message);
	_context = null;
    }

    public ParserException (String message, Context context)
    {
	super (message + "; saw '" + context.getCharacter () + "' at line " +
	       context.getLineNumber ()   + ", column " +
	       context.getColumnNumber () + ", level " +
	       context.getNestingLevel () + ", state " +
	       context.getState ().toString ());
	_context = context;
    }

    /******************************************************************
     * PUBLIC INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    public Context getContext ()
    {
	return _context;
    }
}
