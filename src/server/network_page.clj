(ns server.network-page)





(def style (str "<style>\ntable, th, td {\n  border: 1px solid black;\n}\n</style>"))
(def type {"Content-Type" "text/html"})
(def header type)

(defn row
  [nn layer neuron]
  (str  "<tr>" "<td>" (nth (nth nn layer) neuron) "</td>" "</tr>"))


(defn first-layer-table
  [nn]
  (str "<table "
       ;;"<tr>" "<th>" "input-neurons" "</th>" "<th>" "first-layer-neurons" "</th>" "<th>" "second-layer-neurons" "</th>" "<th>" "output-neurons" "</th>" "</tr>"
       (row nn 0 0) (row nn 0 1) (row nn 0 2) (row nn 0 3) (row nn 0 4)
        "</table>"))

(defn second-layer-table
  [nn]
  (str "<table "
       (row nn 3 0) (row nn 3 1) (row nn 3 2) (row nn 3 3) (row nn 3 4) (row nn 3 5) (row nn 3 6) (row nn 3 7) (row nn 3 8) (row nn 3 9) (row nn 3 10) (row nn 3 11) (row nn 3 12)
       (row nn 3 13) (row nn 3 14) (row nn 3 15)
       "</table>"))



(defn create-page
  [nn]
  {:status 200
   :headers header
   :body (str (second-layer-table nn))})

