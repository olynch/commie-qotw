(ns commie-qotw.core
  [:require 
   [compojure.core :refer :all]
   [compojure.route :as route]
   [org.httpkit.server :refer [run-server]]
   [ring.middleware.defaults :refer :all]])

(defroutes qotw
  (route/files "/" {:root "ui"}))

(defn -main []
  (run-server (wrap-defaults qotw site-defaults) {:port 5000}))
