/*
This is a comment
This is a very simple template with just one attribute substitution
The template is declared with hello(audience) ::=...
The name of the template is hello and it must match the name of the file.
The arguments that the template expects are given in ( ) after the name.
All the text within << >>> is the body of the template.
Within the template body text between $$ are template expressions.
Everything else is literal text to include in the output.
$audience;null="is anyone there?"$ is an attribute expression with the null option.
This substitutes the value of the data attribute named audience and if that value is not present or null then
the null option provides an alternative value.
Try this template with these commands:
    stst hello hello.json
    stst hello hello_empty.json
*/
hello(audience) ::= <<Hello $audience;null="is anyone there?"$!$!this is the syntax for a comment inside a template!$
>>
