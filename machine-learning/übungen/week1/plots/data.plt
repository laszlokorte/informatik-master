set terminal latex
set output "var/data.tex"

set xrange [-0.1:1.1]
set yrange [-1.5:1.5]

load 'var/polynomial.plt'

polynomial(x) = a*x**0 + b*x**1 + c*x**2 + d*x**3

plot "var/points.txt" using 1:2 title "data", \
    polynomial(x) lw 4 lt -1 title "$f(x)$", \
    sin(2 * pi * x) lw 2  title "$sin(2\\pi x)$"
