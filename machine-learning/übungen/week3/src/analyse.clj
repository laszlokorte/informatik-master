(ns analyse
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]))

(defn -main [feature-count & args]
  (let [feature-count (inc (read-string feature-count))
        lines (line-seq (clojure.java.io/reader *in*))
        rows (mapv #(mapv read-string (str/split %1 #" " feature-count)) lines)
        rows' (filterv #(= feature-count (count %1)) rows)
        rows-0 (map rest (filter #(= (first %1) 0) rows'))
        rows-1 (map rest (filter #(= (first %1) 1) rows'))
        count-all (double (count rows'))
        count-0 (double (count rows-0))
        count-1 (double (count rows-1))
        phi (/ count-0 count-all)
        mu-0 (m/div (reduce m/add rows-0) count-0)
        mu-1 (m/div (reduce m/add rows-1) count-1)
        sigma-0 (map #(m/outer-product (m/sub %1 mu-0) (m/sub %1 mu-0)) rows-0)
        sigma-1 (map #(m/outer-product (m/sub %1 mu-1) (m/sub %1 mu-1)) rows-1)
        sigma (m/div (m/add (reduce m/add sigma-0) (reduce m/add sigma-1)) count-all)]
    (println {:phi phi :sigma sigma :mu-0 mu-0 :mu-1 mu-1 :feature-count feature-count})))
