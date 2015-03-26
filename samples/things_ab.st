/*
This sample is the same as things.st except it uses <,> (angle brackets) as the start stop delimiter characters.
Try this template with these commands:
    stst -s "<>" things things_song.json
    stst -s "<>" things things_drinks.json
*/
things_ab(header, things) ::= <<
<!This is a comment!>
<header>:
<things : {t | * <t>
}>
>>
