# Strutture dati
Le principali strutture dati usate sono *HotelList* e *UserList*.

**_HotelList_** è usata per memorizzare le informazioni relativi agli hotel. Costituisce un wrapper attorno ad una ConcurrentHashMap, in aggiunta a dei metodi per effettuare operazioni utili come il riordinamento o la ricerca per nome e/o città, oppure per ottenere informazioni di interesse come i primi hotel classificati nei vari ranking locali.

L'idea iniziale era di usare una struttura simil-ArrayList sincronizzata e con un ordinamento totale, ma ho subito scartato questa via perchè la struttura non permetteva un accesso efficace in un contesto multi-threaded, dato che sarebbe stato necessario un lock all'intera struttura.
Anchè l'ordinamento stesso dell'ArrayList sarebbe stato troppo costoso e di complessità *O(N)*, con N il numero totale degli hotel.

Ho scelto di usare una ConcurrentHashMap come struttura alla base per risolvere questi problemi. Come chiave ho scelto la città dove l'hotel è situato, mentre il valore è un CopyOnWriteArrayList di Hotel situati nella città, lista che costituisce il ranking locale. 

Ho scelto un CopyOnWriteArrayList, invece di un normale ArrayList, dato che il primo è adatto a contesti in cui più thread accedono alla struttura. In ottica di scalabilità, con molti client è decisamente più probabile che le operazioni di ricerca (lettura) siano numericamente superiori a quelle di inserimento (modifica).


Con questa struttura la ricerca per città è molto più efficiente, dell'ordine di *O(1)* dato che non possono esistere conflitti di chiave. 

Discorso leggermente diverso per la ricerca tramite città e nome.
Purtroppo non è stato possibile usare una ricerca binaria dato che all'interno dei ranking locale, l'ordinamento non è basato sul nome dell'hotel. Per tale ragione la ricerca basata sul nome dell'hotel è di complessità *O(n<sub>i</sub>)*, con n<sub>i</sub> il numero degli hotel presenti nella città c<sub>i</sub>. 

Una possibile soluzione (non esplorata) potrebbe essere quella di mantenere una ulteriore struttura, ordinata sul nome degli hotel, ed usarla esclusivamente per effettuare ricerche; tuttavia sarebbe da valutare l'eventuale overhead in memoria al crescere del numero di hotel, nonchè andrebbe garantita la consistenza tra le due all'inserimento di una recensione.

Inoltre dato che l'operazione di *get()* può ritornare valori nulli, ho deciso di incapsulare i risulati delle operazioni di *search* in degli *Optional*, in modo tale da gestire esplicitamente anche il caso il cui il risulato sia assente ed evitare di operare con dei null-pointer.


Un discorso simile vale anche per **_UserList_**. La struttura ha lo scopo di memorizzare gli utenti registrati al sistema, ed effettuare operazioni quali l'aggiunta di un nuovo utente e la ricerca tramite username.

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

L'algoritmo sceeto per ricalcolare il voto dopo aver inserito una recensione è una *Exponential Moving Average (EMA)*: r<sub>next</sub> = α \* r<sub>inserted</sub> + (1-α) \* r<sub>prev</sub> , con α scelto pari a 0.3 per non causare sbalzi troppo repentini.

L'ordinamento invece è basto su....

- Multicast sender-receiver come thread separati
