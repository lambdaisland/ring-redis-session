# 3.4.126 (2024-06-25 / a4d04c6)

- fork from Xylon2/ring-redis-session, first release under `com.lambdaisland`

# 3.3.0

- Data serialization now happens with default for `com.taoensso/carmine` `nippy`
  library because original serialization method was based on deprecated `eval`
  read macro `#=`;

- `read-handler` and `:write-handler` options added to constructor which can be
  used to define custom data serialization format. See example in [Customize
  data serialization format](#customize-data-serialization-format) section.

# 3.1.0

- This release has changed the repo name, project name, and release name from
  `clj-redis-session` to `ring-redis-session` (thanks @plexus for the great
  suggestion!)

# 3.0.0

- This release introduces a breaking change in the API. Whereas prior to this
  release on used `ring-redis-session.core`, one now has to use
  `ring.redis.session`. If this is a problem for your project, simply ensure
  that you depend upon a version prior to the v3.0.0 release -- all of which
  uphold the old API.
