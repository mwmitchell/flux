# electrojet

A Clojure based Solr client.

## Usage

###Http

```clojure
(require '[electrojet.core :as ej])
(require '[electrojet.http :as http])

(def conn (http/create "http://localhost:8983/solr" :collection1))
(ej/with-connection conn
	(ej/query "*:*"))
```

FIXME

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
