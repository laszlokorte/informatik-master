# Week 2

## Result

![var/animation.gif](Animation of algorithm steps)

## Generate data

```
$ clj -m gen-data > var/points.txt
$ # generate 2d 100 data points
```

## Running the algorithm

```
$ cat var/points.txt | clj -m aproximate 3 0.2 1500 > var/iterations.txt
$ #                3-degree polynomial --^  ^   ^-- iteration limit
$ #                                         |
$ #                                   learning rate
```

## Plotting the data (latex)

```
$ gnuplot plots/data.plt
$ gnuplot plots/error.plt
```

## Animated plot

```
$ cat var/iterations.txt | php plots/animate.plt.php 10 | gnuplot
$ #   ^                    plot each 10th iteration -^
$ #   precomputed values
```

or

```
$ cat var/points.txt | clj -m aproximate 3 0.05 0 | php plots/animate.plt.php 100 | gnuplot
```
