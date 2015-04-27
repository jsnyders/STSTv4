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

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.*;

import org.stringtemplate.v4.AttributeRenderer;

public class JavaScriptFormatRenderer implements AttributeRenderer
{

    static HashSet<String> reservedWords = new HashSet<String>();
    static Pattern ident = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*");

    {
        reservedWords.add("delete");
        reservedWords.add("default");
        reservedWords.add("else");
        reservedWords.add("if");
        reservedWords.add("new");
        reservedWords.add("this");
        // more ???
    }

    private String escapeString(String str)
    {
        // this should be much better
        return str.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\\", "\\\\");
    }

    public String toString(Object o, String formatName, Locale locale)
    {
        if (formatName == null || formatName.equals("none"))
        {
            return o.toString();
        }
        else if (formatName.equals("string"))
        {
            return escapeString(o.toString());
        }
        else if (formatName.equals("key"))
        {
            boolean quoteIt = false;
            String value = o.toString();
            
            if (reservedWords.contains(value)) {
                quoteIt = true;
            } else {
                Matcher m = ident.matcher(value);
                if (!m.matches()) {
                    quoteIt = true;
                }
            }
            if (quoteIt) {
                return "\"" + escapeString(value) + "\"";
            } // else
            return value;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported format name");
        }
    }

}
