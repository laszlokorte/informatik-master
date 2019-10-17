(ns gradient-descent 
  (:require [com.hypirion.clj-xchart :as c]
            [clojure.core.matrix :as m]))

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

(defn find-polynomial [points rate degree]
  (let [data (map (fn [[x y]] {:x (powers x (inc degree)) :y y}) points)
        alpha rate
        theta (vec (take (inc degree) (repeatedly #(rand 0.5))))
        hyp m/dot]
    (map 
    (fn [t] {:t t :error (error data t hyp)})
    (iterate #(gradient-descent data alpha %1 hyp) theta))))
    
(defn find-polynomial-matrix [points degree]
(let [X (map (fn [[x y]] (powers x (inc degree))) points)
      XT (m/transpose X)
      y (map second points)]
      (m/mmul (m/inverse (m/mmul XT X)) XT y)))

(defn draw [d]
  (let [polynome-degree d
        step-size 0.03
        iteration-limit 4000
        data (vec (gen-data 100 0.3))
        x-range (range 0 1 0.01)
        sin (map #(vector %1 (sin-scaled %1)) x-range)
        iterations (vec (take iteration-limit (find-polynomial data step-size polynome-degree)))
        errors (map :error iterations)
        polynome (:t (last iterations))
        polynome' (find-polynomial-matrix data polynome-degree)
        aprox (map #(vector %1 (m/dot polynome (powers %1 (count polynome)))) x-range)
        aprox' (map #(vector %1 (m/dot polynome' (powers %1 (count polynome')))) x-range)]
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
                     :style {:marker-type :none})} {:title (format "Aproximation via polynomial of degree %d" polynome-degree)})
            (c/xy-chart {
                "Error" {
                    :x (range (count errors)) 
                    :y errors 
                    :style {:marker-type :none}}} 
                {
                    :title (format "Error after n iterations, step size %f" step-size)
                    :x-axis {:title "Iterations"}
                    :y-axis {:title "Error"}}))))