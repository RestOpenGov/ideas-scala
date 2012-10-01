This is the backend web service for ideas-ba
============================================

## See it in action

Ideas-ba backend is up and running in [OpenShift](https://openshift.redhat.com) on http://ideas-jugar.rhcloud.com/api.

You can play with it without installing anything on you computer using any http client, like [curl](http://en.wikipedia.org/wiki/CURL), [poster](https://addons.mozilla.org/es/firefox/addon/poster/) for firefox or [Dev HTTP Client](https://chrome.google.com/webstore/detail/aejoelaoggembcahagimdiliamlcdmfm) for chrome.

An example, with curl

```
curl --request GET http://ideas-jugar.rhcloud.com/api/users?filter=nardoz

[{"name":"Mister Nardoz","avatar":"nardoz.avatar.png","email":"nardoz@hotmail.com","id":1,"created":"2012-09-23T11:45:00Z","nickname":"nardoz"}]
```

Tip: for a prettier output you can use json.tool form python, like this:

```
curl --request GET http://ideas-jugar.rhcloud.com/api/users?filter=nardoz | python -m json.tool
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   144  100   144    0     0    132      0  0:00:01  0:00:01 --:--:--   626
[
    {
        "avatar": "nardoz.avatar.png", 
        "created": "2012-09-23T11:45:00Z", 
        "email": "nardoz@hotmail.com", 
        "id": 1, 
        "name": "Mister Nardoz", 
        "nickname": "nardoz"
    }
]
```

## Installing it locally

To start playing locally with the web service you need the following prerequisites:

- JDK 1.7 (http://www.oracle.com/technetwork/java/javase/downloads/jdk7u7-downloads-1836413.html)

- Play framework 2.0.4 (http://download.playframework.org/releases/play-2.0.4.zip)

Check the [install documentation](http://www.playframework.org/documentation/latest/Installing) for play framework.

Then just clone the repo with

```
git clone git@github.com:RestOpenGov/ideas-ba.git

cd ideas-ba/webservice/
```

and then start the web service with

```
play "run -DapplyEvolutions.default=true"
```

or just

```
./run
```

To see the web service in action, open a browser and go to:

```
http://localhost:9000/api
```

## Running the automated tests

In the test folder you'll find many automated tests.

To run them just enter play console with

```
play
```

And then run

```
test
```

to run the whole test suite.

Optionally you can run a specific test with

```
test-only test.models.UserSpec
```

or a group of tests with

```
test-only test.models.*
```

Tip: if you start working on the source code, you can tell play to automatically run your tests whenever a source file is changed, like this:

```
~test-only test.models.*
```

The "~" tells play to run that command whenever a file change. That way you could also tell play to compile the application with:

```
~compile
```