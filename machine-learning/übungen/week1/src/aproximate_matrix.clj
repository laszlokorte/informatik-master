(ns aproximate
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str]))

(m/set-current-implementation :vectorz)


(defn find-polynomial-matrix [points degree]
  "find a polynomial of degree matching the given points using matrix multiplication"
  (let [X (map (fn [[x y]] (powers x degree)) points)
        XT (m/transpose X)
        y (map second points)]
    (m/mmul (m/inverse (m/mmul XT X)) XT y)))
