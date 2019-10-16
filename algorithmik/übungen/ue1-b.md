---
title: Algorithmik Blatt 1 Aufgabe 2
author: '6329857'
---

 

Modulo Schleifeninvariante
--------------------------

Als Schleifeninvariante gilt $$r = x - q * y$$ und $$q ≥ 0$$.

### Initialisierung

-   $$q = 0$$, $$r = x$$, also $$x = x - 0 * y$$ und $$q ≥ 0$$

### Fortsetzung

-   Angenommen $$r = x - q * y$$

-   Die Schleife setzt $$r' = r - y$$ und $$q' = q + 1$$

-   Umgeformt: $$r = r' + y$$ und $$q = q' - 1$$

-   Einsetzen in Annahme: $$r' + y = x - (q' - 1) * y = x - q' * y + y$$

-   Also gilt $$r' = x - q' * y$$ und $$q ≥ 0$$ auch am Ende der Schleife

### Terminierung

Wenn die Schleife endet ist $$r < y$$ und $$x = q * y + r$$. Das ist genau die
Definition von $$x % y$$.

Primzahl Schleifeninvarianz
---------------------------

Als Schleifeninvarianten gelten a) $$forall 0 < p < i^2: (P[p] -> forall z <= n:
not P[p * z])$$ und b) $$forall 0 < p < i*i: (isPrime(p) -> P[p])$$. Sie ließen
sich auch in einer gemeinsamen Formel ausdrücken, allerdings ist es
übersichtlicher sie getrennt zu zeigen

### Initialisierung (a)

Zu beginn ist $$P[1] = false$$ und $$i = 2$$. Also einziges $$p$$ ist 1 zu
prüfen. Das Antezedens des Konditionals ist falsch, die Invariante gilt.

### Initialisierung (b)

Da das Array mit true initialisiert wird, gilt das Konditional in Invariante b
immer.

### Fortsetzung (a)

Die Schleife erhöht $$i$$ in jedem Durchlauf um 1. Die Invariante muss also nach
jedem durchlauf für größere $$p$$ gelten sowie auch für alle niedrigeren $$p$$,
für die sie schon im Vorherigen Durchlauf galt. Bis auf das Inkrement wird $$i$$
nicht verändert.

Die Invariante besteht im Kern aus einem Konditional. Sie könnte also nur
dadurch verletzt werden, dass das Antezedens wahr, aber das Konsequenz falsch
ist. Um die Invariante zu verletzen, müsste also gelten, dass es ein $$p < i*i$$
und ein $$z ≤ n$$ gibt für die gilt $$P[p] ∧ P[p * z]$$.

In jedem Durchlauf der äußeren Schleife wird aber nur $$P[≥i^2]$$ beschrieben.
Der linke Operand kann durch die Operationen der also Schleife nicht wahr
werden. Außerdem wird das Array nur mit $$false$$ Werten beschrieben, also kann
auch der rechte Operand nicht wahr werden. Es ist also nicht möglich eine
Belegung zu finden, die die Invariante verletzt.

### Fortsetzung (b)

Der Algorithmus beschreibt nur Felder des Arrays deren Index ein vielfaches
($$>1$$) von $$i$$ sind. Also wird das Konsequenz von Invariante b niemals mit
$$false$$ belegt.

### Terminierung (a)

Die Schleife terminiert mit $$i^2 > n$$, also gilt $$forall 0 < p < n: (P[p] ->
forall z <= n: not P[p * z])$$. Der Algorithmus hat also alle Felder mit
$$false$$ belegt, deren Index $$A$$ ein vielfaches eines Index $$B$$ ist, dessen
Feld $$P[B] = true$$ ist.

### Terminierung (b)

Da im Verlauf nur Felder mit ganzzahlig-vielfachen Indizes beschrieben wurde,
sind alle Primzahlfelder immer noch mit der initialen Belegung von $$true$$
belegt.

### Terminierung

Also sind genau die Primzahlfelder im Array mit $$true$$ und alle anderen mit
$$false$$ markiert.
