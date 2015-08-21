(defproject commie-qotw "0.1.0-SNAPSHOT"
  :description "A Quote of the Week Website for Commschool"
  :url "http://qotw.net"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-json "0.4.0"]
                 [http-kit "2.1.18"]
                 [ring-server "0.4.0"]
                 [compojure "1.4.0"]
                 [honeysql "0.6.1"]
                 [clj-time "0.10.0"]
                 [environ "1.0.0"]
                 [com.mchange/c3p0 "0.9.5.1"]
                 [ragtime "0.5.1"]
                 [buddy "0.6.1"]
                 ]
  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]]
  :aliases {"migrate" ["run" "-m" "commie-qotw.db/migrate"]
            "rollback" ["run" "-m" "commie-qotw.db/rollback"]
            "populate" ["run" "-m" "commie-qotw.actions/populate"]}
  :ring {:handler commie-qotw.core/app})
