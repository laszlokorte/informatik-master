set terminal latex
set output "var/error.tex"

plot 'var/iterations.txt' using 1:2 w l title "error"
