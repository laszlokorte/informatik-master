(ns aproximate
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]))

; vectoru is needed for matrix inverse
(m/set-current-implementation :vectorz)


(defn noise [ampl]
  "generate random value in [-ampl, ampl]"
  (- (rand (* 2 ampl))
     ampl))

(defn powers [x degree]
  "list of all integer powers of x up to degree"
  (->> (range)
       (map #(Math/pow x %1))
       (take (inc degree))
       (vec)))

(defn error [points theta]
  "error of the polynomial theta in respect to the points"
  (->> points
       (map #(Math/pow (- (m/dot theta (:x %1))
                          (:y %1))
                       2))
       (reduce +)
       (* 0.5)))

(defn gradient-descent [points alpha theta]
  "gradient descent algorithm"
  "alpha is the the learning rate"
  "theta is the initial vector of coefficients"
  (->> points
       (reduce
        (fn [t d]
          (m/add-scaled t
                        (:x d)
                        (* alpha
                           (- (:y d)
                              (m/dot t (:x d))))))
        theta)))

(defn find-polynomial [points degree learning-rate]
  "given a list of points find a polynomial of degree"
  "using gradient descent algorithm with learning-rate"
  "returns a sequence of steps {:coefficients :error}"
  (->> #(noise 0.5)
         ; initialize coefficients
         (repeatedly)
         (take (inc degree))
         (vec)
         ; apply gradient descent
         (iterate #(gradient-descent points learning-rate %1))
         (map-indexed (fn [i coefficients] {:coefficients coefficients
                                            :index i
                                            :error (error points coefficients)}))
         ))

(defn break-on-threshold [seq threshold min]
  (->> seq
       (partition 2 1)
       (take-while (fn [[a b]]
                     (or
                      (< (:index a) min)
                      (> (/ (:error b) (:error a)) 1)
                      (> (- 1 threshold)
                         (/ (:error b) (:error a))))))
       (map second)))



(defn polynomial-string [coefficients]
  "converts list of coefficients into a string representing the polynomial"
  (->> coefficients
       (map-indexed #(format "%s*x**%d" (str %2) %1))
       (str/join " + ")))



(defn -main [arg-degree arg-learning-rate arg-limit & args]
  (let [degree (read-string arg-degree)
        learning-rate (read-string arg-learning-rate)
        limit (read-string arg-limit)
        lines (line-seq (clojure.java.io/reader *in*))
        rows (mapv #(mapv read-string (str/split %1 #" " 2)) lines)
        rows' (filterv #(= 2 (count %1)) rows)
        points (mapv (fn [[x y]] {:x (powers x 3) :y y}) rows')
        iterations (as-> points v
                     (find-polynomial v degree learning-rate)
                   #_(break-on-threshold v error-threshold 10)
                     (take limit v))]
    (println "# iteration error" (str/join " " (map #(format "x%d" %1) (range 0 (inc degree)))))
    (doseq [it iterations]
        (println (:index it) (:error it) (str/join " " (:coefficients it))))))

