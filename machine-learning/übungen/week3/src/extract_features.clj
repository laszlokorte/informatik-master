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
        (/ 255.0)))
(defn clamp [min max x]
    (cond (> x max) max (< x min) min :else x))

(defn above-avg [matrix]
    (let [avg (* 2 (/ (m/esum matrix) (m/ecount matrix)))]
    (m/emap #(if (> %1 avg) 1 0) matrix)))

(defn above [x matrix]
    (m/emap #(if (>= %1 x) 1 0) matrix))

(defn convolution [kernel matrix]
    (let [
        [w h] (m/shape matrix)
        pix (fn [[x y] val]
        (m/esum (m/emap-indexed
            (fn [[xx yy] k]
                (* k
                (m/mget matrix
                    (clamp 0 (- w 1) (+ x xx))
                    (clamp 0 (- h 1) (+ y yy)))))
            kernel)))]
        (m/emap-indexed pix matrix))
)

(defn edges [rgb-matrix]
    (let [kernel (m/matrix [
        [-1.0 -1.0 -1.0]
        [-1.0 +8.0 -1.0]
        [-1.0 -1.0 -1.0]])]
    (m/abs (convolution kernel rgb-matrix)))
)

(defn edge-score [matrix]
    (m/esum (edges matrix)))

(defn grey-scale [rgb-matrix]
    (let [reds (m/emap #(rgb-channel 16 %1) rgb-matrix)
          greens (m/emap #(rgb-channel 8 %1) rgb-matrix)
          blues (m/emap #(rgb-channel 0 %1) rgb-matrix)]
     (m/add (m/mul 0.2126 reds) (m/mul 0.7152 greens) (m/mul 0.0722 blues))))

(defn extract-features [rgb-matrix]
    (let [reds (m/emap #(rgb-channel 0 %1) rgb-matrix)
          greens (m/emap #(rgb-channel 8 %1) rgb-matrix)
          blues (m/emap #(rgb-channel 16 %1) rgb-matrix)
          bw (grey-scale rgb-matrix)
          size (reduce * (m/shape rgb-matrix))]
    {
    :edges-bw (* 1.0 (edge-score bw))}))

(defn -main [& dirs]
    (doseq [[idx dir] (map-indexed vector dirs)]
        (let [files (map (fn [f] {:file f :name (.getName f)}) (filter #(.isFile %1) (file-seq (File. dir))))
              images (filter (comp not nil? :image) (map #(assoc %1 :image (. ImageIO (read (:file %1)))) files))
              matrices (map #(assoc %1 :matrix (image2matrix (:image %1))) images)]
             (doseq [m matrices]
                (->> m
                    :matrix
                    extract-features
                    vals
                    (clojure.string/join " ")
                    (println (:name m) idx))))))
