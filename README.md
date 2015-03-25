# StringTemplate Standalone Tool - for StringTemplate v4

A simple command line utility to process a [StringTemplate](http://www.stringtemplate.org/) template with
a [JSON](http://json.org/) file supplying data for the template.

See this blog [post](http://hardlikesoftware.com/weblog/2007/04/26/on-learning-stringtemplate/) for motivation for
this tool. That post was written about StringTemplate v3 and the corresponding STST
but the motivation still applies to v4. The old v3 STST is still [available](http://hardlikesoftware.com/weblog/stst/).

This project is an update to STST so that it works with StringTemplate v4.

StringTemplate is both a language for describing text generation and a tool (template
engine) that implements that language and provides an API to connect the tool to another
program.

The StringTemplate language by design has no way to perform computation or evaluation.
It also has no general way to specify structural data. It is up to some driving program
(sometimes known as the controller) to produce and or make data available to the StringTemplate
template engine. The lack of readily available data makes learning the template language
difficult without also learning the StringTemplate API.

In theory one benefit of the separation of presentation from business logic is that
a project can be divided between people who work on the business logic and people who
work on the presentation. The people working on presentation don't need to be programmers.

Tutorials on StringTemplate tend to describe both the template syntax and how to invoke
it from the Java API at the same time. In some cases it is confusing to learn
two things at once and more importantly it excludes non-programmers from learning
StringTemplate.

In contrast, trying out XSLT is is easy (even if learning XSLT is hard) because all you need
is a text editor to create both your input documents and your stylesheet and a XSLT program
to process the stylesheet with the input documents.

The intent of this Standalone Tool is to make StringTemplate easy to try out without
having to program in Java (or some other language). It does this by providing a textual
data format that can be input to the tool along with a set of templates.

What is needed to go along with StringTemplate is a format for representing data. XSLT
uses XML as its data model. Another text format that is just as expressive is JSON.

This command line program takes as input a text file in JSON format, the name of a
template and optional group and produces an output file that results from processing the
named template with the data from the JSON file.

Comments, questions, or other feedback are welcome.

NOTES
1)
2) There is a basic renderer that can be used to try out the format option
   syntax. The formats are toUpper toLower lpad,nn rpad,nn 
   where nn is a the number of characters to pad. See the book samples.

## INSTALL INSTRUCTIONS
This is a Java program. You need java to run it. I used java 1.7 but other versions may work.

todo update install instructions

1) Download stst-v0.1.zip from www.hardlikesoftware.com

2) Unzip stst.zip into a folder of your choice. For example C:\util\stst
   This folder is referred as STST_HOME in the rest of the steps

3) In the STST_HOME folder copy stst.bat.init to stst.bat. 
   Then edit stst.bat and replace @HOME@ with the path for STST_HOME
   For example C:\util\stst

   NOTE: if you are using another OS such as Linux you should be able
   to figure out how to run the program by looking at stst.bat. 

4) Make sure JAVA_HOME is defined or java.exe can be found on the PATH.

5) (optional) copy stst.bat to a folder on the path or update the path
   to include the STST_HOME folder. 

6) From a command prompt type stst -h for usage. Then try out some of 
   the samples.

## USE
Type stst -h for help.

Just create some data files and template files.

Then run stst from the same directory or use the -d option to specify a different
directory.

See the samples folder. Here are some commands you can try in the samples folder
stst -h
stst hello hello.js
stst hello empty.js
stst things drinks.js
stst thingsBase.main things.js
stst thingsHTML.main things.js
stst -d -v things things.js
stst -a things-al things.js


## BUILDING
You don't need to build anything but if you want too...
You need a JDK and ant to build it.
Get the [source](https://github.com/jsnyders/STSTv4) and use ant to build.
