(defproject commie-qotw "0.1.0-SNAPSHOT"
  :description "A Quote of the Week Website for Commschool"
  :url "http://qotw.net"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring/ring-defaults "0.1.4"]
                 [http-kit "2.1.18"]
                 [compojure "1.3.3"]
                 [honeysql "0.5.2"]
                 ])
