/*
 [The "BSD licence"]
 Copyright (c) 2015, John Snyders
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package jjs.stst;

import java.io.*;

/**
 * A few utility functions to support STStandaloneTool
 * 
 * @author John Snyders
 *
 */
public class FileUtil
{
    protected static final int BLKSIZE = 8192;


    /**
     * Copies the contents of the given file to a string.
     * @param file the contents of this file are copied to a string
     * @param encoding the character encoding of the file contents
     * @return contents of the file as a string
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getFileContentAsString(File file, String encoding)
        throws FileNotFoundException, IOException
    {
        FileInputStream fis = new FileInputStream(file);
        return getStreamAsString(fis, encoding);
    }

    /**
     * Copies the contents of the given stream to a string.
     * @param is the stream to copy to a string
     * @param encoding the character encoding of the input stream
     * @return contents of the stream as a string
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getStreamAsString(InputStream is, String encoding)
    throws FileNotFoundException, IOException
    {
        Reader r = null;
        if (encoding == null)
        {
            r = new InputStreamReader(is);
        }
        else
        {
            r = new InputStreamReader(is, encoding);
        }
        try
        {
            StringWriter w = new StringWriter();
            char[] buffer = new char[BLKSIZE];
    
            int len;
            while ((len = r.read(buffer)) != -1)
            {
                w.write(buffer, 0, len);
            }
    
            return w.toString();
        }
        finally
        {
            r.close();
        }
    }
}