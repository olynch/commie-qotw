# A new QOTW Website for Commonwealth

This project is composed of two parts. One is a REST API written in Clojure that handles storing quotes in a database as well as admin authentication and emailing, and the other is a HTML5/Javascript frontend that talks to the API through a webpage. The frontend will be served statically.

Owen Lynch will write the Clojure backend, Caleb Sanders will write the javascript frontend.

# How to run

First, install leiningen 2. To pull in the dependencies run `lein deps` and then to start the server run `lein run -m commie-qotw.core`.
