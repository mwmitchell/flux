# flux

A Clojure based Solr client. The latest stable version of flux is 0.7.0, which supports Solr `8.5.2` and java 14.
For Java 12 support change the `slf4j` in `project.clj`.

## Installation (Leiningen)

To include the Flux library, add the following to your `:dependencies`:

    [com.codesignals/flux "0.7.0"]

## Usage

### Http

```clojure
(require '[flux.connection.http :as http])

(def conn (http/create "http://localhost:8983/solr" :name-of-collection))
```

### Cloud
Create a connection to SolrCloud using one zk host:

```clojure
(require '[flux.connection.cloud :as cloud]
         '[flux.collections :as cloud-api)

(def conn (cloud/create ["zk1:2181"]))
```

Create a connection to SolrCloud using multiple zk hosts (for failover):

```clojure
(def conn (cloud/create ["zk1:2181" "zk2:2181" "zk3:2181"]))
```

Create a connection to SolrCloud using multiple zk hosts (for failover) and a chroot:

```clojure
(def conn (cloud/create ["zk1:2181" "zk2:2181" "zk3:2181"] "mysolrchroot"))
```

Create a connection to SolrCloud using zk and specify a default collection

```clojure
(def conn (cloud/create ["zk1:2181" "zk2:2181" "zk3:2181"] "mysolrchroot" :name-of-collection))
```

Create a new SolrCloud collection with 4 shards:

```clojure
(cloud-api/create-collection conn "my-solr-collection" 4)
```

Create a new SolrCloud collection with 4 shards and replication factor of 2:

```clojure
(cloud-api/create-collection conn "my-solr-collection" 4 2)
```

Create a new SolrCloud collection with 4 shards and replication factor of 2 and additional parameters:

```clojure
(cloud-api/create-collection conn "my-solr-collection" 4 2 { "collection.configName" "schemaless"
                                                  "router.name" "implicit"
                                                  "shards" "x,y,z,p"
                                                  "maxShardsPerNode" 10})
```

The SolrCloud connection is thread-safe and it is recommended that you create just one
and re-use it across all requests. 

Remember to shutdown the connection on exit:

```clojure
(flux/with-connection conn (flux/shutdown))
```

Delete a SolrCloud collection:

```clojure
(cloud-api/delete-collection conn :name-of-collection)
```

Get a list of active replicas of a collection:

```clojure
(filter active? (all-replicas conn :name-of-collection))
```

Get a list of not-active replicas (either down or recovering) of a collection:

```clojure
(filter not-active? (all-replicas conn :name-of-collection))
```

Get a list of down replicas of a collection:

```clojure
(filter down? (all-replicas conn :name-of-collection))
```

Get a list of recovering replicas of a collection:

```clojure
(filter recovering? (all-replicas conn :name-of-collection))
```

Get a list of replicas of a collection hosted by host/port:

```clojure
(filter (fn [x] (hosted-by? x "127.0.1.1:8983")) (all-replicas conn :name-of-collection))
```

Get a list of leaders of a particular collection:

```clojure
(filter leader? (all-replicas conn :name-of-collection))
```

Wait for all replicas of a given collection hosted by a particular host/port to be in 'active' state:

```clojure
(wait-until-active conn :name-of-collection "host1:8983")
```

### Embedded

Currently not usable - preferred method is http interface in this case. See [https://cwiki.apache.org/confluence/display/solr/EmbeddedSolr](https://cwiki.apache.org/confluence/display/solr/EmbeddedSolr) too.

```clojure
(require '[flux.connection.embedded :as embedded])

(def cc (embedded/create-core-container))
```

#### Core auto-discovery
Flux also supports `core.properties`. Just give `create-core` the solr-home path as the only argument.

  Note: It's important to call the `load` method on the resulting `CoreContainer` instance:

```clojure
(def cc (doto (embedded/create-core-container "path/to/solr-home")
              (.load))
```

Now create the embedded server instance:

```clojure
(def conn (embedded/create cc :name-of-collection))
```

### Client
Once a connection as been created, use the `with-connection` macro to wrap client calls:

```clojure
(require '[flux.core :as flux]
         '[flux.query :as q])

(flux/with-connection conn
    (flux/add [{:id 1} {:id 2}])
    (flux/commit)
    (flux/query "*:*")
    ;; Or set the path and/or method:
    (flux/request
      (q/create-query-request :post "/docs" {:q "etc"}))
```

new with version `0.7.0` you can now use the `set-default-connection`. All client calls will use this connection if no other connection is given by `with-connection`.

```clojure
(require '[flux.core :as flux]
         '[flux.query :as q])

(flux/set-default-connection conn)
(flux/add [{:id 1} {:id 2}])
(flux/commit)
(flux/query "*:*")
```

#### Query options
You can also specify additional query options (filter queries, fields, ...):
```clojure
   (let [options {:fq {:id 1 :range_field [1 5]} :sort "id asc" :fl ["id" "another_field"]}]
    (flux/query "*:*" options)
```

#### Special options

In past all values returned by Solr were strings, flux now tries to convert them back into Doubles or Longs too.
If you prefer the old way the `->clojure` function worked or need a possible better performance you can change back to the old way by:

```clojure
(require '[flux.converter :refer [set-stringoutput-only]])

;; Only strings in return
(set-stringoutput-only true)
;; Allow to try to parse
(set-stringoutput-only false)
```

### Test

Prepare a local zookeeper and solr with collection flux_test then run the tests.

Usually it's downloading both programms, configure zookeeper and start them, 
then create the collection or use parametered start to do so.

Help config zk: [zookeeper minimum config](https://lucene.apache.org/solr/guide/8_5/setting-up-an-external-zookeeper-ensemble.html#initial-configuration)

Help start zk: [zookeeper startup](https://lucene.apache.org/solr/guide/8_5/setting-up-an-external-zookeeper-ensemble.html#starting-and-stopping-zookeeper)

Solr cloud start: [solr sstartup](https://lucene.apache.org/solr/guide/8_5/solr-tutorial.html#launch-solr-in-solrcloud-mode)

remember one node is enough in dev-test!

After you are done verify with the following command that nothing is broken. Do not forget to write docstrings and tests! 

```shell
lein midje
```

## License

Copyright © 2013-2020 Matt Mitchell

Distributed under the Eclipse Public License, the same as Clojure.
