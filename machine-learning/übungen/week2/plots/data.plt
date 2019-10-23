set terminal latex
set output "var/data.tex"

stats 'var/data.txt' using 2 name "ystats" nooutput

set yrange [ystats_min - 2:ystats_max + 2]

a_0 = -0.004745724038684598
b_0 = -0.008897108422270036
c_0 = 0.0019116669189833298

a = 21.84401335497923
b = -3.777551683915814
c = 8.617252026405144


f_0(x) = (a_0 + (b_0*x)) * (-1/c_0)
f(x) = (a + (b*x)) * (-1/c)

plot 'var/data.txt' using 1:($3==0 ? $2 : (1/0)) title "" ps 2 pt 1, \
     'var/data.txt' using 1:($3==1 ? $2 : (1/0)) title "" ps 2 pt 2, \
     f_0(x) with lines linestyle 7 title "$z_0(x) = 0$", \
     f(x) with lines linestyle 5 title "$z(x) = 0$"
