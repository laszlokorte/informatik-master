# Week 3

Gaussian Discriminant Analysis

## Extract Features from image data

```
$ clj -m extract-features data/positives data/negatives > var/features.txt
```

## Convert Features to SVM format

```
$ cat var/features.txt | clj -m convert-svm > var/svm.txt
```
