(ns gen-data)


(defn noise [ampl]
  "generate random value in [-ampl, ampl]"
  (- (rand (* 2 ampl))
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

(defn -main [& args]
  (doseq [[x y] (gen-data 100 0.3)]
    (println x y)))
