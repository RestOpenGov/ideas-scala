SocialAuth.providers.Twitter = {

  consumerKeys: {
    'ideasba.dev' :    'NlSXLXyTcTTsbA85wxpHdw',
    'ideas-ba.com.ar': 'nrtUVmeQAlNN2lFwxSlQA',
    'ideasba.org':     'wGtZZoq1CVcmwKkbhXfg'
  },

  init: function() {
    $('<script src="https://platform.twitter.com/anywhere.js?id=' + SocialAuth.getKey('Twitter') + '&amp;v=1" type="text/javascript"></script>').appendTo('body');
  },

  authenticate: function() {
    twttr.anywhere(function (T) {
      if(T.isConnected()) {
        SocialAuth.onAuthentication({ provider: 'twitter', token: twttr.anywhere.token })
      } else {
        T.signIn();
        T.bind("authComplete", function (e, user) {
          SocialAuth.onAuthentication({ provider: 'twitter', token: twttr.anywhere.token })
        });
      }
    });
  }
};


SocialAuth.providers.Facebook = {

  consumerKeys: {
      'localhost':       '486452174721099',
      'ideasba.dev':     '486452174721099',
      'ideas-ba.com.ar': '471376869573780',
      'ideasba.org':     '363371277085724'
  },

  init: function() {

    $('body').append('<div id="fb-root"></div>');

    window.fbAsyncInit = function() {
      FB.init({
        appId : SocialAuth.getKey('Facebook'),
            channelUrl : '/channel.html', 
            status : true,
            cookie : true,
            xfbml : true 
          });
    };

    (function(d){
       var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
       if (d.getElementById(id)) {return;}
       js = d.createElement('script'); js.id = id; js.async = true;
       js.src = "//connect.facebook.net/en_US/all.js";
       ref.parentNode.insertBefore(js, ref);
     }(document));
  },

  authenticate: function() {
    FB.getLoginStatus(function(loginResponse) {
      if(loginResponse.status == 'connected') {
        SocialAuth.onAuthentication({ provider: 'facebook', token: loginResponse.authResponse.accessToken })
      } else {
        FB.login(function(response) {
          if(response.authResponse) {
            SocialAuth.onAuthentication({ provider: 'facebook', token: response.authResponse.accessToken });
          }
        });
      }
    });
  }
};


SocialAuth.providers.Google = {

  consumerKeys: {
    'localhost':       '985870621747-7a3hngmm8k249qd4d1gcmk2jtf156msh.apps.googleusercontent.com',
    'localhost:9000':  '985870621747-2a4vrdvetqt3nf3l02pik89cdtq8qi8v.apps.googleusercontent.com',
    'ideasba.dev':     '985870621747-7a3hngmm8k249qd4d1gcmk2jtf156msh.apps.googleusercontent.com',
    'ideas-ba.com.ar': '985870621747-j927dgla2ulsugmu6bjpaicnae46ppc2.apps.googleusercontent.com',
    'ideasba.org':     '985870621747-ldmuh1o133lstageeo47jl3ggn0h9mvm.apps.googleusercontent.com'
  },

  init: function() {
    $('body').append('<script src="https://apis.google.com/js/client.js"></script>');
  },

  authenticate: function() {
    var config = {
      client_id: SocialAuth.getKey('Google'),
      scope: [ 
        'https://www.googleapis.com/auth/userinfo.email', 
        'https://www.googleapis.com/auth/userinfo.profile' 
      ]
    };

    gapi.auth.authorize(config, function() {
      SocialAuth.onAuthentication({ provider: 'google', token: gapi.auth.getToken().access_token })
    });
  }

};