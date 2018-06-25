ring-redis-session
==================

[![Build Status][travis-badge]][travis]
[![Dependencies Status][deps-badge]][deps]
[![Clojars Project][clojars-badge]][clojars]
[![Tag][tag-badge]][tag]
[![JDK version][jdk-v]](.travis.yml)
[![Clojure version][clojure-v]](project.clj)

[![Project Logo][logo]][logo-large]

*A Redis backed Clojure/Ring session store*


What is it?
-----------

`ring-redis-session` uses redis as a Clojure/Ring's HTTP session
storage engine. What makes it different is its support for
hierarchical data, actually any `*print-str*`able clojure data types.


Why?
----

The reason `ring-redis-session` was written was that there was a need for
a Redis-backed session store that supported hierarchical data structures,
and the only Redis session store available ([rrss][rrss]) ... didn't.


Important Changes
-----------------

* **3.3.0-SNAPSHOT**
  - Data serialization now happens with default for
    `com.taoensso/carmine` `nippy` library because original
    serialization method was based on deprecated `eval` read macro `#=`;

  - `read-handler` and `:write-handler` options added to constructor
    which can be used to define custom data serialization format. See example in
    [Customize data serialization format](#customize-data-serialization-format) section.


* **v3.1.0** - This release has changed the repo name, project name, and release
  name from `clj-redis-session` to `ring-redis-session` (thanks @plexus for the
  great suggestion!)

* **v3.0.0** - This release introduces a breaking change in the API. Whereas
  prior to this release on used `ring-redis-session.core`, one now has to use
  `ring.redis.session`. If this is a problem for your project, simply ensure
  that you depend upon a version prior to the v3.0.0 release -- all of which
  uphold the old API.


Installation
------------

Add
```clojure
[clojusc/ring-redis-session "3.3.0-SNAPSHOT"]
```
to `:dependencies` in your `project.clj`.


Usage
-----

If you need it, documentation is [here][docs].

`ring-redis-session` is a drop-in replacement for Ring native session
stores. `ring-redis-session` uses [Carmine][carmine] as its Redis client.


First, require the session namespaces:

```clj
(ns your-app
  (:require [ring.middleware.session :as ring-session]
            [ring.redis.session :refer [redis-store]]))
```

Then define the Redis [connection options][redis conn opts] as you would when
using Carmine directly. For example:

```clj
(def conn {:pool {}
           :spec {:host "127.0.0.1"
                  :port 6379
                  :password "s3kr1t"
                  :timeout-ms 5000}})
```

At this point, you'll be ready to use `ring-redis-session` to manage your
application sessions:

```clj
(def your-app
  (-> your-routes
      (... other middlewares ...)
      (ring-session/wrap-session {:store (redis-store conn)})
      (...)))
```

If you are using `friend` for auth/authz, you will want to thread your security
wrappers first, and then the session. If you are using `ring-defaults` to wrap
for the site defaults, you'll want to thread the session wrapper before the
defaults are set.

Automatically expire sessions after 12 hours:

```clj
(wrap-session your-app {:store (redis-store conn {:expire-secs (* 3600 12)})})
```

Automatically extend the session expiration time whenever the session data is
read:

```clj
(wrap-session your-app {:store (redis-store conn {:expire-secs (* 3600 12)
                                                  :reset-on-read true})})
```

You can also change the default prefix, `session`, for the keys in Redis to
something else:

```clj
(wrap-session your-app {:store (redis-store conn {:prefix "your-app-prefix"})})
```

## Customize data serialization format

The format of how data will be kept in Redis storage could be defined
with `:read-handler`, `:write-handler` functions passed to
constructor.

This example shows how to set handlers to store data in `transit` format:

```clojure
  (defn to-str [obj]
    (let [string-writer  (ByteArrayOutputStream.)
          transit-writer (transit/writer string-writer :json)]
      (transit/write transit-writer obj)
      (.toString string-writer)))

  (defn from-str [str]
    (let [string-reader  (ByteArrayInputStream. (.getBytes str))
          transit-reader (transit/reader string-reader :json)]
      (transit/read transit-reader)))

  ...

  (session/wrap-session handler {:store (redis-store redis-conn
                                         {:read-handler #(some-> % from-str)
                                          :write-handler #(some-> % to-str)})})
```


License
-------

Copyright © 2013 Zhe Wu <wu@madk.org>

Copyright © 2016-2017 Clojure-Aided Enrichment Center

Distributed under the Eclipse Public License, the same as Clojure.


[travis]: https://travis-ci.org/clojusc/ring-redis-session
[travis-badge]: https://travis-ci.org/clojusc/ring-redis-session.png?branch=dev
[logo]: resources/images/redis-logo-small.png
[logo-large]: resources/images/redis-logo.png
[rrss]: https://github.com/paraseba/rrss
[carmine]: https://github.com/ptaoussanis/carmine
[redis conn opts]: https://github.com/ptaoussanis/carmine/blob/master/src/taoensso/carmine.clj#L26
[deps]: http://jarkeeper.com/clojusc/ring-redis-session
[deps-badge]: http://jarkeeper.com/clojusc/ring-redis-session/status.svg
[tag-badge]: https://img.shields.io/github/tag/clojusc/ring-redis-session.svg
[tag]: https://github.com/clojusc/ring-redis-session/tags
[clojure-v]: https://img.shields.io/badge/clojure-1.8.0-blue.svg
[jdk-v]: https://img.shields.io/badge/jdk-1.7+-blue.svg
[clojars]: https://clojars.org/clojusc/ring-redis-session
[clojars-badge]: https://img.shields.io/clojars/v/clojusc/ring-redis-session.svg
[docs]: https://clojusc.github.io/ring-redis-session/current/
