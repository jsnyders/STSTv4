/*
This sample shows iterating over a list by applying a template to each item in the list
    $list : template(args)$
It also demonstrates an anonymous sub template
    {args| template-body}
It uses the default template expression delimiters (start stop characters) $,$.
Try this template with these commands:
    stst things things_song.json
    stst things things_drinks.json
To look at the template with the inspector
    stst -i things things_drinks.json
*/
things(header, things) ::= <<$header$:
$things : {t| * $t$
}$
>>