'use strict';


if(location.hostname=="localhost" || location.hostname=="127.0.0.1" ){
  
  // this need to run only in prod enviroment
   var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-35728816-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
}
