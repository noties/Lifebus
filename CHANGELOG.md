# Changelog

## v1.0.2
* Add `view` functionality
  * `ViewEvent` (ATTACH, DETACH)
  * `ViewLifebus`
  * `ViewLifebusSource`
* ActivityLifebus#create now returns `ActivityLifebus` (extends Lifebus<ActivityEvent>)
* FragmentLifebus#create now returns `FragmentLifebus` (extends Lifebus<FragmentEvent>)

### v1.0.1
* CompositeSubscription must not allow new Subscriptions after it has been unsubscribed