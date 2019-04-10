(ns yehaa.theman-game
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [yehaa.gamestats :as gamestats]
            [yehaa.colliders :as colliders]
            [yehaa.neuralnetwork-mark2 :as nn]
            [yehaa.converters :as converters]
            [clojure.edn :as edn]))




(defn gravity
  [game-state]
  (let [player1 (:player game-state)
        player2 (conj player1 {:velY (+ (:velY player1) gamestats/gravity)})]
    (conj game-state {:player player2})))

(defn setup []
  ;tell quil what framerate / speed of the game and color mode, also gives the initialt state of the game.
  (q/frame-rate 80)
  (q/color-mode :hsb)
  gamestats/theman-state)

(defn- draw-object
  [{:keys [x y width height]}]
  (q/fill 255 255 255)
  (q/rect x y width height))

(defn draw
  [game-state]
  (q/clear)
  (draw-object (:player game-state))
  (draw-object (:floor gamestats/map-1))
  (draw-object (:goal gamestats/map-1)))

(defn move [game-state]
  (let [state1 (:player game-state)
        state2 (conj state1 {:y (+ (:velY state1) (:y state1))})
        state3 (if (> (:velY state2) gamestats/fall-speed)
                 (assoc state2 :velY gamestats/fall-speed)
                 state2)]

    (conj game-state {:player state3})))

#_(defn updater [game-state]
    (if (not (colliders/collidecheck-general (:player game-state) (:floor gamestats/map-1)))
      (-> game-state
          (gravity)
          (move))
      (if (colliders/collidecheck-general (:player game-state) (:floor gamestats/map-1))
        (-> game-state
            (assoc-in  [:player :velY] 0)
            (assoc-in [:player :y] (:y (- @colliders/platform (:height (:player game-state))))))
        (assoc-in  game-state[:player :velY] 0))))

(defn updater [game-state]
  (let [colides (not (colliders/collidecheck-general
                       (:player game-state)
                       (:floor gamestats/map-1)))]
    (cond-> game-state
            colides       (gravity)
            (not colides) (assoc-in  [:player :velY] 0)
            false       (assoc-in [:player :y]
                                  (- (:y @colliders/platform)
                                     1))
            true          (move))))






(defn gamestarter
  []
  (q/defsketch lala
               :title "tadaaa"
               :size [800 600]
               :setup setup
               :draw draw
               :update updater
               :features [:keep-on-top]
               :middleware [m/fun-mode]))

(defn testtest
  []
  #'gamestarter)

