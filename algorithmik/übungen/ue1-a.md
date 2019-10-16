---
title: Algorithmik Blatt 1 Aufgabe 1
author: '6329857'
---

 

Was macht der Algorithmus?
--------------------------

`DoSomething(X,x,start,end)` sucht in dem Array `X` im Bereich zwischen Position
`start` und `end` nach dem Vorkommen des Elements, welches dem doppelten von `x`
entspricht, und gibt bei Erfolg die Position zurück, ansonsten 0.

Dafür wird der zu durchsuchende Bereich des Arrays in drei Drittel geteilt und
geprüft ob das gesuchte Element am Ende des ersten Drittels oder am Ende des
zweiten Drittels liegt. Ist das der Fall, ist das Ergebnis gefunden. Liegt das
Gesuchte Element auf keinen dieser beiden Position werden die drei Drittel
jeweils nach dem gleichen Prinzip durchsucht, angefangen mit dem hinteren
Drittel. Der Algorithmus bricht nicht ab, wenn ein Ergebnis gefunden wurde,
sondern verarbeitet alle bereits ermittelten Drittel – sowie deren weitere
Zerteilung – bis zu Ende.

Rekursive Form
--------------

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
DoSomething(X, x, start, end)
    m_1 = floor(start + (end - start) / 3) // 1/3 Position
    m_2 = floor(start + 2 * (end - start) / 3) // 2/3 Position
    if start > end // Rekursionsabbruch
        return 0
    if X[m_1] == 2*x // Element gefunden
        return m_1
    else if X[m_2] == 2*x // Element gefunden
        return m_2
    else
        return max( // Rekursion, nur einer kann >0 sein
            DoSomething(X, x, m_2 + 1, end), // (R1)
            DoSomething(X, x, m_1 + 1, m_2 - 1), // (R2)
            DoSomething(X, x, start, m_1 - 1) // (R3)
        )
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

Rekurenzgleichung
-----------------

Der nichtrekursive Anteil der Funktion besteht nur aus einfachen arithmetischen
Operationen, benötigt also konstant (`c`) viel Zeit. Die Eingabegröße $$n$$
ergibt sich aus der Differenz Parametern `start` und `end`, da diese den zu
verarbeitenden Bereich angeben. Die Parameter `X` und `x` sind konstant. Der
Algorithmus teilt das Problem in 3 Teilprobleme und reduziert die Problemgröße
dabei von $$n$$ auf $$\frac{n}{3} - 1$$ bzw. auf $$\frac{n}{3} - 2$$. Denn sei
$$n = end - start$$ ergibt sich für Aufruf R1 $$n = end - (m_2 + 1) = (end -
start) / 3 - 1$$, für R2 $$n = (m_2 - 1) - (m_1 + 1) = (end - start) / 3 - 2$$
und für R3 $$n = (m_1 - 1 - start) = (end - start) / 3 - 1$$. Die
Abbruchbedingung ist dass $$start > end$$ ist, also dass $$n < 0$$ ist.

Daraus ergibt sich die Rekurenzgleichung:

$$
T(n) = c wenn n < 0
T(n) = 3 * T(n / 3 - q) + c wenn n ≥ 0
$$

Wobei $$q$$ je nach Rekursionschritt 1 oder 2 ist, was aber wie wir sehen werden
keinen Unterschied macht.

Um das Mastertheorem verwenden zu können, bringen wir die Gleichung in die
richtige Form:

$$
T(n) = c wenn n < 0
T(n) = 3 * T((n - 3 * q) / 3) + c wenn n ≥ 0
$$

Sei $$m = n - 3 * q$$

$$
T(m + 3 * q) = 3 * T(m / 3) + c
S(m) = T(m + 3 * q)
S(m) = 3 * S(m / 3) + c
$$

Nun hat die Rekurenzgleichung die Passende Form um das Mastertheorem (MT)
anzuwenden. Da die Parameter $$a$$ und $$b$$ des Mastertheorems 3 sind und der
nichtrekursive Teil konstant ist, tritt Fall 1 des MT ein, wonach der rekursive
Anteil dominiert und sich $$S(m) = \theta(N^{log_3{3}}) = \theta(m) = \theta(n -
(3 * q))$$ ergibt. Bisher hatten wir keinen Wert für $$q$$ gewählt, doch nun hat
sich sowieso ergeben, dass es nur als konstanter Summand auftritt, also
vernachlässigt werden kann.

 

 
