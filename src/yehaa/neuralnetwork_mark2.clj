(ns yehaa.neuralnetwork-mark2
  (:require [clojure.core.matrix :as matrix]))


(def input-neurons [1 0 0 0 1])
(def input-neurons2 [0 1 1 1 0])
(def input-hidden-strengths [ [0.12 0.2 0.13 0.3 0.1 0.12 0.2 0.13 0.3 0.1 0.1 0.3 0.1 0.12 0.2 0.13]
                             [0.12 0.2 0.13 0.3 0.1 0.12 0.2 0.13 0.3 0.1 0.1 0.3 0.1 0.12 0.2 0.13]
                             [0.12 0.2 0.13 0.3 0.1 0.12 0.2 0.13 0.3 0.1 0.1 0.3 0.1 0.12 0.2 0.13]
                             [0.12 0.2 0.13 0.3 0.1 0.12 0.2 0.13 0.3 0.1 0.1 0.3 0.1 0.12 0.2 0.13]
                             [0.12 0.2 0.13 0.3 0.1 0.12 0.2 0.13 0.3 0.1 0.1 0.3 0.1 0.12 0.2 0.13]])

(def input-hidden-strengths-test (doall (map (fn [_] (map (fn [_] (rand)) (range 16))) (range 5))))

(def hidden-neurons [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0])
(def hidden-neurons2 [0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0])

(def hidden-layer1-strengths [[0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]
                              [0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.1 0.01 0.02 0.15 0.15 0.15]])

(def hidden-layer1-strengths-test (doall (map (fn [_] (map (fn [_] (rand)) (range 16))) (range 16))))


(def hidden-layer2-strengths [[0.1]
                              [0.1]
                              [0.1]
                              [0.3]
                              [0.1]
                              [0.1]
                              [0.1]
                              [0.1]
                              [0.2]
                              [0.15]
                              [0.13]
                              [0.12]
                              [0.2]
                              [0.3]
                              [0.15]
                              [0.2]])

(def hidden-layer2-strengths-test (doall (map (fn [_] (map (fn [_] (rand)) (range 1))) (range 16))))

(def targets [1])
(def targets2 [0])
(def new-output-neurons)

(def learning-rate 0.3)

(def nn [input-neurons input-hidden-strengths hidden-neurons
         hidden-layer1-strengths hidden-neurons2 hidden-layer2-strengths [0]])

(def nn-test [input-neurons input-hidden-strengths-test hidden-neurons
              hidden-layer1-strengths-test hidden-neurons2 hidden-layer2-strengths-test [0]])

(def activation-fn (fn [x] (Math/tanh x)))
(def dactivation-fn (fn [y] (matrix/sub 1.0 (matrix/mul y y))))




(defn update-strengths [deltas neurons strengths lrate]
  (matrix/add strengths (matrix/mul lrate
                                    (mapv #(matrix/mul deltas %) neurons))))

(defn hlayer-deltas
  [odeltas neurons strengths]
  (matrix/mul (mapv dactivation-fn neurons)
              (mapv #(reduce + %)
                    (matrix/mul odeltas strengths))))

(defn output-deltas
  [targets outputs]
  (matrix/mul (mapv dactivation-fn outputs)
              (matrix/sub targets outputs)))

(defn layer-activation [inputs strengths]
  (mapv activation-fn
        (mapv #(reduce + %)
              (matrix/mul inputs (matrix/transpose strengths)))))

(defn feed-forward [input network]
  (let [[in i-h-strengths h h-o-strengths h2 h2-o-strengths out] network
        new-h (layer-activation input i-h-strengths)
        new-h2 (layer-activation new-h h-o-strengths)
        new-o (layer-activation new-h2 h2-o-strengths)]
    [input i-h-strengths new-h h-o-strengths new-h2 h2-o-strengths new-o]))

(defn update-weights [network target learning-rate]
  (let [[in i-h-strengths h h-o-strengths h2 h2-o-strengths out] network
        o-deltas (output-deltas target out)
        h2-deltas (hlayer-deltas o-deltas h2 h2-o-strengths)
        h-deltas (hlayer-deltas h2-deltas h h-o-strengths)
        n-h-o-strengths (update-strengths
                          o-deltas
                          h2
                          h2-o-strengths
                          learning-rate)
        n-i-h2-strengths (update-strengths
                           h2-deltas
                           h
                           h-o-strengths
                           learning-rate)
        n-i-h-strengths (update-strengths
                          h-deltas
                          in
                          i-h-strengths
                          learning-rate)]
    [in n-i-h-strengths h2 n-i-h2-strengths h n-h-o-strengths out]))

(defn train-network [network input target learning-rate]
  (update-weights (feed-forward input network) target learning-rate))

(defn nn-out
  [input network]
  (last (feed-forward input network)))

(defn deep-learning
  [nn input-neurons targets learning-rate i]
  (loop [nn nn
         input-neurons input-neurons
         targets targets
         learning-rate learning-rate
         i i]
    (let [set (train-network nn input-neurons targets learning-rate)]
      (if (> i 0)(recur set input-neurons targets learning-rate (dec i)) set))))
