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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.NoIndentWriter;
import org.stringtemplate.v4.ModelAdaptor;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STRawGroupDir;
import org.stringtemplate.v4.STWriter;
import org.stringtemplate.v4.misc.STMessage;
import org.stringtemplate.v4.misc.ErrorType;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;
import org.stringtemplate.v4.misc.STNoSuchAttributeException;

import st4hidden.org.antlr.runtime.Token;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * StringTemplate is both a language for describing text generation and a tool (template
 * engine) that implements that language and provides an API to connect the tool to another
 * program.
 * 
 * The StringTemplate language by design has no way to perform computation or evaluation.
 * It also has no general way to specify structural data. It is up to some driving program
 * (sometimes known as the controller) to produce and or make data available to the StringTemplate
 * template engine. The lack of readily available data makes learning the template language
 * difficult without also learning the StringTemplate API. 
 * 
 * In theory one benefit of the separation of presentation from business logic is that
 * a project can be divided between people who work on the business logic and people who
 * work on the presentation. The people working on presentation don't need to be programmers.
 * 
 * Tutorials on StringTemplate tend to describe both the template syntax and how to invoke 
 * it from the Java API at the same time. In some cases it is confusing to learn 
 * two things at once and more importantly it excludes non-programmers from learning
 * StringTemplate.
 * 
 * In contrast, trying out XSLT is is easy (even if learning XSLT is hard) because all you need
 * is a text editor to create both your input documents and your stylesheet and a XSLT program
 * to process the stylesheet with the input documents.  
 * 
 * The intent of this STStandaloneTool is to make StringTemplate easy to try out without
 * having to program in Java (or some other language). It does this by providing a textual
 * data format that can be input to the tool along with a set of templates. 
 * 
 * What is needed to go along with StringTemplate is a format for representing data. XSLT
 * uses XML as its data model. Another text format that is just as expressive is JSON.
 * 
 * This command line program takes as input a text file in JSON format, the name of a 
 * template and optional group xxx and produces an output file that results from processing the
 * named template with the data from the JSON file.
 * 
 * The command line syntax is
 * stst [<options>] [<group-name>.]<template-name> [<json-file>]
 *    options:
 *    -h                  display usage/help
 *    -n                  no indent
 *    -r                  (raw) templates files with no declarations
 *    -i                  debug templates using inspector GUI
 *    -d                  same as -i
 *    -v                  verbose
 *    -s <start-stop>     Start and stop characters that delimit template expressions.
 *                        Must be exactly two characters Example <>. Default is dollar signs.
 *    -e <encoding>       encoding for templates, json-file, and output
 *    -o <file>           output file if not specified use stdout
 *    -t <dir>            if not specified the current working directory is used
 * 
 * See usage for most up to date syntax
 * 
 * The STStandaloneTool class can be used for both simple template files (.st extension) and 
 * group files (.stg extension). 
 * 
 * TODO
 *  - handle group files
 *  - better error listener
 *  - update/test samples, examples, tests
 *  - automate build version and build number
 *  - independent encoding control for template, data, output
 *  - take some options from ENV VAR?
 *  - more testing: more error handeling, encodings
 * 
 * future: 
 *  support renderers
 *  interactive mode
 *  support more data formats: XML, CSV, YAML
 *  print diagnostic info
 *  servlet
 * 
 * @author John Snyders
 *
 */
public class STStandaloneTool
{
    private static final String VERSION = "0.4"; // keep in sync with version in build.xml
    private static final String RESOURCE_BUNDLE_NAME = "jjs.stst.ApplicationMessages";

    private static ResourceBundle resources = null;

    {
        resources = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
    }

    // group to hold templates when using simple templates or 
    // read group file into when using a group file
    private STGroup group = null;

    // a place to report errors
    private STSTErrorListener errorListener = null;

    //options
    private boolean noIndent = false;
    private boolean raw = false;
    private boolean debugMode = false;
    private boolean verboseMode = false;
    private char startChar = '$';
    private char stopChar = '$';

    // where to write template output
    private File outFile = null;

    // the JSON data used as input to the template
    private JSONObject data = null;

    public STStandaloneTool()
    {
        errorListener = new STSTErrorListener();
    }

    /**
     * Controls the output indenting
     * @return true if not indenting output and false otherwise
     */
    public boolean getNoIndent()
    {
        return noIndent;
    }

    /**
     * Controls the output indenting
     * @param noIndent true to not indent and false to indent output
     */
    public void setNoIndent(boolean noIndent)
    {
        this.noIndent = noIndent;
    }

    /**
     * Controls if the STRawGroupDir is used
     * @return true if using raw templates and false otherwise
     */
    public boolean isRaw()
    {
        return raw;
    }

    /**
     * Controls if the STRawGroupDir is used
     * @param raw true to use raw templates and false otherwise
     */
    public void setRaw(boolean raw)
    {
        this.raw = raw;
    }

    /**
     * Controls the StringTemplate start character Delimiter
     * @return char default is $
     */
    public char getDelimiterStartChar()
    {
        return startChar;
    }

    /**
     * Controls the StringTemplate stop character Delimiter
     * @return char default is $
     */
    public char getDelimiterStopChar()
    {
        return stopChar;
    }

    /**
     * Controls the StringTemplate start character Delimiter
     * @param startChar typically $ or <
     */
    public void setDelimiterStartChar(char startChar)
    {
        this.startChar = startChar;
    }

    /**
     * Controls the StringTemplate stop character Delimiter
     * @param startChar typically $ or >
     */
    public void setDelimiterStopChar(char stopChar)
    {
        this.stopChar = stopChar;
    }

    /**
     * Controls debug mode. In debug mode show the inspector GUI
     * @return true if in debug mode false otherwise
     */
    public boolean isDebugMode()
    {
        return debugMode;
    }

    /**
     * Controls debug mode. In debug mode show the inspector GUI
     * @param debugMode true for debug mode and false for no debugging
     */
    public void setDebugMode(boolean debugMode)
    {
        this.debugMode = debugMode;
    }

    /**
     * Controls verbose output mode.
     * @return true if in verbose mode false otherwise
     */
    public boolean isVerboseMode()
    {
        return verboseMode;
    }

    /**
     * Controls verbose output mode.
     * @param verboseMode true for verbose mode and false otherwise
     */
    public void setVerboseMode(boolean verboseMode)
    {
        this.verboseMode = verboseMode;
    }

    /**
     * The file to write template output to. If null then write output to standard output.
     * @return output file or null if none
     */
    public File getOutFile()
    {
        return outFile;
    }

    /**
     * The file to write template output to. If null then write output to standard output.
     * @param outFile output file or null to use standard output
     */
    public void setOutFile(File outFile)
    {
        this.outFile = outFile;
    }

    /**
     * The data the template will use
     * @return JSON internal representation of template input data
     */
    public JSONObject getData()
    {
        return data;
    }

    /**
     * Set template data with JSONObject
     * @param data template input data
     */
    public void setData(JSONObject data)
    {
        this.data = data;
    }

    /**
     * Set template data with a string in JSON format
     * @param jsonString template input data string
     */
    public void setData(String jsonString)
    {
        JSONObject data = null;
        try
        {
            data = new JSONObject(jsonString);
        }
        catch (JSONException je)
        {
            String format = resources.getString("JSONError");
            logError(MessageFormat.format(format, je.getLocalizedMessage()));
            throw new RuntimeException(); // exit
        }
        setData(data);
    }

    /**
     * Set template data from the contents of a file
     * @param f the file containing JSON format data
     * @param encoding the file encoding
     */
    public void setData(File f, String encoding)
    {
        String contents = null;
        try
        {
            contents = FileUtil.getFileContentAsString(f, encoding);
        }
        catch (FileNotFoundException fnfe)
        {
            String format = resources.getString("DataFileNotFound");
            logError(MessageFormat.format(format, f.getPath()));
            throw new RuntimeException(); // exit
        }
        catch (IOException ioe)
        {
            String format = resources.getString("ErrorReadingData");
            logError(MessageFormat.format(format, ioe.getLocalizedMessage()));
            throw new RuntimeException(); // exit
        }
        setData(contents);
    }

    /**
     * Set template data from a stream
     * @param is the stream containing JSON format data
     * @param encoding the stream encoding
     */
    public void setData(InputStream is, String encoding)
    {
        String contents = null;
        try
        {
            contents = FileUtil.getStreamAsString(is, encoding);
        }
        catch (IOException ioe)
        {
            String format = resources.getString("ErrorReadingData");
            logError(MessageFormat.format(format, ioe.getLocalizedMessage()));
            throw new RuntimeException(); // exit
        }
        setData(contents);
    }

    private class JSONAdaptor implements ModelAdaptor
    {
        @Override
        public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName)
            throws STNoSuchPropertyException
        {
            JSONObject jo = (JSONObject)o;
            Object value;

            if (property == null || !jo.has(propertyName))
            {
                throw new STNoSuchPropertyException(null, null, propertyName);
            }
            value = jo.get(propertyName);
            if (value instanceof JSONArray)
            {
                value = convertJSONArrayToArray((JSONArray)value);
            }
            return value;
        }

    }

    private Object[] convertJSONArrayToArray(JSONArray ja) {
        int i;
        Object array[] = new Object[ja.length()];

        for (i = 0; i < ja.length(); i++)
        {
            array[i] = ja.get(i);
        }
        return array;
    }

    /**
     * Set the group name of the main group when using a group file.
     * The setGroupLoader must be called first. xxx
     * 
     * @param groupName name of the group file to process without the extension
     */
    public void setGroup(String groupName)
    {
//xxx        group = STGroup.loadGroup(groupName);
        if (group == null)
        {
            // an error has already been given
            throw new RuntimeException(); // exit
        }
        initGroup();
    }

    /**
     * Set the group when using simple template files
     * All the template files must be under the given directory
     * 
     * Note: only one of setGroupLoader, setGroup(name) or setGroup(dir, encoding) should be called xxx
     * 
     * @param dir path directory containing templates
     * @param encoding encoding for any and all template files if null the 
     * system default encoding is used
     */
    public void setGroup(String dir, String encoding)
    {
        if (isDebugMode()) {
            STGroup.trackCreationEvents = true;
        }
        if (isRaw()) {
            group = new STRawGroupDir(dir, encoding, startChar, stopChar);
        } else {
            group = new STGroupDir(dir, encoding, startChar, stopChar);
        }
        initGroup();
    }

    /**
     * Call to generate output from the given template.
     * Call after setting desired options and after setting the data and group
     * 
     * @param templateName the template to process
     */
    public void generate(String templateName)
    {
        ST st = getTemplate(templateName);
        invokeTemplate(st);
    }

    /**
     * List the instance settings
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("StringTemplateStandaloneTool:\n");
        sb.append("  Group: ").append(group.getName()).append("\n");
        sb.append("  Raw: ").append(raw ? "yes" : "no").append("\n");
        sb.append("  Indent: ").append(noIndent ? "yes" : "no").append("\n");
        sb.append("  Verbose: ").append(verboseMode ? "yes" : "no").append("\n");
        sb.append("  Debug: ").append(debugMode ? "yes" : "no").append("\n");
        sb.append("  Start Char: ").append(startChar).append("\n");
        sb.append("  Stop Char: ").append(stopChar).append("\n");
        sb.append("  Output: ").append(outFile != null ? outFile.getName() : "<stdout>").append("\n");
        return sb.toString();
    }
    //
    // Implementation
    //

    private void initGroup()
    {
        group.setListener(errorListener);
        group.registerRenderer(String.class, new BasicFormatRenderer());
        group.registerModelAdaptor(JSONObject.class, new JSONAdaptor());
        STGroup.verbose = isVerboseMode();

        // TODO
        // support extensible renderers
    }

    private ST getTemplate(String templateName)
    {
        ST st = group.getInstanceOf(templateName);
        if (st == null)
        {
            String msg = MessageFormat.format(resources.getString("NoSuchTemplate"), templateName);
            logError(msg);
            throw new RuntimeException(); // exit
        }
        return st;
    }

    private void invokeTemplate(ST st)
    {
        JSONObject data = getData();
        // add top level attributes
        for (String k: JSONObject.getNames(data))
        {
            Object value = data.get(k);
            if (value instanceof JSONArray)
            {
                value = convertJSONArrayToArray((JSONArray)value);
            }
            try
            {
                st.add(k, value);
            }
            catch (Exception ex) // STNoSuchAttributeException nsae)
            {
                if (isVerboseMode())
                {
                    logError(MessageFormat.format(resources.getString("IgnoreAttribute"), k));
                }
            }
        }

        if (isDebugMode()) {
            st.inspect();
        }
        PrintWriter out = null;
        try
        {
            out = getOutputPrintWriter();
            STWriter writer = null;
            if (getNoIndent())
            {
                writer = new NoIndentWriter(out);
            }
            else
            {
                writer = new AutoIndentWriter(out);
            }
            st.write(writer);
            out.println();
            out.flush();
        }
        catch (Exception ex)
        {
            String msg = resources.getString("RuntimeError");
            logError(msg + " " + ex.getLocalizedMessage());
            throw new RuntimeException(); // exit
        }
        finally
        {
            if (outFile != null)
            {
                out.close();
            }
        }
    }

    private PrintWriter getOutputPrintWriter()
    {
        if (outFile != null)
        {
            try
            {
                return new PrintWriter(outFile);
            }
            catch (FileNotFoundException ex)
            {
                String format = resources.getString("OutputFileNotFound");
                logError(MessageFormat.format(format, outFile.getPath()));
                throw new RuntimeException(); // exit
            }
        }
        return new PrintWriter(System.out, true);
    }

    private class STSTErrorListener implements STErrorListener
    {
        @Override
        public void compileTimeError(STMessage msg)
        {
            report(resources.getString("CompileTimeError"), msg);
        }

        @Override
        public void internalError(STMessage msg)
        {
            logError("xxx internal error");
            report(resources.getString("InternalError"), msg);
        }

        @Override
        public void IOError(STMessage msg)
        {
            logError("xxx io error");
            report(resources.getString("IOError"), msg);
        }

        @Override
        public void runTimeError(STMessage msg)
        {
            report(resources.getString("RunTimeError"), msg);
        }

        private void report(String msgType, STMessage msg)
        {
            logError("xxx from error listener: " + msgType + " " + msg.error.toString() + " " + msg.toString()); // xxxMessageFormat.format(format, msg));
            String format = resources.getString("Warning"); //xxx
        }
    }

    private void logError(String message)
    {
        System.err.println(message);
    }

    /**
     * Parse the StringTemplateTool command line and execute it
     * 
     * @param args see usage
     */
    public static void main(String[] args)
    {
        STStandaloneTool stst = new STStandaloneTool();
        String templateSpec = null;
        String data = null;
        ArrayList<File> dirs = new ArrayList<File>();
        String encoding = System.getProperty("file.encoding");
        String startStop = "$$";
        boolean dirsParam = false;
        boolean outParam = false;
        boolean encodingParam = false;
        boolean startStopParam = false;

        int param = 0;
        for (String arg : args)
        {
            if (dirsParam)
            {
                dirsParam = false;
                File d = new File(arg);
                if (!d.isDirectory())
                {
                    String format = resources.getString("InvalidDirectory");
                    System.err.println(MessageFormat.format(format, arg));
                    continue;
                }
                dirs.add(d);
            }
            else if (outParam)
            {
                outParam = false;
                stst.setOutFile(new File(arg));
            }
            else if (encodingParam)
            {
                encodingParam = false;
                encoding = arg;
            }
            else if (startStopParam)
            {
                startStopParam = false;
                startStop = arg;
                if (startStop.length() != 2)
                {
                    String format = resources.getString("InvalidStartStop");
                    System.err.println(MessageFormat.format(format, arg));
                    continue;
                }
                stst.setDelimiterStartChar(startStop.charAt(0));
                stst.setDelimiterStopChar(startStop.charAt(1));
            }
            else if (arg.charAt(0) == '-') // handle options
            {
                if (arg.equals("-n"))
                {
                    stst.setNoIndent(true);
                }
                else if (arg.equals("-r"))
                {
                    stst.setRaw(true);
                }
                else if (arg.equals("-d"))
                {
                    stst.setDebugMode(true);
                }
                else if (arg.equals("-s"))
                {
                    startStopParam = true;
                }
                else if (arg.equals("-v"))
                {
                    stst.setVerboseMode(true);
                }
                else if (arg.equals("-h"))
                {
                    usage();
                    return;
                }
                else if (arg.equals("-t"))
                {
                    dirsParam = true;
                }
                else if (arg.equals("-o"))
                {
                    outParam = true;
                }
                else if (arg.equals("-e"))
                {
                    encodingParam = true;
                }
                else
                {
                    String format = resources.getString("UnknownOption");
                    System.err.println(MessageFormat.format(format, arg));
                }
            }
            else
            {
                switch (param)
                {
                case 0: // this is the template name
                {
                    templateSpec = arg;
                    break;
                }
                case 1:
                {
                    data = arg;
                    break;
                }
                default:
                {
                    String format = resources.getString("TooManyParameters");
                    System.err.println(MessageFormat.format(format, arg));
                    break;
                }
                }
                param++;
            }
        }
        
        if (param < 1)
        {
            String msg = resources.getString("TooFewParameters");
            System.err.println(msg);
            usage();
            return;
        }


        if (stst.isVerboseMode())
        {
            versionBanner();
        }

        // add cwd to list of dirs
        File cwd = new File(System.getProperty("user.dir"));
        dirs.add(cwd);

        try
        {
            String templateName = null;
            // check for group in templateSpec
            int dot = templateSpec.indexOf('.');
            if (dot != -1)
            {
                String groupName = templateSpec.substring(0, dot);
                templateName = templateSpec.substring(dot + 1);
    
                // add all directories to group loader
                StringBuilder sb = new StringBuilder();
                int i = 0;
                for (File dir : dirs)
                {
                    if (i > 0)
                    {
                        sb.append(":");
                    }
                    sb.append(dir.getPath());
                    i++;
                }
//xxx                stst.setGroupLoader(sb.toString(), encoding);
//xxx                stst.setGroup(groupName);
            }
            else
            {
                stst.setGroup(dirs.get(0).getPath(), encoding);
                templateName = templateSpec;
            }
            
            if (data != null)
            {
                stst.setData(new File(data), encoding);
            }
            else
            {
                stst.setData(System.in, encoding);
            }

            if (stst.isVerboseMode())
            {
                String settings = stst.toString();
                System.out.println(settings);
                STGroup.verbose = true;
            }
            long start = System.currentTimeMillis();
            stst.generate(templateName);
            long end = System.currentTimeMillis();
            if (stst.isVerboseMode())
            {
                double time = (double)(end - start) / 1000.0;
                String format = resources.getString("Timing");
                System.out.println(MessageFormat.format(format, String.valueOf(time)));
            }
        }
        catch (RuntimeException ex)
        {
            // the error was already logged 
            System.exit(1);
        }
    }

    public static void versionBanner()
    {
        String format = resources.getString("VersionBanner");
        System.out.println(MessageFormat.format(format, VERSION, ST.VERSION));
    }

    public static void usage()
    {
        versionBanner();
        String msg = resources.getString("Usage");
        System.out.println(msg);
    }

}
