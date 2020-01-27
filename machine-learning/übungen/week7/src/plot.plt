set terminal pdf
set output "var/plot-reward.pdf"

# set multiplot layout 2, 1;
set key outside right center;

set xtics 0, 200, 1000

set title "Reward";

plot "var/result.txt" every :::0::0 with lp title "eps 0.1", \
"var/result.txt" every :::2::2 with lp title "eps 0.01", \
"var/result.txt" every :::4::4 with lp title "eps 0.0", \

set terminal pdf
set output "var/plot-best.pdf"

set title "Best Action Count";

plot "var/result.txt" every :::1::1 with lp title "eps 0.1", \
"var/result.txt" every :::3::3 with lp title "eps 0.01", \
"var/result.txt" every :::5::5 with lp title "eps 0.0", \
