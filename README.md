# ring-redis-session

<!-- badges -->
[![cljdoc badge](https://cljdoc.org/badge/com.lambdaisland/ring-redis-session)](https://cljdoc.org/d/com.lambdaisland/ring-redis-session) [![Clojars Project](https://img.shields.io/clojars/v/com.lambdaisland/ring-redis-session.svg)](https://clojars.org/com.lambdaisland/ring-redis-session)
<!-- /badges -->

[![Project Logo](resources/images/redis-logo-small.png)](resources/images/redis-logo.png)

*A Redis backed Clojure/Ring session store*

Originally created by clj-redis-session by sritchie. Seems multiple forks have been created in the past either due to lack of maintenance, or lack of a public (clojars) release. This version (`com.lambdaisland/ring-redis-session`) is similar. It is a fork of [Xylon2/ring-redis-session](https://github.com/Xylon2/ring-redis-session), which forked off [Ninerian/ring-redis-session](https://github.com/Ninerian/ring-redis-session), which forked off [clojusc/ring-redis-session](https://github.com/clojusc/ring-redis-session), which forked off [the original](https://github.com/sritchie/clj-redis-session).

We've adapted it to the lambdaisland release process, which makes it easy for us to put out new releases to clojars, so feel free to submit PRs. If you do please add an entry to `CHANGELOG.md` under the "Unreleased" heading.

## What is it?

`ring-redis-session` uses redis as a Clojure/Ring's HTTP session
storage engine. What makes it different is its support for
hierarchical data, actually any `*print-str*`able clojure data types.

<!-- installation -->
## Installation

To use the latest release, add the following to your `deps.edn` ([Clojure CLI](https://clojure.org/guides/deps_and_cli))

```
com.lambdaisland/ring-redis-session {:mvn/version "3.4.126"}
```

or add the following to your `project.clj` ([Leiningen](https://leiningen.org/))

```
[com.lambdaisland/ring-redis-session "3.4.126"]
```
<!-- /installation -->

## Usage

`ring-redis-session` is a drop-in replacement for Ring native session
stores. `ring-redis-session` uses [Carmine](https://github.com/ptaoussanis/carmine) as its Redis client.


First, require the session namespaces:

```clj
(ns your-app
  (:require [ring.middleware.session :as ring-session]
            [ring.redis.session :refer [redis-store]]))
```

Then define the Redis [connection options](https://github.com/ptaoussanis/carmine/blob/master/src/taoensso/carmine.clj#L26) as you would when
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

### Customize data serialization format

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

## License

Copyright © 2013 Zhe Wu <wu@madk.org>

Copyright © 2016-2018 Clojure-Aided Enrichment Center

Distributed under the Eclipse Public License, the same as Clojure.

