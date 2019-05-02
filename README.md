# StringTemplate Standalone Tool - for StringTemplate v4

A simple command line utility to process a [StringTemplate](http://www.stringtemplate.org/) template with a [JSON](http://json.org/) file supplying data for the template.

See this blog [post](http://hardlikesoftware.com/weblog/2007/04/26/on-learning-stringtemplate/) for motivation for this tool.
That post was written about StringTemplate v3 and the corresponding STST but the motivation still applies to v4.
The old v3 STST is still [available](http://hardlikesoftware.com/weblog/stst/).

This project is an update to STST so that it works with StringTemplate v4.

StringTemplate is both a language for describing text generation and a tool (template engine) that implements that
language and provides an API to connect the tool to another program.

The StringTemplate language by design has no way to perform computation or evaluation. It also has no general way to
specify structural data. It is up to some driving program to produce and or make data available to the StringTemplate
template engine. The lack of readily available data makes learning the template language difficult without also
learning the StringTemplate API.

In theory one benefit of the separation of presentation from business logic is that a project can be divided between
people who work on the business logic and people who work on the presentation. The people working on presentation
don't need to be programmers.

Tutorials on StringTemplate tend to describe both the template syntax and how to invoke it from the Java API at the
same time. In some cases it is confusing to learn two things at once and more importantly it excludes non-programmers
from learning StringTemplate.

In contrast, trying out XSLT is is easy (even if learning XSLT is hard) because all you need is a text editor to create
both your input documents and your stylesheet and a XSLT program to process the stylesheet with the input documents.

The intent of this Standalone Tool is to make StringTemplate easy to try out without having to program in Java (or some
other language). It does this by providing a textual data format that can be input to the tool along with a set of templates.

This command line program takes input from a text file in JSON format, the name of a template and optional group and
produces an output file that results from processing the named template with the data from the JSON file.

Comments, questions, or other feedback are welcome.

NOTES:

 * There is a basic renderer that can be used to try out the format option syntax. The formats are toUpper, toLower,
 lpad,nn rpad,nn where nn is a the number of characters to pad. See the book samples.
 * In version 0.4.1 you need to use the -f basic option to get the basic renderer. There is also -f javascript
 renderer that supports format options "string" and "key"

## INSTALL INSTRUCTIONS
This is a Java program. You need java to run it. I used java 1.7 but other versions may work. Make sure java can be
found on your path.

 1 Download [stst-0.4.1.zip](http://www.hardlikesoftware.com/weblog/download/stst-0.4.1.zip)

 2 Unzip stst-0.4.1.zip into a folder of your choice. For example C:\util\stst or ~/util/stst

   This folder is referred as STST_HOME in the rest of the steps

 3 Edit the script used to run the tool

For Windows

In the STST_HOME folder copy stst.bat.init to stst.bat.
Then edit stst.bat and replace <home> with the path for STST_HOME
You can copy stst.bat to somewhere on your path or update the path to include the STST_HOME folder.

For Linux

In the STST_HOME folder copy stst.sh.init to stst.sh.
Then edit stst.sh and replace <home> with the path for STST_HOME
You can copy stst.sh to somewhere on your path or update the path to include the STST_HOME folder.
Change the mode so it can be executed.
Optionally you can rename the file to "stst".

 4) From a command prompt type stst -h for usage. Then try out some of the samples in the samples folder.

## USE
Type stst -h for help.

Depending on your OS and where you put the stst shell script and what you called it you may need to use
./stst.sh, stst.sh or give a relative path to it.

Just create some data files and template files.

Then run stst from the same directory or use the -d option to specify a different
directory.

See the samples folder. Here are some commands you can try in the samples folder

```
    stst -h
    stst hello hello.json
    stst hello hello_empty.json
    stst things things_drinks.json
    stst things_base.main things_drinks.json
    stst things_HTML.main things_drinks.json
    stst -i -v things things_song.json
    stst -s "<>" things_ab things_song.json
```

## Group and raw file examples

All these examples assume that you have a `data.json` file
in the current directory with this content:

```
{"foo":"bar"}
```

### Template group file

Create a file named `main.stg` with this content:

```
greeting(foo) ::= <<
hello <foo>
>>
```

Generate output:

```
stst -s "<>" main.greeting data.json
```

### Template group directory

Create a file named `greeting.st` with this content:

```
greeting(foo) ::= <<
hello <foo>
>>
```

Generate output:

```
stst -s "<>" greeting data.json
```

### Template group directory with raw templates

Create a file named `greeting.st` with this content:

```
hello <foo>
```

Generate output:

```
stst -s "<>" -r greeting data.json
```

### Template raw single file without a group

This mode is like `-r` but does not require the file name
to end with ".st". Group features will not work because this
option only reads the specified file. 

Create a file named `greeting.txt` with this content:

```
hello <foo>
```

Generate output:

```
stst -s "<>" -R greeting.txt data.json
```

## BUILDING
You don't need to build anything but if you want too...

You need a JDK and ant to build it. Get the [source](https://github.com/jsnyders/STSTv4) and use ant to build.

Starting with version 0.4.2, you can also use maven to build. When you update the version number in 
`build.properties`, run `ant version-number` to update the Java code and the `pom.xml` file. The ant
build includes this task automatically.
