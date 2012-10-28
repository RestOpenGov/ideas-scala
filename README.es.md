Ideas-ba
========

Ideas-ba es un sitio web social que permita a los ciudadanos compartir sus ideas, reclamos, preguntas y propuestas para mejorar su ciudad.

Inspirado en sitios como [stackoverflow](http://stackoverflow.com/), que a partir del conocimiento colectivo, nos permiten ayudar a resolver los problemas que, como desarrolladores, enfrentamos día a día.

Nos gustaría aplicar los mismos principios para mejorar nuestra ciudad colectivamente.

Ideas-ba es y seguirá siendo software libre, lanzado bajo la licencia de Apache 2.0, por lo que no sólo te permitimos copiarlo sino que te animamos a hacerlo. Go on and fork it!

El proyecto se encuentra dividido en 2 partes: el front-end y el back-end.

Back-end de Ideas-ba 
================

El back-end de Ideas-ba es un servicio web desarrollado con [play framework 2](http://www.playframework.org/) y [scala](http://www.scala-lang.org/).

Se trata de un servicio [RESTFul](http://en.wikipedia.org/wiki/Representational_state_transfer) que brinda información en formato [json](http://en.wikipedia.org/wiki/JSON).

La API permitirá acceder a toda la funcionalidad de Ideas-ba, como la inserción y modificación de ideas, comentarios, votos, etc.

El servicio web está disponible en [OpenShift](https://openshift.redhat.com), por lo que puedes empezar a jugar con él simplemente desde la nube, sin necesidad de instalarlo localmente. Puedes encontrarlo en http://ideas-ba.com.ar/api

Si quieres ver el código y ensuciarte un poco las manos, no olvides leer el archivo [README file](https://github.com/RestOpenGov/ideas-ba/blob/master/webservice/README.md)

Front-end de Ideas-ba 
================

El front-end de Ideas-ba es una aplicación web del lado del cliente desarrollada con [angular.js](http://angularjs.org/), un framework para javascript.

La aplicación utliza exclusivamente el servicio web REST para acceder a la información de Ideas-ba.

Puedes instalar el servicio web de manera local (consultá el [Archivo README](https://github.
com/RestOpenGov/ideas-ba/blob/master/webservice/README.md)) o simplemente apuntarlo en
https://github.com/RestOpenGov/ideas-ba/tree/master/webservice, ya que, al ser una aplicación del lado del cliente, puedes ejecutarlo sin necesidad de instalar un servidor local. Tan sólo abrir index.html debería ser suficiente para que lo veas funcionando.

Para comenzar a jugar con el front-end de Ideas-ba, puedes leer el [Archivo RE
ADME](https://github.com/RestOpenGov/ideas-ba/blob/master/webapp/README.md)

## Licencia

Este software es distribuído bajo la licencia Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0

