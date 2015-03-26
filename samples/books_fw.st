$! output in fixed with format with variable last column !$
$books : {it |
$it.isbn;format="lpad,18"$:$it.year;format="lpad,4"$:$it.pub;format="lpad,26"$:$it.title$
}$