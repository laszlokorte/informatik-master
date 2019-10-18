(ns gradient-descent 
  (:require [com.hypirion.clj-xchart :as c]
            [clojure.core.matrix :as m]
            [clojure.string :as str]))

; vectoru is needed for matrix inverse
(m/set-current-implementation :vectorz) 

(defn noise [ampl]
  "generate random value in [-ampl, ampl]"
  (- (rand
      (* 2 ampl))
     ampl))

(defn sin-scaled [x]
  "sin(2pi*x)"
  (-> x
      (* 2 Math/PI)
      (Math/sin)))

(defn sin-noised [x n]
  "sin(2pi*x) + eps where eps in [-n, n]"
  (+ (sin-scaled x)
     (noise n)))

(defn gen-data [n noise-factor]
  "get n values in the neighborhood of sin(2pi*x)"
  (->> (repeatedly rand)
       (map (fn [r] [r (sin-noised r noise-factor)]))
       (take n)
       (vec)))

(defn series-from-vectors [v]
  "convert list of pairs into map of two lists"
  (c/extract-series {:x first :y second} v))

(defn powers [x degree]
  "list of all integer powers of x up to degree"
  (->> (range)
       (map #(Math/pow x %1))
       (take (inc degree))
       (vec)))

(defn error [data theta]
  "error of the polynomial theta in respect to the data points"
  (->> data
       (map #(Math/pow (- (m/dot theta (:x %1))
                          (:y %1))
                       2))
       (reduce +)))

(defn build-polynomial [pairs degree]
  "convert [x y] pairs into {:x :y} map where"
  ":x becomes a vector of powers of x"
  (->> pairs
       (map
        (fn [[x y]] {:x (powers x degree)
                     :y y}))
       (vec)))

(defn gradient-descent [data alpha theta]
  "gradient descent algorithm"
  "alpha is the the learning rate"
  "theta is the initial vector of coefficients"
  (->> data
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
  (let [data (build-polynomial points degree)]
    (->> #(noise 0.5)
         (repeatedly)
         (take (inc degree))
         (vec)
         (iterate #(gradient-descent data learning-rate %1))
         (map (fn [coefficients] {:coefficients coefficients
                                  :error (error data coefficients)})))))
    
(defn find-polynomial-matrix [points degree]
  "find a polynomial of degree matching the given points using matrix multiplication"
  (let [X (map (fn [[x y]] (powers x degree)) points)
        XT (m/transpose X)
        y (map second points)]
    (m/mmul (m/inverse (m/mmul XT X)) XT y)))

(defn polynomial-string [coefficients]
  "converts list of coefficients into a string representing the polynomial"
  (->> coefficients
       (map-indexed #(format "%s*x^%d" (str %2) %1))
       (str/join " + ")))

(def simple-test-data
  (build-polynomial [[0 0] [1 1] [2 2] [3 3]] 3))

(defn run [d s]
  (let [polynomial-degree d
        learning-rate s
        iteration-limit 4000
        data (gen-data 100 0.3)
        x-range (range 0 1 0.01)
        sin (map #(vector %1 (sin-scaled %1)) x-range)
        iterations (vec (take iteration-limit (find-polynomial data polynomial-degree learning-rate)))
        errors (map :error iterations)
        polynomial (:coefficients (last iterations))
        polynomial' (find-polynomial-matrix data polynomial-degree)
        aprox (map #(vector %1 (m/dot polynomial (powers %1 polynomial-degree))) x-range)
        aprox' (map #(vector %1 (m/dot polynomial' (powers %1 polynomial-degree))) x-range)]
    (println polynomial-degree "," (count polynomial) "," (count polynomial') "," (count (powers 1 polynomial-degree)))
    (println "itr = " (polynomial-string polynomial))
    (println "mtx = " (polynomial-string polynomial'))
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
                    :title (format "Error after n iterations, step size %f" learning-rate)
                    :x-axis {:title "Iterations"}
                    :y-axis {:title "Error"}}))))

(defn -main [& args]
  (run 5 0.1))