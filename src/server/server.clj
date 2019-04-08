(ns server.server
  (:require [ring.adapter.jetty :as srv]
           [ring.middleware.params :refer [wrap-params]]
           [ring.middleware.keyword-params :refer [wrap-keyword-params]]
           [compojure.core :refer :all]
           [compojure.route :as route]
           [clojure.edn :as edn]
           [selmer.parser :as selmer]
           [server.network-page :as build-http]))


(defn readnnfromfile
  []
  (edn/read-string (slurp "C:/Users/peter.l.rasmussen/Desktop/project-resoures/test.txt")))
(defn readstatus-fromfile
  []
  (edn/read-string (slurp "C:/Users/peter.l.rasmussen/Desktop/project-resoures/status.txt")))


(defn input-neurons
  []
  (map (fn [count] {count (nth (first (readnnfromfile)) count)}) (range (count (first (readnnfromfile))))))

(defn homeroute
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "o/"})

(defn mynetwork
  [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str (readnnfromfile))})


(defn pagetest
  [request]
  (build-http/create-page (readnnfromfile)))


(defn status
  [request]
  {:status 200
   :headers {"Context-Type" "text/html"}
   :body (str (readstatus-fromfile))})

(defn network
  [request]
  (selmer/render-file "network.html" {:input (input-neurons)
                                      :fst_layer (second (readnnfromfile))
                                      :snd_layer (nth (readnnfromfile) 2)
                                      :output_layer (last (readnnfromfile))}))

(defroutes myroutes
           (GET "/home" request (homeroute request))
           (GET "/mycode" request (mynetwork request))
           (GET "/status" request (status request))
           (GET "/network" request (network request))
           (GET "/test" request (pagetest request)))


(defn myhandler
  [x]
  (((comp wrap-params wrap-keyword-params) myroutes) x))



(defn runserver
  []
  (future (srv/run-jetty #'myhandler {:port 8083})))




