(ns ring.redis.session-test
  (:require [clojure.test :refer [deftest is]]
            [ring.redis.session :as rs]
            [ring.middleware.session.store :as ring-store]))

(def conn {:pool {}
           :spec {:host "127.0.0.1"
                  :port 6379}})

(deftest store-creation
  (let [store (rs/redis-store conn)]
    (is (true? (instance? ring.redis.session.RedisStore store)))))

(deftest store-retrieve-session-test
  (let [store (rs/redis-store conn)]
    (is (= "key" (ring-store/write-session store "key" {:foo :bar})))
    (is (= {:foo :bar} (ring-store/read-session store "key")))))

(deftest delete-session-test
  (let [store (rs/redis-store conn)]
    (ring-store/write-session store "key" {:foo :bar})
    (is (= {:foo :bar} (ring-store/read-session store "key")))
    (is (nil? (ring-store/delete-session store "key")))
    (is (nil? (ring-store/read-session store "key")))))

(deftest automatic-timeout-test
  (let [store (rs/redis-store conn {:expire-secs 1})]
    (ring-store/write-session store "key" {:foo :bar})
    (Thread/sleep 1100)
    (is (nil? (ring-store/read-session store "key")))))

(deftest automatic-renewal-test
  (let [store (rs/redis-store conn {:expire-secs 1 :reset-on-read true})]
    (ring-store/write-session store "key" {:foo :bar})
    (Thread/sleep 500)
    (ring-store/read-session store "key")
    (Thread/sleep 600)
    (is (= {:foo :bar} (ring-store/read-session store "key")))))

(deftest custom-read-handler-test
  (let [store (rs/redis-store conn {:read-handler (fn [_] {:foo :baz})})]
    (ring-store/write-session store "key" {:foo :bar})
    (is (= {:foo :baz} (ring-store/read-session store "key")))))

(deftest custom-write-handler-test
  (let [store (rs/redis-store conn {:read-handler (fn [_] "foo")})]
    (ring-store/write-session store "key" {:foo :bar})
    (is (= "foo" (ring-store/read-session store "key")))))
