(ns tex-model
  (:require [clojure.string :as str]))

(import java.util.Locale)

(defn format-number [fmt n]
  (let [locale (Locale. "en-us")]
    (String/format locale fmt (into-array Object [n]))))

(defn -main [file & args]
  (let [model (read-string (slurp file))
        feature-count (:feature-count model)
        sigma (:sigma model)
        phi (:phi model)
        mu-0 (:mu-0 model)
        mu-1 (:mu-1 model)
        number-format "%.10f"]
       (println "\\begin{align*}"
    "\\phi &="
    (format-number number-format phi)
    "&\n"
    "\\mu_0 &="
    "\\begin{pmatrix}"
    (str/join "\\\\\n" (map #(format-number number-format %1) mu-0))
    "\\end{pmatrix}&\n"
    "\\mu_1 &= \\begin{pmatrix}"
    (str/join "\\\\\n" (map #(format-number number-format %1) mu-1))
    "\\end{pmatrix}\n"
    "\\end{align*}\n"
    "\\vspace{-\\baselineskip}\n"
    "\\begin{align*}\n"
    "\\sigma &="
"\n\\begin{pmatrix}"
(str/join "\\\\\n" (map (fn [row] (str/join "&" (map (fn [c] (format-number number-format c)) row))) sigma))
"\n\\end{pmatrix}"
"\n\\end{align*}")))
