# Lifebus

Utility to trigger action on Android lifecycle events. Contains implementation based on [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html) and own implementation with extended set of events for Activity, Fragment and View.

[![lifebus-arch](https://img.shields.io/maven-central/v/ru.noties/lifebus-arch.svg?label=lifebus-arch)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22lifebus-arch%22)
[![lifebus](https://img.shields.io/maven-central/v/ru.noties/lifebus.svg?label=lifebus)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22lifebus%22)
[![subscription](https://img.shields.io/maven-central/v/ru.noties/subscription.svg?label=subscription)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22subscription%22)

---

```java
@Override
public void onStart() {
    super.onStart();
    
    final HoldMe holdMe = obtainSomething();
    
    lifebus.on(Lifecycle.Event.ON_STOP, () -> {
        holdMe.release();
    });
}
```

```java
@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    final MilkyWayBinding binding = MilkyWayBinding.bind(this, view);

    lifebus.on(FragmentEvent.DESTROY_VIEW, binding::release);
}
```

The idea behind this: most lifecycle events have their counterparts. `onCreate/onDestroy`, `onStart/onStop`, `onResume/onPause`, etc. And having something initialized for example `onStart` would require releasing it `onStop`. No need for a _stream_ of events. You register once, you receive an event exactly once, as there _should_ not be 2 subsequent calls for `onStart` without interrupting them with at least one `onStop`.


## Arch

```gradle
implementation 'ru.noties:lifebus-arch:1.0.2'
```

**NB** this artifact relies on `android.arch.lifecycle:common-java8:1.1.1` in order to receive all lifecycle events without using annotation processor. I have tested it on a project with `JavaVersion.VERSION_1_7` and it compiled and worked. But I cannot guarantee that this will be true with the future releases of architecture components.

```java
// Pass an instance of `LifecycleOwner`
private final LifebusArch lifebus = LifebusArch.create(this);
``` 

All events from `Lifecycle.Event` are available to be notified about except the `ON_ANY` which will throw `IllegalStateException`.

```java
lifebus.on(Lifecycle.Event.ON_DESTROY, () -> {});
```


## Extended set of events

```gradle
implementation 'ru.noties:lifebus:1.0.2'
```

Activity via (`ActivityEvent`):
* CREATE
* START
* RESUME
* PAUSE
* STOP
* DESTROY

Fragment via (`FragmentEvent`):
* ATTACH
* CREATE
* CREATE_VIEW
* START
* RESUME
* PAUSE
* STOP
* DESTROY_VIEW
* DESTROY
* DETACH

View via (`ViewEvent`):
* ATTACH
* DETACH

```java
private final Lifebus<ActivityEvent> lifebus = 
        ActivityLifebus.create(application, activity);
```

```java
private final Lifebus<FragmentEvent> lifebus = 
        FragmentLifebus.create(fragmentManager, fragment);
```

```java
private final Lifebus<ViewEvent> lifebus = ViewLifebus.create(view);
```

Please note that after ActivityLifebus receives a `DESTROY` event it will automatically unsubscribe any listeners (after dispatching a notification). The same for the FragmentLifebus and ViewLifebus with `DETACH` event.

## Subscription

```gradle
implementation 'ru.noties:subscription:1.0.2'
```

Represents an abstraction for the _subscribe_/_release_ functionality. Contains 2 implementations: `Subscription` and `CompositeSubscription`. Internally used by `lifebus`.

```java
@NonNull
private Subscription doIt(@NonNull Action action) {
    // do it
}

@Override
public void onStart() {
    super.onStart();

    doIt(result -> Log.i("DID_IT", "Result: " + result))
            .accept(subscription -> lifebus.on(FragmentEvent.STOP, subscription::unsubscribe));
}
```

```java
@Override
public void onStart() {
    super.onStart();

    final CompositeSubscription compositeSubscription = CompositeSubscription.create()
            .accept(subscription -> lifebus.on(ActivityEvent.STOP, subscription::unsubscribe));

    doIt(result -> Log.i("DID_IT", "Result: " + result))
            .accept(compositeSubscription.add());
}
```

## License

```
  Copyright 2018 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```
