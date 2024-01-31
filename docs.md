# Strutture dati
Le principali strutture dati usate sono *HotelList* e *UserList*.

HotelList è usata per memorizzare le informazioni relativi agli hotel. Costituisce un wrapper attorno ad una ConcurrentHashMap, in aggiunta a dei metodi per effettuare operazioni utili come il riordinamento o la ricerca per nome e/o città, oppure per ottenere informazioni di interesse come i primi hotel classificati nei vari ranking locali.

L'idea iniziale era di usare una struttura simil-ArrayList sincronizzata e con un ordinamento totale, ma ho subito scartato questa via perchè la struttura non prevedeva un accesso efficace in un contesto multi-threaded.
Anchè l'ordinamento stesso dell'ArrayList sarebbe stato troppo costoso e di complessità *O(N)*, con N il numero totale degli hotel.

Ho scelto di usare una ConcurrentHashMap come struttura alla base per risolvere questi problemi. Come chiave ho scelto la città dove l'hotel è situato, mentre il valore è un ArrayList di Hotel situati nella città, lista che costituisce il ranking locale. Con questa struttura la ricerca per città (con o senza nome) è molto più efficiente //TODO: esiste complessità in docs?
Inoltre dato che l'operazione di *get()* può ritornare valori nulli, ho deciso di incapsulare i risulati delle operazini di *search* in degli *Optional*, in modo tale da gesitre anche il caso il cui il risulato sia assente ed evitare di operare con dei null-pointer.

//TODO: argomentare perchè valore è ArrayList e non CopyOnWriteArray o simile.

Un discorso simile vale anche per *UserList*. La struttura ha lo scopo di memorizzare gli utenti registrati al sistema, ed effettuare operazioni quali l'aggiunta di un nuovo utente e la ricerca tramite username.

*UserList* ha come base una *TreeMap* sincronizzata. Ho scelto questa struttura per le proprietà e le garanzie che offre. Dalla documentazione sono garantite get() e put() di complessità *O(log n)*, e dato che sono proprio queste le operazioni più usate, tale struttura è la più adatta. Inoltre l'aggiunta è ordinata sulle chiavi, in questo caso l'username dell'utente. È proprio tale ordinamento che consente una ricerca efficiente.

Dato che ho imposto l'username sia univoco, non serve preoccuparsi di eventuali conflitti di chiave.

# Struttura del server
Sul server sono presenti due ThreadPool: un *CachedThreadPool* responsabile di gestire i client accettati ed uno *ScheduledThreadPool* responsabile di eseguire periodicamente la serializazione di hotel ed utenti, oltre al riordino dei ranking locali e l'eventuale notifica in multicast.

Per ogni client viene attivato un task *Session* che si occupa di soddisfare le richieste di quello specifico client.

# Struttura del client
A differenza del server, la struttura del client è molto più semplice e leggera. Il main thread si occupa dell'interazione utente: accogliere le richieste, inviarle al server e stampare il risultato. Un secondo thread viene attivato dopo il login dell'utente ed è all'ascolto di eventuali notifiche multicast, stampate non appena possibile l'accesso alla console.


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

L'algoritmo sceeto per gestire ricalcolare il voto dopo aver inserito una recensione è una *Exponential Moving Average (EMA)*: r<sub>next</sub> = α \* r<sub>inserted</sub> + (1-α) \* r<sub>prev</sub> , con α scelto pari a 0.3 per non causare sbalzi troppo repentini.

L'ordinamento invece è basto su....

- Multicast sender-receiver come thread separati
