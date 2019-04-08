(ns yehaa.gamestats)


(def boundsYLow 0)
(def boundsYHigh 575)
(def boundsXLow 0)
(def boundsXHigh 580)
(def ballspeed 4)
(def freewind 2)
(def aipower true)
(def aideeplearning_right true)
(def aideeplearning_left false)

(def playerspeed 5)
(def playerSize [20 200])
(def ballsize [20 20])

(def gravity 1)
(def fall-speed 10)




(def mock-state
  '({:role "player" :type "entity" :color 255 :x 20 :y 100 :velX 6 :velY 6 :formed false :target 0 :input [1 0 1 0 1]}
    {:role "ball" :type "object" :color 255 :x 200 :y 200 :velX 5 :velY 5}
    {:role "enemy" :type "entity" :color 255 :x 760 :y 200 :velX 6 :velY 6 :formed false :target 0 :input [1 0 1 0 1]}
    {:role "score" :type "logic" :player1 0 :player2 0}))


(def theman-state
  {:player {:x 20 :y 20 :velX 0 :velY 0 :width 20 :height 20}})


(def map-1
  {:floor {:x 0 :y 580 :width 1200 :height 30}
   :goal {:x 740 :y 500 :width 40 :height 80}})

