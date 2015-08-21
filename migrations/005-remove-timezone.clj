{:up ["ALTER TABLE quotes DROP \"timestamp\", ADD submitted timestamp"]
 :down ["ALTER TABLE quotes DROP submitted, ADD \"timestamp\" timestamp with time zone"]}
