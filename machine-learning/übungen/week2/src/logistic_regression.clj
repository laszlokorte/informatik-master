(ns logistic-regression
  (:require [com.hypirion.clj-xchart :as c]
            [clojure.core.matrix :as m]
            [clojure.string :as str]))

(use 'clojure.java.io)


(defn noise [ampl]
  "generate random value in [-ampl, ampl]"
  (- (rand (* 2 ampl))
     ampl))

(defn gradient-descent-step [data alpha theta]
  (->> data
       (reduce
        (fn [t d]
          (m/add-scaled
            t
            (:x d)
            (* alpha
               (- (:y d)
                  (/ 1
                  (+ 1 (Math/exp (* -1 (m/dot t (:x d))))))))))
        theta)))

(defn find-classifier [points degree learning-rate]
  (->> #(noise 0.01)
         ; initialize coefficients
         (repeatedly)
         (take (inc degree))
         (vec)
         ; apply gradient descent
         (iterate #(gradient-descent-step points learning-rate %1))))


(defn -main [& args]
  (let [lines (line-seq (clojure.java.io/reader *in*))
        rows (mapv #(mapv read-string (str/split %1 #" " 3)) lines)
        rows' (filterv #(= 3 (count %1)) rows)
        points (mapv (fn [[x0 x1 y]] {:x [1 x0 x1] :y y}) rows')
        steps (find-classifier points 2 0.1)
        result (nth steps 0)
        [t0 t1 t2] result]
    (println (format "(%f + (%f*x))(-1/%f)" t0 t1 t2))))


; final (22,701787 + (-3,897869*x))(-1/8,926598)
