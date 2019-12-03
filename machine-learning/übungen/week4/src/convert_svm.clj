(ns convert-svm
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]))

(defn -main [& args]
  (let [lines (line-seq (clojure.java.io/reader *in*))
        rows (vec (map #(map read-string (str/split %1 #" ")) lines))]

    (doseq [[name class & features] rows]
        (print (if (= class 1) "+1" "-1"))
        (doseq [[idx feature] (map-indexed vector features)]
          (print " ")
          (print (+ 1 idx))
          (print ":")
          (print feature))
        (println))))

