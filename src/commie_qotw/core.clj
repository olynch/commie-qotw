(ns commie-qotw.core
  (:require
    [commie-qotw.actions :refer :all]
    [commie-qotw.auth :as auth]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer :all]
    [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
    [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]))

; Request Map -> Response Map (will intelligently convert various things to response map)
(defroutes qotw
  (context "/api" []
           (POST "/submit" [quote]
                 (submit-quote quote))
           (GET "/archives" [& params]
                (get-archives params))
           (GET "/message" [messageID]
                (get-message messageID))
           (GET "/lastmessage" []
                (get-lastmessage))
           (POST "/send-message" [title body]
                 (send-message title body))
           (POST "/whoami" request
                 (whoami request))
           #_(POST "/vote" [vote]
                   (submit-vote vote)))
  (context "/admin" []
           (POST "/login" [email password]
                 (login email password))
           (POST "/submissions" []
                 (get-submissions))
           ;(POST "/votes" []
           ;(get-votes))
           (POST "/admins" []
                 (get-admins))
           (POST "/addadmin" [email password]
                 (sign-up email password))
           (POST "/rmadmin" [email]
                 (rm-user email)))
  (route/files "/" {:root "ui"}))


; Request Map -> Response Map
(def app
  (-> qotw
      (wrap-authorization auth/auth-backend)
      (wrap-authentication auth/auth-backend)
      (wrap-defaults api-defaults)
      (wrap-json-response)
      (wrap-json-params)
      ))
