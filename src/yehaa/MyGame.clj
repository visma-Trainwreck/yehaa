(ns yehaa.MyGame
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [yehaa.gamestats :as gamestats]
            [yehaa.colliders :as colliders]
            [yehaa.neuralnetwork-mark2 :as nn]
            [yehaa.converters :as converters]
            [clojure.edn :as edn]))

(defonce gamestate (atom '()))
(defonce currentactions (atom {:leftplayer 0 :rightplayer 0}))

(def descission-formed-atom (atom false))
(def input-atom (atom []))
(def neuralnetwork (atom []))
(def target-atom (atom 0))
(def neuralnetwork-right (ref {}))

(def counter-atom (atom 0))



(defn updategamestate
  [statelist]
  #_(swap! gamestate (fn [_] statelist))
  statelist)




(defn gamelearn
  [ball enemy]
  (if (< (:x ball) 20)
    #_(swap! descission-formed-atom (fn [_] false))
    (conj enemy (:formed false))
    (let [target (:y ball)
          valtarget (converters/entity-y-pos-to-val target)
          newnetwork (nn/train-network @neuralnetwork (:input enemy)#_@input-atom [valtarget] 0.1)]
      (do
          (swap! neuralnetwork (fn [_] newnetwork))
          #_(dosync
              (alter neuralnetwork-right
                     (fn [_] newnetwork)))
          (conj enemy (:formed false))
          #_(swap! descission-formed-atom (fn [_] false))))))






(defn scoreadd
  [statelist]
  (map (fn [state] (cond
                     (= "score" (:role state)) (let [ball (first (filter (fn [state] (if (= "ball" (:role state)) state nil)) statelist))]
                                                 (if (< (:x ball) -20)
                                                   (conj state {:player2 (+ 1 (:player2 state))})
                                                   (conj state {:player1 (+ 1 (:player1 state))})))
                     (= "player" (:role state)) (first gamestats/mock-state)
                     (= "enemy" (:role state)) (do
                                                 (gamelearn (second statelist) (nth statelist 2))
                                                 (conj state (nth gamestats/mock-state 2)))
                     (= "ball" (:role state)) (let [ball (second gamestats/mock-state)
                                                    ranvelX (+ 3 (rand-int 8))
                                                    ranvelY (+ 1 (rand-int 4))
                                                    dir (if (= 1 (- 1 (rand-int 2)))
                                                          1
                                                          -1)]
                                                (conj ball {:velX (* dir ranvelX) :velY ranvelY}))
                     :else state)) statelist))






(defn reset?
  [ball]
  (let [x (:x ball)]
    (if (or (< x -20) (> x 800))
      true
      false)))

(defn writescore
  [state]
  (let [player1score (:player1 state)
        player2score (:player2 state)
        player1 (if gamestats/aipower
                  "Nadal"
                  "Player")]
    (q/text (str player1 "   " player1score "        |        " player2score "   Mr AI") 350 50)))



(defn writeballspeed
  [state]
  (let [velX (:velX state)
        velY (:velY state)
        rawspeed (Math/sqrt (+ (* velX velX) (* velY velY)))
        speed (int (Math/floor rawspeed))]
    (q/text (str "speed:  " speed) 400 90)))
;(q/text (str "SpeedY: " velY "   speedX: " velX) 400 110)



(defn drawplayer
  [state]
  (let [x (:x state)
        y (:y state)
        color (:color state)]
    (q/fill color 255 255)
    (q/rect x y (first gamestats/playerSize) (second gamestats/playerSize))))



(defn drawball
  [state]
  ;Draw baaaaaaalll
  (let [x (:x state)
        y (:y state)
        color (:color state)
        velX (Math/sqrt (Math/pow (:velX state) 2))
        velY (:velY state)]

    (dorun (cond
             (> 15 velX) (q/fill 0 255 255)
             (and (<= 15 velX) (> 10 velX)) (q/fill 0 125 255)
             (and (<= 10 velX) (> 4 velX)) (q/fill 0 0 255)
             :else (q/fill 0 0 0))
           (q/rect x y (first gamestats/ballsize) (second gamestats/ballsize)))))








(defn setup []
  ;tell quil what framerate / speed of the game and color mode, also gives the initialt state of the game.
  (q/frame-rate 80)
  (q/color-mode :hsb)
  gamestats/mock-state)


(defn drawIt
  [statelist]
  ;map through the list of states, and draw them. and returns the updated list. DOrun is there to make the lazy map do stuff
  (q/clear)
  (dorun (map (fn [state] (let [role (:role state)]
                            (cond (= "ball" role) (do (drawball state) (writeballspeed state))
                                  (= "player" role) (drawplayer state)
                                  (= "enemy" role) (drawplayer state)
                                  (= "score" role) (writescore state))))
              statelist)))



(defn player1
  [statelist]
  (q/background 240)
  (for [state statelist]
    (do (q/fill (:color state) 255 255)
        (q/with-translation
          [(/ (q/width) 2)
           (/ (q/height) 2)]
          (q/rect (:x state) (:y state) 20 20)))))

(defn newplayermower
  [state]

  (let [state (cond
                (= (:role state) "player")
                (conj state {:y (:leftplayer currentactions)})
                (= (:role state) "enemy")
                (conj state {:y (:rightplayer currentactions)}))]

    state))






(defn playermover
  [state]
  ;keylistener! If a key is being pressed AND its the w or s key, then we do stuff, else we return the old state...and not move.
  ;update! it also sets the new velocity in the state depentant on which key is pressed!
  (let [state1 (cond
                 (and (q/key-pressed?) (= (q/raw-key) \w)) (conj state {:y (- (:y state) gamestats/playerspeed) :velY gamestats/playerspeed})
                 (and (q/key-pressed?) (= (q/raw-key) \s)) (conj state {:y (+ (:y state) gamestats/playerspeed) :velY (* -1 gamestats/playerspeed)})
                 :else (conj state {:velY 0}))]
    (colliders/outofbounds state1)))


(defn form-descession
  [ball enemy]
  (if (and (< 0 (:velX ball)) (not (:formed enemy) #_@descission-formed-atom))
    (do #_(swap! descission-formed-atom (fn [_] true))
        (let [ballvector (converters/ball-vector-to-vals ball)
              enemyvector (converters/entity-y-pos-to-val (:y enemy))
              inputvector (conj ballvector enemyvector)
              targetraw (nn/nn-out inputvector @neuralnetwork)
              targetrefined (int (converters/entity-y-val-to-pos (first targetraw)))]
          (conj enemy {:input inputvector :formed true :target targetrefined})))



    #_(swap! target-atom (fn [_] targetrefined))
    #_(swap! input-atom (fn [_] inputvector))

    #_(swap! @descission-formed-atom (fn [_] false))
    (if (and (> 0 (:velX ball)) (:formed enemy)#_@descission-formed-atom)
      (do
        (println "yehaaa2")
        (conj enemy (:formed false)))
      enemy)))

(defn chase-descession
  [enemy]
  (let [y (:y enemy)
        new-enemy (if (> y (:target enemy)#_@target-atom)
                    (conj enemy {:y (- (:y enemy) gamestats/playerspeed)})
                    (conj enemy {:y (+ (:y enemy) gamestats/playerspeed)}))]
    new-enemy))







(defn enemyMover
  [ball enemy]
  ;The enemy moves compared to the ball. If the ball is moving away from it, it will seek to move to the middle
  ;if the ball is moving towards the enemy it will match its own coordiants with the balls.
  (let [ballY (:y ball)
        ballv (:velX ball)
        enemyY (:y enemy)
        role (:role enemy)
        state (cond
                (and (= "enemy" role) (neg? ballv)) (do #_(swap! descission-formed-atom (fn [_] false))
                                                        (cond
                                                          (:formed enemy) (conj enemy {:formed false})
                                                          (< enemyY 200) (conj enemy {:y (+ (:y enemy) gamestats/playerspeed)})
                                                          (> enemyY 200) (conj enemy {:y (- (:y enemy) gamestats/playerspeed)})
                                                          :else enemy))
                (and (= "player" role) (pos-int? ballv)) (cond
                                                           (:formed enemy) (conj enemy {:formed false})
                                                           (< enemyY 200) (conj enemy {:y (+ (:y enemy) gamestats/playerspeed)})
                                                           (> enemyY 200) (conj enemy {:y (- (:y enemy) gamestats/playerspeed)})
                                                           :else enemy)
                (and (= "enemy" role) gamestats/aideeplearning_right) (->> enemy
                                                                                 (form-descession ball)
                                                                                 (chase-descession))
                (and (= "player" role) gamestats/aideeplearning_left) (->> enemy
                                                                           (form-descession ball)
                                                                           (chase-descession))

                (< (- ballY 50) enemyY) (conj enemy {:y (- (:y enemy) gamestats/playerspeed)})
                (> (- ballY 50) enemyY) (conj enemy {:y (+ (:y enemy) gamestats/playerspeed)})
                :else enemy)]
    (colliders/outofbounds state)))



(defn ballmover
  [statelist]
  ;the ball always moves, check if its above or below the top or bottom, and if it does we invert its Y velocity
  ;If the ball is hitting a player then we invert its X velocity
  ;if nothing is hiting the ball and we are within the game window the ball just continues
  (let [ball
        (if (colliders/checkBoundsY (second statelist))
          (colliders/bounce-hori (second statelist))
          (colliders/bounce-powerup statelist))
        x (:x ball)
        y (:y ball)
        velX (:velX ball)
        velY (:velY ball)]
    (conj ball {:x (+ x velX) :y (+ y velY)})))





(defn update_main [statelist]
  ; check if the ball is out of bounds, and reset the state if it is!
  #_(println (:target (nth statelist 2)))
  (let [ball (first (filter (fn [state] (if (= "ball" (:role state)) state nil)) statelist))]
    (if (reset? ball)
      (scoreadd statelist)
      ;map though the statelist and update the gamestats for outside to see
      (updategamestate (map (fn [state] (let [role (:role state)]
                                          (cond
                                            (and (= role "player") (not gamestats/aipower)) (playermover state)
                                            (and (= role "player") gamestats/aipower) (enemyMover (second statelist) (first statelist))
                                            (= role "ball") (ballmover statelist)
                                            (= role "enemy") (enemyMover (second statelist) (nth statelist 2))
                                            :else state))) statelist)))))

(defn gamestarter
  []
  #_(swap! neuralnetwork (fn [_] nn/nn-test))
  (q/defsketch lala
               :title "tadaaa"
               :size [800 600]
               :setup setup
               :draw drawIt
               :update update_main
               :features [:keep-on-top]
               :middleware [m/fun-mode]))


(defn read-network-fromfile
  []
  (swap! neuralnetwork (fn [_](edn/read-string (slurp "resources/neuralnetwork.txt"))))
  (edn/read-string (spit "C:/Users/peter.l.rasmussen/Desktop/project-resoures/status.txt" "online")))

(defn write-network-tofile
  []
  (edn/read-string (spit "resources/neuralnetwork.txt" @neuralnetwork))
  (edn/read-string (spit "C:/Users/peter.l.rasmussen/Desktop/project-resoures/test.txt" @neuralnetwork))
  (edn/read-string (spit "C:/Users/peter.l.rasmussen/Desktop/project-resoures/status.txt" "offline")))

(defn gameloop
  []
  (loop [statelist gamestats/mock-state
         i 1]
    (if (not (= 0 (mod i 100000)))
      (do
        #_(println @target-atom)
        (recur (update_main statelist) (inc i))))))




(defn fullthrottle
  [n]
  #_(doall (map #(do (println "dereffing future") (deref %))
                (doall (repeatedly 5 #(do (println "created future") (atom "foo"))))))
  (let [x (future (gameloop))
        y (future (gameloop))
        z (future (gameloop))
        a (future (gameloop))
        b (future (gameloop))
        c (future (gameloop))]
    (println @x @y @z @a @b @c "DONE" n))

  #_(gamestarter))

(defn -main
  []
  (read-network-fromfile)
  (println "Start")
  (dotimes [n 100](fullthrottle n))
  (write-network-tofile)
  (println "done"))