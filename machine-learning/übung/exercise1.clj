#!/usr/bin/env inlein

'{:dependencies [[org.clojure/clojure "1.8.0"]
                 [com.hypirion/clj-xchart "0.2.0"]]}

(require '[com.hypirion.clj-xchart :as c])

(defn polynomial-at [koefs x]
		(reduce + (map-indexed #(* %2 (Math/pow x %1)) koefs))
)

(defn polynomial-derive [koefs] 
		(vec (rest (map-indexed #(* %1 %2) koefs))))

(let [eps #(- (rand 0.6) 0.3)
      sin #(Math/sin (* 2 Math/PI %1))
      sinNoised #(+ (sin %1) (eps))
      datax (take 100 (repeatedly rand))
      datay (map sinNoised datax)
      sinx (range 0 1 0.01)
      siny (map sin sinx)]
      (c/view (c/xy-chart {
							"Data" {:x datax :y datay :style {:render-style :scatter}}
							"Sine" {:x sinx :y siny :style {:marker-type :none}}
					 })))