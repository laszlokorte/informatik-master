set xrange [-0.1:1.1]
set yrange [-1.5:1.5]
h_0(x) = 0.09508857560955108*x**0 + 0.16478199198282317*x**1 + -0.1883442613787426*x**2 + -0.35372302412664836*x**3
h(x) = -0.07140564108537509*x**0 + 10.952921101300655*x**1 + -32.34188356639234*x**2 + 21.667181853728856*x**3
plot h_0(x), h(x), "var/points.txt" title "data"
#error:  1.7376543684609538
