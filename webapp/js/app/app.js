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

    var token = SocialAuth.get('ideas-ba-token'), _headers={};

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
// var SERVICE_ENDPOINT = "http://ideas-ba.com.ar/api/";
var SERVICE_ENDPOINT = "http://ideasba.org/api/";
// var SERVICE_ENDPOINT = "http://localhost:9000/api/";





