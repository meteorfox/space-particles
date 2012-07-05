(ns space-particles.core
  (:use quil.core)
  (:gen-class))

;; Translated from C++ to Clojure by Carlos Torres

;; Space particle translation to Clojure
;; inspired by openFrameworks Particle Emitter
;; http://www.quietless.com/kitchen/getting-started-with-openframeworks/


(def number-particles 5000)

(defn mk-particle []
  {:x (random -1.0 1.0)
   :y (random -1.0 1.0)
   :radius (random 4)
   :speed-x (random 4)
   :speed-y (random 4)
   :fill-color (color (random 255)  (random 255) (random 255))
   :death (+ (random 50) 250)
   :age 0
   })

(defn add-particles! [*particles*]
  (dotimes [_ number-particles]
    (let [p (mk-particle)]
      (swap! *particles* conj p))))

(defn setup []
  (smooth)
  (background 0)
  (frame-rate 100)

  (let [*particles* (atom [])]
    (add-particles! *particles*)
    (set-state! :particles *particles*)))

(defn update-particle [{:keys [x y speed-x speed-y radius age death] :as particle}]
  (let [new-x (if (> x 0) (+ x speed-x) (- x speed-x))
        new-y (if (> y 0) (+ y speed-y) (- y speed-y))
        dead  (> age death)
        new-x (if dead (random -1.0 1.0) new-x)
        new-y (if dead (random -1.0 1.0) new-y)
        age   (if dead 0 (inc age))]
    (assoc particle :x new-x :y new-y :age age)))

(defn update-particles [particles]
  (map update-particle particles))

(defn draw-particle [{:keys [x y radius fill-color]}]
  (no-stroke)
  (fill-int fill-color)
  (ellipse x y (* 2 radius) (* 2 radius)))

(def ^:dynamic *angle* (atom 0.005))

(defn draw []
  (translate (/ (width) 2) (/ (height) 2))
  (rotate (swap! *angle* + 0.005))
  (background 0)
  (let [*particles* (state :particles)
        particles (swap! *particles* update-particles)]
    (doseq [p particles]
      (draw-particle p))))

(defn particle-emitter []
  "Starts the particle emitter"
  (defsketch space-particles
    :title "Space Particles"
    :setup setup
    :draw draw
    :size [1280 720]
    :renderer :p2d))

(defn -main [& args]
  (particle-emitter))
