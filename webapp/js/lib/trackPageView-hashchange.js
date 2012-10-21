
$(function(){
  
  // Bind an event to window.onhashchange that, when the hash changes, gets the
  // hash and adds the class "selected" to any matching nav link.
  $(window).hashchange( function(){
    var hash = location.hash;

    // Seo Feature (Change title )
    // document.title = ( hash.replace( /^#/, '' ) || 'blank' ) + '.';

    var params = {}, 
    queryString = location.hash.substring(1), regex = /([^&=]+)=([^&]*)/g, m; 
    while (m = regex.exec(queryString)) { params[decodeURIComponent(m[1])] = decodeURIComponent(m[2]); } 
    
    // Just it prod enviroment
    if(location.hostname=="localhost" || location.hostname=="127.0.0.1" ){
      // It will inform google analytics about the event
      _gaq.push(['_trackPageview', queryString]);
    }
  })
  
  // Since the event is only triggered when the hash changes, we need to trigger
  // the event now, to handle the hash the page may have loaded with.
  $(window).hashchange();
  
});

