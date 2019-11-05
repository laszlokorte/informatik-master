(ns test
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]))

(m/set-current-implementation :vectorz)


(defn p [mu sigma x]
    (let [n (count x)
          inverse-sigma (m/inverse sigma)
          det-sigma (m/det sigma)
          two-pi (* 2 Math/PI)
          diff (m/sub x mu)
          exp (* -0.5 (m/mmul diff inverse-sigma diff))]
       (* (/ 1 (Math/sqrt (* (Math/pow two-pi n) det-sigma)))
          (Math/exp exp))))

(defn -main [file & args]
  (let [model (read-string (slurp file))
        feature-count (:feature-count model)
        sigma (:sigma model)
        phi (:phi model)
        mu-0 (:mu-0 model)
        mu-1 (:mu-1 model)
        lines (line-seq (clojure.java.io/reader *in*))
        rows (mapv #(mapv read-string (str/split %1 #" " feature-count)) lines)]
    (doseq [[c & x] rows]
        (let [prediction (if (> (p mu-1 sigma x) (p mu-0 sigma x)) 1 0)]
            (println "expected:" c "prediction: " prediction (if (= c prediction) "✅" "❌"))
        ))))
