# A new QOTW Website for Commonwealth

This project is composed of two parts. One is a REST API written in Clojure that handles storing quotes in a database as well as admin authentication and emailing, and the other is a HTML5/Javascript frontend that talks to the API through a webpage. The frontend will be served statically.

Owen Lynch will write the Clojure backend, Caleb Sander will write the javascript frontend.

# How to run

First, install leiningen 2, which handles dependencies for the code. You also have to have a postgresql server, and you must supply the connection info for that server in either environment variables or a profiles.clj file in the root directory of the code. The variables that must be defined are `database-host`, `database-port`, `database-db-name`, `database-username`, and `database-password`. An example profiles.clj file is below.

```clojure
{:dev {:env {:database-host "localhost"
             :database-port "5432"
             :database-db-name "commie-qotw-dev"
             :database-username "*******"
             :database-password "*******"}}}
```

If you wanted to supply these variables as environment variables, `:database-username` is equivalent to `DATABASE_USERNAME`, you can see `https://github.com/weavejester/environ` for more details. Once this has been set up, run `lein migrate` to set up the tables needed for operation of the app, and run `lein populate` to seed the database with some initial data.

Finally, run `lein ring server PORT_NUMBER` to start the server.
