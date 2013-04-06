# flux

A Clojure based Solr client.

## Usage

###Http

```clojure
(require '[flux.http :as http])

(def conn (http/create "http://localhost:8983/solr" :collection1))
```

###Embedded

```clojure
(require '[flux.embedded :as embedded])

(def core (embedded/create-core "path/to/solr-home" "path/to/solr.xml"))
(def conn (embedded/create core :collection1))
```

###Client
Once a connection as been created, use the with-connection macro to wrap client calls:

```clojure
(require '[flux.core :as ej])

(ej/with-connection conn
    (ej/add [{:id 1} {:id 2}])
    (ej/commit)
	(ej/query "*:*"))
```

## License

Copyright © 2013 Matt Mitchell

Distributed under the Eclipse Public License, the same as Clojure.