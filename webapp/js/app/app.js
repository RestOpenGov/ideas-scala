var ideasModule = angular.module('ideas-ba', [], function($routeProvider, $locationProvider){

	//ROUTES
	$routeProvider.
    when('/ideas', {templateUrl: './partials/home.html',   controller: HomeCtrl}).
    when('/ideas/lista', {templateUrl: './partials/idea-list.html',   controller: IdeaListCtrl}).
    when('/ideas/nueva', {templateUrl: './partials/idea-form.html', controller: IdeaFormCtrl}).
    when('/ideas/:ideaId', {templateUrl: './partials/idea-detail.html', controller: IdeaDetailCtrl}).
    when('/user/edit', {templateUrl: './partials/user-form.html', controller: UserFormCtrl}).
    when('/user/:userId', {templateUrl: './partials/user-detail.html', controller: UserDetailCtrl}).
    otherwise({redirectTo: '/ideas'});

    //URL -- Remove # char from url
    //$locationProvider.html5Mode(true);
    //Commented because deeplinking is not working

});

ideasModule.factory('$USER', function() {
  var data = {};

  data.id = 2;
  data.token = '234234';
  
  data.setUser = function(json){
    data.id = json.id;
  };

  data.getId = function(){
    return data.id;
  };

  return data; // returning this is very important
});

ideasModule.run(function($rootScope,$http) {

  //Global functions!!
  $rootScope.ideaAjaxCall = function (_method,_url,_data,_callback,_callbackError) {
    //TODO add header with 

    var token = getCookie('ideas-ba-token'), _headers={};

    if (token) {
      _headers = {
        "Authorization": 'ideas-token='+token
      };
    }

    $http({method: _method, url: _url, data: _data, headers: _headers}).success(
        function (data, status, headers, config){
          //TODO validar respuesta inválida para mandar a login
          _callback(data, status, headers, config);  
        }
    ).error(_callbackError);
  }

});

//var SERVICE_ENDPOINT = "https://ideas-jugar.rhcloud.com/api/";
var SERVICE_ENDPOINT = "http://ideas-ba.com.ar/api/";
// var SERVICE_ENDPOINT = "http://localhost:9000/api/";

function setCookie(c_name,value,exdays) {
  var exdate=new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
  document.cookie=c_name + "=" + c_value;
}

function getCookie(c_name) {
  var i,x,y,ARRcookies=document.cookie.split(";");
  for(i=0;i<ARRcookies.length;i++) {
    x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
    y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
    x=x.replace(/^\s+|\s+$/g,"");
    if(x==c_name) {
    return unescape(y);
    }
  }
}

var Auth = {

  providerKey: {
    get: function(key) {
      if(typeof Auth.providerKey[window.location.host] == 'undefined') {
        return false;
      }
      return Auth.providerKey[window.location.host][key];
    },
    'localhost': {
      twitter: '',
      facebook: '486452174721099',
      google: '985870621747-7a3hngmm8k249qd4d1gcmk2jtf156msh.apps.googleusercontent.com'
    },
    'localhost:9000': {
      twitter: '',
      facebook: '486452174721099',
      google: '985870621747-2a4vrdvetqt3nf3l02pik89cdtq8qi8v.apps.googleusercontent.com'
    },
    'ideasba.dev': {
      twitter: 'NlSXLXyTcTTsbA85wxpHdw',
      facebook: '486452174721099',
      google: '985870621747-7a3hngmm8k249qd4d1gcmk2jtf156msh.apps.googleusercontent.com'
    },
    'ideas-jugar.rhcloud.com': {
      twitter: 'nrtUVmeQAlNN2lFwxSlQA', // ideasba registered app
      // twitter: 'z7F8JCoVZrmXpTiG18tw',
      facebook: '486452174721099',
      google: '985870621747.apps.googleusercontent.com'
    },
    'ideas-ba.com.ar': {
      twitter: '',
      facebook: '',
      google: '985870621747-j927dgla2ulsugmu6bjpaicnae46ppc2.apps.googleusercontent.com'
    },
    'ideasba.org': {
      twitter: '',
      facebook: '',
      google: '985870621747-ldmuh1o133lstageeo47jl3ggn0h9mvm.apps.googleusercontent.com'
    }
  },

  initiated: false,

  initTwitter: function() {
    $('<script src="https://platform.twitter.com/anywhere.js?id=' + Auth.providerKey.get('twitter') + '&amp;v=1" type="text/javascript"></script>').appendTo('body');
  },

  initFacebook: function() {
    $('body').append('<div id="fb-root"></div>');

    window.fbAsyncInit = function() {
      FB.init({
        appId : Auth.providerKey.get('facebook'),
            //channelUrl : '//ideas-jugar.rhcloud.com/channel.html', 
            //channelUrl : '//ideas-ba.com.ar/channel.html', 
            channelUrl : '//localhost/ideas-ba/channel.html', 
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

  initGoogle: function() {
    $('body').append('<script src="https://apis.google.com/js/client.js"></script>');
  },

  init: function() {

    if(this.initated) {
      return;
    }

    for(var i in this) {
      if(typeof this[i] == 'function' && i != 'init') {
        this[i].apply();
      }
    }

    this.initiated = true;
  }
};

$('#authModal').on('shown', function() {
  Auth.init();
});



