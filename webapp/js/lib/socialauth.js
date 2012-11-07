
$(function() {

  $('#authModal')
    .on('shown', function() {
      SocialAuth.init();
    })
    /*
    .css({
      'width': function() { 
        return '700px';  
      },
      'margin-left': function() { 
        return -($(this).width() / 2); 
      }
    });*/

  $('.authButton').click(function() {
    SocialAuth.authenticate($(this).attr('rel'));
  });
});

var SocialAuth = {

  options: {
    storage: 'cookie'
  },

  user: {},
  initialized: false,
  providers: {},
  storageEngines: {},

  init: function() {

    if(this.initialized) {
      console.error('SocialAuth has already been initialized.');
      return;
    }

    for(var provider in SocialAuth.providers) {
      if(typeof SocialAuth.providers[provider] != 'undefined') {
        SocialAuth.providers[provider].init();
      }
    }

    this.initialized = true;
  },

  getKey: function(provider) {

    if(typeof SocialAuth.providers[provider] == 'undefined') {
      console.error('Unregistered provider <' + provider + '>');
      return false;
    }

    if(typeof SocialAuth.providers[provider].consumerKeys[window.location.host] == 'undefined') {
      console.error('Unregistered host <' + window.location.host + ' for provider <' + provider + '>');
      return false;
    }

    return SocialAuth.providers[provider].consumerKeys[window.location.host];
    
  },

  authenticate: function(provider) {
    if(typeof SocialAuth.providers[provider] == 'undefined') {
      console.error('Unregistered provider <' + provider + '>');
      return false;
    }

    return SocialAuth.providers[provider].authenticate();
  },

  onAuthentication: function(data) { 
    console.log('No onAuthentication specified.'); 
    console.log(data); 
  },

  getUser: function() {
    this.get('user');
  },

  setUser: function(user) {
    this.set('user', user);
  },

  clearUser: function() {
    this.set('user', '');
  },

  get: function(key) {
    return SocialAuth.storageEngines[this.options.storage].get(key);
  },

  set: function(key, value) {
    SocialAuth.storageEngines[this.options.storage].set(key, value);
  }

};


SocialAuth.storageEngines.cookie = {

  ttl: 7, // days

  get: function(key) {
    var i, x, y, cookies = document.cookie.split(";");

    for(i=0; i < cookies.length; i++) {
      x = cookies[i].substr(0, cookies[i].indexOf("="));
      y = cookies[i].substr(cookies[i].indexOf("=") + 1);
      x = x.replace(/^\s+|\s+$/g,"");

      if(x == key) {
        return JSON.parse(y);
      }

      return false;
    }
  },

  set: function(key, value) {
    var expireDate = new Date();
    expireDate.setDate(date.getDate() + this.ttl);
    value = JSON.stringify(value) + ((days == null) ? "" : "; expires=" + expireDate.toUTCString());
    document.cookie = key + "=" + value;
  }

};



