(ns yehaa.converters)










(defn entity-y-pos-to-val
  [y]
  (/ y 600.0)
  )

(defn entity-y-val-to-pos
  [val]
  (* val 600))

(defn ball-vector-to-vals
  [ball]
  (let [x (:x ball)
        y (:y ball)
        velX (:velX ball)
        velY (:velY ball)
        valx (/ x 800.0)
        valy (/ y 600.0)
        valVelX (/ velX 30.0)
        valVelY (/ velY 30.0)]
    [valx valy valVelX valVelY]))

