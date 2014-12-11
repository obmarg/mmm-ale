(defproject mmm-ale "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.0"]
                 [ring/ring-defaults "0.1.2"]
                 [com.taoensso/timbre "3.3.1"]
                 [ring-middleware-format "0.4.0"]]
  :jvm-opts ["-server"]
  :plugins [[lein-ring "0.8.13"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.5.5"]
            [lein-kibit "0.0.8"]]
  :ring {:handler mmm-ale.core.handler/app
         :port    1338}
  :profiles
  {:dev {:dependencies  [[javax.servlet/servlet-api "2.5"]
                         [ring-mock "0.1.5"]
                         [pjstadig/humane-test-output "0.6.0"]]
         :injections    [(require 'pjstadig.humane-test-output)
                         (pjstadig.humane-test-output/activate!)]
         :ring          {:auto-reload? true}
         :env           {}}})
