(ns mmm-ale.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.format-params :refer [wrap-restful-params]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [taoensso.timbre :as timbre]
            ))

(defroutes app-routes
  (GET "/" [] "Hello World!")
  (POST "/test" [] "BOOM")
  (route/not-found "Not Found"))


(defn- my-wrap-restful-params [handler]
  (wrap-restful-params
    handler
    :formats [:json-kw :edn :yaml :transit-mspack :transit-json]))


(defn- log-request [handler]
  (fn [req]
    (timbre/debug req)
    (timbre/debug (ring.util.request/content-type req))
    (handler req)))

(defn default-params []
  (merge api-defaults
         {:params {:multipart   true
                   :keywordize  true}}))

(def app
  (-> app-routes
    log-request
    wrap-restful-response
    my-wrap-restful-params
    (wrap-defaults (default-params))
    ))
