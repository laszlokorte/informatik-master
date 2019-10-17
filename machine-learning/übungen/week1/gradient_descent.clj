(ns gradient-descent 
  (:require [com.hypirion.clj-xchart :as c]
            [clojure.core.matrix :as m]
            [clojure.string :as str]))

(m/set-current-implementation :vectorz) 

(defn sin-scaled [x]
  (Math/sin (* 2 Math/PI x)))

(defn sin-noised [x noise-factor]
  (let [noise (- (rand (* 2 noise-factor)) noise-factor)]
    (+ (sin-scaled x) noise)))

(defn gen-data [n noise-factor]
  (take n (map #(vector %1 (sin-noised %1 noise-factor)) (repeatedly rand))))

(defn series-from-vectors [v]
  (c/extract-series {:x first :y second} v))

(defn powers [x degree]
  (vec (take degree (map #(Math/pow x %1) (range)))))

(defn error [data theta hyp]
  (reduce + (map
    (fn [t d]
      (let [x (:x d)
            y (:y d)]
        (Math/pow (- (hyp theta x) y) 2)))
    theta
    data)))

(def test-data (map (fn [[x y]] {:x (powers x 3) :y y}) [[0 0] [1 1] [2 2] [3 3]]))

(defn gradient-descent [data alpha theta hyp]
  (reduce
   (fn [t d]
     (let [x (:x d)
           y (:y d)]
       (m/add-scaled t x
        (* alpha (- y (hyp t x))))))
   theta
   data))

(defn find-polynomial [points alpha degree]
  (let [data (map (fn [[x y]] {:x (powers x (inc degree)) :y y}) points)
        gen-random #(- (rand) 0.5)
        theta (vec (take (inc degree) (repeatedly gen-random)))
        hyp m/dot]
    (map 
    (fn [t] {:t t :error (error data t hyp)})
    (iterate #(gradient-descent data alpha %1 hyp) theta))))
    
(defn find-polynomial-matrix [points degree]
(let [X (map (fn [[x y]] (powers x (inc degree))) points)
      XT (m/transpose X)
      y (map second points)]
      (m/mmul (m/inverse (m/mmul XT X)) XT y)))

(defn polynomial-string [poly]
  (str/join " + " (map-indexed #(format "%s*x^%d" (str %2) %1) poly))
)

(defn draw [d s]
  (let [polynomial-degree d
        step-size s
        iteration-limit 4000
        data (vec (gen-data 100 0.3))
        x-range (range 0 1 0.01)
        sin (map #(vector %1 (sin-scaled %1)) x-range)
        iterations (vec (take iteration-limit (find-polynomial data step-size polynomial-degree)))
        errors (map :error iterations)
        polynomial (:t (last iterations))
        polynomial' (find-polynomial-matrix data polynomial-degree)
        aprox (map #(vector %1 (m/dot polynomial (powers %1 (count polynomial)))) x-range)
        aprox' (map #(vector %1 (m/dot polynomial' (powers %1 (count polynomial')))) x-range)]
    (println (polynomial-string polynomial))
    (println (polynomial-string polynomial'))
    (c/view (c/xy-chart
             {"Data Points"
              (assoc (series-from-vectors data)
                     :style {:render-style :scatter})
              "Aproximation (via Iteration)" 
              (assoc (series-from-vectors aprox)
                     :style {:marker-type :none})
              "Aproximation (via Matrix)"
              (assoc (series-from-vectors aprox')
                     :style {:marker-type :none})
              "Sine wave"
              (assoc (series-from-vectors sin)
                     :style {:marker-type :none})} {:title (format "Aproximation via polynomial of degree %d" polynomial-degree)})
            (c/xy-chart {
                "Error" {
                    :x (range (count errors)) 
                    :y errors 
                    :style {:marker-type :none}}} 
                {
                    :title (format "Error after n iterations, step size %f" step-size)
                    :x-axis {:title "Iterations"}
                    :y-axis {:title "Error"}}))))