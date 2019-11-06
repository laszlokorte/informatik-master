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

(defn convolution [kernel matrix]
    (let [
        [w h] (m/shape matrix)
        pix (fn [[x y] val]
        (m/esum (m/emap-indexed
            (fn [[xx yy] k]
                (* k
                (m/mget matrix
                    (mod (+ x xx w) w)
                    (mod (+ y yy h) h))))
            kernel)))]
        (m/emap-indexed pix matrix))
)

(defn edges [rgb-matrix]
    (let [kernel (m/matrix [
        [-1.0 -1.0 -1.0]
        [-1.0 +8.0 -1.0]
        [-1.0 -1.0 -1.0]])]
    (convolution kernel rgb-matrix))
)

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
    {:min-red (m/emin reds)
    :min-green (m/emin greens)
    :min-blue (m/emin blues)
    :max-red (m/emax reds)
    :avg-red (/ (m/esum reds) size)
    :edges (/ (m/esum (m/clamp (edges bw) 0 1)) size)}))

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
