(ns commie-qotw.core
  (:require
    [commie-qotw.auth :refer :all]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.defaults :refer :all]
    [ring.middleware.json :refer [wrap-json-response wrap-json-params]]))

; Request Map -> Response Map (will intelligently convert various things to response map)
(defroutes qotw
  (context "/api"
   (POST "/submit" [quote]
         (submit-quote quote))
   (GET "/archives" {params :params}
        (get-archives params))
   (GET "/message" [messageID]
        (get-message messageID))
   (GET "/lastmessage" []
        (get-lastmessage))
   (POST "/vote" [vote]
         (submit-vote vote)))
  (context "/admin"
           (POST "/login" [username password]
                 (login username password))
           (POST "/submissions" []
                 (require-auth get-submissions))
           (POST "/votes" []
                 (require-auth get-votes))
           (POST "/compose" [message]
                 (require-auth compose message))
           (POST "/admins" []
                 (require-auth get-admins))
           (POST "/addadmin" [username password]
                 (require-auth add-admin username password))
           (POST "/rmadmin" [username]
                 (require-auth rm-admin username)))
  (route/files "/" {:root "ui"}))

; Request Map -> Response Map
(def app
  (-> qotw
      (wrap-authentication)
      (wrap-defaults api-defaults)
      (wrap-json-response)
      (wrap-json-params)
      ))
