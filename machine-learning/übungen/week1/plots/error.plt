set terminal latex
set output "var/error.tex"

plot 'var/iterations.txt' using 1:2 w l ls 5 title "error"
