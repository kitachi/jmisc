/**********************************************************************
 * METS Java Toolkit
 * Copyright (c) 2003 by the President and Fellows of Harvard College
 **********************************************************************/

package edu.harvard.hul.ois.mets.helper;

import java.io.*;
import java.util.*;

/**
 * Class encapsulating a Base64-encoded content string.
 * For reference, see RFC 2045.
 * @author Stephen Abrams
 * @version 1.1 2003/Mar/19
 */
public class Base64
    implements MetsSerializable
{
    /******************************************************************
     * PRIVATE CLASS FIELDS.
     ******************************************************************/

    /** Content reader buffer size. */
    private static final int BUFFERSIZE = 8192;

    /** Map of 6-bit sextets to ASCII characters. */
    private static final char [] _base64 = {
	'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
	'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
	'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
	'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'
    };

    /******************************************************************
     * PRIVATE INSTANCE FIELDS.
     ******************************************************************/

    /** Base64 content model. */
    private List _content;

    /** Content byte stream. */
    private InputStream _stream;

    /******************************************************************
     * CLASS CONSRUCTOR.
     ******************************************************************/

    /**
     * Instantiate a <tt>Base64</tt> object.
     */
    public Base64 ()
    {
	init ();
    }

    /**
     * Instantiate a <tt>Base64</tt> object with encoded content.
     * @param content Encoded content
     */
    public Base64 (String content)
    {
	init ();
	_content.add (content);
    }

    /**
     * Instantiate a <tt>Base64</tt> object with a content stream.
     * Objects instantiated with this constructor must not attempt
     * to add content to the list returned by the <tt>getContent()</tt>
     * method.
     * @param reader Reader
     */
    public Base64 (InputStream stream)
    {
	_stream = stream;
	if (!(_stream instanceof BufferedInputStream)) {
	    _stream = new BufferedInputStream (_stream);
	}
    }

    /**
     * Initialize a <tt>PCData</tt> object.
     */
    private void init ()
    {
	_content = new ArrayList ();
    }

    /******************************************************************
     * INSTANCE METHODS.
     *
     * Accessor methods.
     ******************************************************************/

    /**
     * Get content model.
     * @return Null
     */
    public List getContent ()
    {
	return _content;
    }

    /******************************************************************
     * Mutator methods.
     ******************************************************************/

    /**
     * Add additional Base64-encoded content.
     * @param content Encoded content
     */
    public void add (String content)
    {
	_content.add (content);
    }

    /******************************************************************
     * Serializing/de-serializing methods.
     ******************************************************************/

    /**
     * De-serialize this element and its content model as Base64.  Reference
     * XML Schema Datatype description for base64Binary.  Note that any
     * whitespace characters ('\t','\r','\n',' ') are replaced with a space
     * and subsequently any continuous whitespace characters are collapsed
     * into a single space, per the XML Schema Datatype definition of
     * the base64Binary type.
     * @param w Writer
     */
    public void decode (MetsWriter w)
	throws MetsException
    {
        try {                              
            BufferedReader reader = null;
            if (_stream != null) {
                reader = 
                    new BufferedReader(new InputStreamReader(_stream));
            }
            else {
                if (_content != null) {
                    StringBuffer sb = new StringBuffer();
                    for (int i=0; i<_content.size (); i++) {
                        sb.append((String) _content.get(i));
                    }
                    reader = 
                        new BufferedReader(new StringReader(sb.toString()));
                }            
                else {
                    // no content or stream
                    return;
                }
            }
            
            char arr[] = new char[BUFFERSIZE];
            int len = 0;
            StringBuffer buffer = new StringBuffer();

            while ((len = reader.read(arr,0,BUFFERSIZE)) != -1) {

                String data = new String(arr,0,len);
                data = data.replace('\t',' ');
                data = data.replace('\n',' ');
                data = data.replace('\r',' ');

                buffer.append(data);

                // remove any leading spaces
                while (buffer.length() > 0) {
                    if (buffer.charAt(0) == ' ') {
                        buffer.delete(0,1);
                    }
                    else { break; }
                }

                // replace any continous spaces with a single space
                StringBuffer buffer2 = new StringBuffer();                
                for (int i=0;i<buffer.length();i++) {
                    if (buffer.charAt(i)   != ' ' ||
                        buffer.charAt(i-1) != ' ') {
                        buffer2.append(buffer.charAt(i));
                    }
                }
                
                StringTokenizer strtok = 
                    new StringTokenizer(buffer2.toString()," ",true);
                
                int tokenCount = strtok.countTokens();

                for (int i=0;i<tokenCount - 1;i++) {
                    String tok = strtok.nextToken();
                    if (!tok.equals(" ")) {
                        decode(w,tok);
                    }
                }
                
                // add last fragment or space to buffer              
                if (strtok.hasMoreTokens()) {
                    buffer = new StringBuffer(strtok.nextToken());
                }
                else {
                    buffer = new StringBuffer();
                }
            }

            if (buffer.length() > 0 && !buffer.toString().equals(" ")) {
                decode(w, buffer.toString());
            }
            
            reader.close();
        }
        catch (IOException e) {
            throw new MetsException (e.toString ());
        }
    }


    /**
     * De-serialize this element.  Meant for internal use only.  
     * Expects data to have no whitespaces and be a multiple of 4.
     *
     * @param w Writer
     * @param data valid chunk of base64 encoded characters with no
     *             whitespaces
     */
    private void decode (MetsWriter w, String data)
	throws MetsException 
    {

        // reverse lookup
        int map[] = new int[178];
        Arrays.fill(map,-1);
        for (int i=0; i<_base64.length;i++) {        
            map[_base64[i]]=i;
        }
        
        if (data.length() % 4 != 0) {
            throw new MetsException("base64 data not a factor of 4");
        }
        
        int chunks = data.length() / 4;
        int lastBytes = 0;                

        for (int j=0;j<chunks;j++) {
            // get next 4 char chunk
            char chunk[] = 
                data.substring(j*4, j*4 + 4).toCharArray(); 
            int octet[] = new int[3];
            int sextet[] = new int[4];
            
            // map from base64 char back to index
            sextet[0] = map[chunk[0]];
            sextet[1] = map[chunk[1]];
            sextet[2] = map[chunk[2]];
            sextet[3] = map[chunk[3]];
            
            // convert 4 6-bit chars to 3 8-bit ints
            octet[0] = (sextet[0]%64)*4;
            octet[0] += sextet[1]/16;
            
            octet[1] = (sextet[1]%16)*16;
            octet[1] += sextet[2]/4;
            
            octet[2] = (sextet[2]%4)*64;
            octet[2] += sextet[3];
            
            int len = 3;
            if (sextet[3] == -1) {
                if (sextet[2] == -1) {
                    // only first byte is valid, rest is padding
                    len = 1;
                }
                else {
                    // first two bytes are valid, rest is padding
                    len = 2;
                }
            }
            
            // convert int to byte
            byte bytes[] = new byte[3];
            bytes[0] = new Integer(octet[0]).byteValue();
            bytes[1] = new Integer(octet[1]).byteValue();
            bytes[2] = new Integer(octet[2]).byteValue();
            
            
            lastBytes += len;
            w.binary(bytes, 0, len);
        }
    }


    /**
     * Serialize this element and its content model as Base64.
     * @param w Writer
     */
    public void encode (MetsWriter w)
	throws MetsException
    {
	if (_stream != null) {
	    byte [] buffer = new byte[3];

	    /* Read the content byte stream 3 bytes at a time. */

	    int nBytes;
	    int nChars = 0;
	    try {
		while ((nBytes = _stream.read (buffer, 0, 3)) > 0) {
		    int [] octet = new int[4];
		    for (int i=0; i<nBytes; i++) {
			if (buffer[i] < 0) {
			    octet[i] = buffer[i] + 256;
			}
			else {
			    octet[i] = buffer[i];
			}
		    }

		    /* Build (up to) 4 6-bit sextets from the (up to) 3
		     * 8-bit octets.
		     */

		    int [] sextet = new int[4];
		    sextet[0] =  octet[0]/4;
		    sextet[1] = (octet[0]%4)*16;
		    int nSextets = 2;

		    if (nBytes > 1) {
			sextet[1] += octet[1]/16;
			sextet[2] = (octet[1]%16)*4;
			nSextets = 3;
			if (nBytes > 2) {
			    sextet[2] += octet[2]/64;
			    sextet[3] =  octet[2]%64;
			    nSextets = 4;
			}
		    }

		    /* Map the sextets to characters. */

		    StringBuffer output = new StringBuffer ();
		    for (int i=0; i<nSextets; i++) {
			output.append (_base64[sextet[i]]);
		    }
		    for (int i=nSextets; i<4; i++) {
			output.append ("=");
		    }
		    int len = output.length ();
		    if (nChars + len < 76) {
			w.text (output.toString ());
			nChars += len;
		    }
		    else {
			int n = nChars + len - 76;
			w.text (output.substring (0, n));
			w.text ("\n");
			w.text (output.substring (n));
			nChars = n;
		    }
		}
		_stream.close ();
	    }
	    catch (IOException e) {
		throw new MetsException (e.toString ());
	    }
	}
	else {
	    for (int i=0; i<_content.size (); i++) {
		w.text ((String) _content.get (i));
	    }
	}
    }

    /**
     * Instantiate a <tt>Base64</tt> object de-serialized from the input
     * stream.
     * @param r Reader
     * @return Base64 object
     * @throws MetsException De-serializing exception
     */
    public static Base64 reader (MetsReader r)
	throws MetsException
    {
	Base64 base64 = new Base64 ();
	base64.read (r);

	return base64;
    }

    /**
     * De-serialize this element and its content model.
     * @param r Reader
     */
    public void read (MetsReader r)
	throws MetsException
    {
	String content = r.getText ();
	_content.add (content);
    }

    /**
     * Serialize this element and its content model.  Calls encode().
     * @param w Writer
     */
    public void write (MetsWriter w)
	throws MetsException
    {
        encode(w);
    }
}
