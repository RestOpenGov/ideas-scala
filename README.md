Ideas-ba
========

Ideas-ba is a social website that allows citizens to share ideas, complaints, questions and proposals to improve their cities.

Inspired by sites like [stackoverflow](http://stackoverflow.com/), that allows collective knowledge to help us solve problems that as developers we face everyday.

We'd like to apply the same principles in order to collectively improve our cities.

Ideas-ba is and will be free-software and is released under the Apache 2.0 license, so copying it is not only allowed but highly encouraged. Go on and fork it!

The project is divided in two components: the front-end and the back-end.

Ideas-ba back-end
================

Ideas-ba back-end is a web service built with [play framework 2](http://www.playframework.org/) and [scala](http://www.scala-lang.org/).

It is a [RESTFul](http://en.wikipedia.org/wiki/Representational_state_transfer) service returning data as [json](http://en.wikipedia.org/wiki/JSON).

The api will allow you to access all the functionality of Ideas-ba application, like submitting and updating, adding comments, voting, etc.

The web service is up and running on [OpenShift](https://openshift.redhat.com), so that you can start playing with it right from the cloud, without installing it locally. You can find it at http://ideas-jugar.rhcloud.com/api

If you want to have a look at the code and get your hands dirty, be sure to read the [README file](https://github.com/RestOpenGov/ideas-ba/blob/master/webservice/README.md)

Ideas-ba front-end
================

Ideas-ba frontend is a client-side web application built with [angular.js](http://angularjs.org/) javascript framework.

It accesses the ideas-ba data exclusively using the rest web service.

You can install the web service locally (check the [README file](https://github.com/RestOpenGov/ideas-ba/blob/master/webservice/README.md)) or you can just point it at https://github.com/RestOpenGov/ideas-ba/tree/master/webservice. Because the front-end is exclusively a client-side application, you can run it without even installing a local web server. Opening the index.html from the file system should be enough to get you up an running.

To start playing with the front-end be sure to read the [README file](https://github.com/RestOpenGov/ideas-ba/blob/master/webapp/README.md).

## License

This software is distributed under the Apache 2.0 license: http://www.apache.org/licenses/LICENSE-2.0

