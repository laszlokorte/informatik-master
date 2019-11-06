# Week 3

Gaussian Discriminant Analysis

## Extract Features from image data

```
$ clj -m extract-features data/positives data/negatives > var/features.txt
```

## Analyse extracted features to generate model

```
$ cat var/features.txt | clj -m analyse > var/model.txt
```

# Use Model for prediction

```
$ cat var/features.txt | clj -m test var/model.txt
```
