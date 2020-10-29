# Changelog

## v1.2.1
use AndroidX artifacts

## v1.2.0
* maven artifact migration `ru.noties` -&gt; `io.noties`
* package name migration `ru.noties` -&gt; `io.noties`

### v1.1.0
* `ActivityEvent.SAVE_INSTANCE_STATE` event added
* `FragmentEvent.SAVE_INSTANCE_STATE` event added
* renamed `FragmentEvent.CREATE_VIEW` -&gt; `FragmentEvent.VIEW_CREATED`
* renamed `FragmentEvent.DESTROY_VIEW` -&gt; `FragmentEvent.VIEW_DESTROYED`
* removed `ViewEvent` and `ViewLifebus`

### v1.0.2
* Add `view` functionality
  * `ViewEvent` (ATTACH, DETACH)
  * `ViewLifebus`
  * `ViewLifebusSource`
* ActivityLifebus#create now returns `ActivityLifebus` (extends Lifebus&lt;ActivityEvent&gt;)
* FragmentLifebus#create now returns `FragmentLifebus` (extends Lifebus&lt;FragmentEvent&gt;)

### v1.0.1
* CompositeSubscription must not allow new Subscriptions after it has been unsubscribed