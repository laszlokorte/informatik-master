# Week 2

## Running the algorithm

```
cat var/data.txt | clj -m logistic-regression 0.4 1000 > var/iterations.txt
```

## Plotting the data (latex)

```
gnuplot plots/data.plt
```

## Animated plot

```
cat var/iterations.txt | php plots/animate.plt.php 10 | gnuplot
```
