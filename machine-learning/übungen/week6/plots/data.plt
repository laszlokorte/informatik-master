plot 'var/centroids.txt' index 0 u 1:2  title "Centroids",\
   for [IDX=1:3] 'var/centroids.txt' index IDX u 1:2 title "Points"
