(ns commie-qotw.auth
  (:require []))

(defn wrap-authentication [request auth-fn]
  (assoc request :auth (auth-fn request)))

(def require-auth [handler & request-params]
  (if (:auth request)
    (apply handler request-params)
    {:valid false}))
