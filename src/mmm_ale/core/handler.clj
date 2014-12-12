(ns mmm-ale.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [response content-type]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.format-params :refer [wrap-restful-params]]
            [ring.middleware.format-response :refer [wrap-restful-response]]
            [taoensso.timbre :as timbre]
            ))

(defonce store (atom []))

; Example keys & types of some incoming mail:
;(:stripped-html str :body-html str :X-Mailgun-Variables any :From str :References str :message-headers any :stripped-signature str :attachment-count int :signature str :recipient str :stripped-text str :Subject str :Mime-Version str :token str :In-Reply-To str :from str :Received [str] :Date str :sender str :Message-Id str :Sender str :To str :timestamp str :Content-Type str :subject str :User-Agent str :content-id-map str :body-plain str)

(def email-regex #"(.*) <(.*)>")

(defn- parse-address
  [data]
  (let [[_ name email] (re-matches email-regex data)]
    {:name  name
     :email email}))

(defn- parse-email
  [email-params]
  {:from      (parse-address (:From email-params))
   :to        (parse-address (:To email-params))
   :subject   (:Subject email-params)
   :body-html (:body-html email-params)})

(defn handle-mail [request]
  (swap! store conj (parse-email (:params request)))
  "BOOM")

(defn get-mail [address]
  (let [groups (group-by (comp :email :to) @store)
        emails (groups address)]
    (timbre/debug "Getting emails for" address)
    (timbre/debug "Found" (count emails) "emails")
    (timbre/debug emails)
    (-> emails
        response
        (content-type "application/json"))))

(defroutes app-routes
  (GET  "/"              []        "Hello World!")
  (POST "/mail"          request   (handle-mail request))
  (GET  ["/mail/:address" :address #".*"] [address] (get-mail address))

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
