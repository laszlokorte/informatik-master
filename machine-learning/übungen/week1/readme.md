# Week 2

## Generate data

```
clj -m gen-data > var/points.txt
```

## Running the algorithm

```
cat var/points.txt | clj -m aproximate 0.2 1500 > var/iterations.txt
```

## Plotting the data (latex)

```
gnuplot plots/data.plt
gnuplot plots/error.plt
```

## Animated plot

```
cat var/iterations.txt | php plots/animate.plt.php 10 | gnuplot
```
