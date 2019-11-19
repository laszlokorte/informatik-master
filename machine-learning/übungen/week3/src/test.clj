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
        rows (mapv #(mapv read-string (str/split %1 #" " (+ 2 feature-count))) lines)]
    (doseq [[name class & x] rows]
        (let [
            pp0 (p mu-0 sigma x)
            pp1 (p mu-1 sigma x)
            p0 (/ (* pp0 phi) (+ (* pp0 phi) (* pp1 (- 1 phi))))
            p1 (/ (* pp1 (- 1 phi)) (+ (* pp0 phi) (* pp1 (- 1 phi))))
            prediction (if (> p1 p0) 1 0)]
            (println name " - " "expected:" class "prediction: " prediction (if (= class prediction) "✅" "❌") "," p0 "," p1)
        ))))
