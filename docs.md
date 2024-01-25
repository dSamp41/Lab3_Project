# USER MANUAL

## Registration
register &lt;username&gt; &lt;password&gt;

## Login
login &lt;username&gt; &lt;password&gt;

## Logout
logout

## Searching
searchAllHotels &lt;città&gt;
searchHotel &lt;nome&gt; &lt;città&gt;

## Help


# Scelte di design
L'algoritmo scleto per gestire le recensioni è una *Exponential Moving Average (EMA)*: r_next = alpha \* review + (1-alpha) \* r_prev.
Ad ogni nuovo inserimento di recensione, il voto viene ricalcolato. Alpha è scelto pari a 0.3 per non causare sbalzi troppo repentini.

- Multicast sender-receiver come thread separati