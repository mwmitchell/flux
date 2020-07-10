# star

<<<<<<< Updated upstream
A Clojure library designed to ... well, that part is up to you.

## Usage

FIXME

## License

Copyright © 2013 FIXME
=======
A Clojure based Solr client. The latest stable version of flux is 0.6.3, which supports Solr `8.5.2`.

## Installation (Leiningen)

To include the Flux library, add the following to your `:dependencies`:

    [com.codesignals/flux "0.6.3"]

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

#### Query options
You can also specify additional query options (filter queries, fields, ...):
```clojure
   (let [options {:fq {:id 1 :range_field [1 5]} :sort "id asc" :fl ["id" "another_field"]}]
    (flux/query "*:*" options)
```

### Test
```shell
lein midje
```

## License

Copyright © 2013-2020 Matt Mitchell
>>>>>>> Stashed changes

Distributed under the Eclipse Public License, the same as Clojure.
