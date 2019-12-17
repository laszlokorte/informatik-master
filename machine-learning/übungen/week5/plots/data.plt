set terminal latex
set output "var/result.tex"

stats 'var/result.txt' using 2 name "ystats" nooutput

plot 'var/dataCircle.txt' using 1:($3==-1 ? $2 : (1/0)) title "" ps 2 pt 3, \
     'var/dataCircle.txt' using 1:($3==1 ? $2 : (1/0)) title "" ps 2 pt 4, \
     'var/result.txt' using 1:($3==-1 ? $2 : (1/0)) title "" ps 2 pt 2, \
     'var/result.txt' using 1:($3==1 ? $2 : (1/0)) title "" ps 2 pt 6, \

