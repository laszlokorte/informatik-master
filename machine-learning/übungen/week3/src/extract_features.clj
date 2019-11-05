(ns extract-features
  (:require [clojure.core.matrix :as m]
            [clojure.string :as str])
  (:import (java.io File) (javax.imageio ImageIO)))

(m/set-current-implementation :vectorz)

(defn image2matrix [image]
    (let [
        width (.getWidth image)
        height (.getHeight image)
        grid (for [
            x (range width)
            y (range height)]
            (.getRGB image x y))]
    (m/reshape (vector grid) [width height])))

(defn rgb-channel [x rgb]
    (-> rgb
        (bit-and (bit-shift-left 0xff x))
        (bit-shift-right x)
        (double)
        (/ 255)))

(defn extract-features [rgb-matrix]
    (let [reds (m/emap #(rgb-channel 0 %1) rgb-matrix)
          greens (m/emap #(rgb-channel 8 %1) rgb-matrix)
          blues (m/emap #(rgb-channel 16 %1) rgb-matrix)
          size (reduce * (m/shape rgb-matrix))]
    {:min-red (m/emin reds)
    :min-green (m/emin greens)
    :min-blue (m/emin blues)
    :max-red (m/emax reds)
    :max-green (m/emax greens)
    :max-blue (m/emax blues)
    :avg-red (/ (m/esum reds) size)
    :avg-green (/ (m/esum greens) size)
    :avg-blue (/ (m/esum blues) size)}))

(defn -main [& dirs]
    (doseq [[idx dir] (map-indexed vector dirs)]
        (let [files (filter #(.isFile %1) (file-seq (File. dir)))
              images (filter (comp not nil?) (map #(. ImageIO (read %1)) files))
              matrices (map image2matrix images)]
             (doseq [m matrices]
                (->> m
                    extract-features
                    vals
                    (clojure.string/join " ")
                    (println idx))))))
