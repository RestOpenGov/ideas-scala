var ideasModule = angular.module('ideas-ba', [], function($routeProvider, $locationProvider){

	//ROUTES
	$routeProvider.
    when('/ideas', {templateUrl: '/partials/home.html',   controller: HomeCtrl}).
    when('/ideas/lista', {templateUrl: '/partials/idea-list.html',   controller: IdeaListCtrl}).
    when('/ideas/nueva', {templateUrl: '/partials/idea-form.html', controller: IdeaFormCtrl}).
    when('/ideas/:ideaId', {templateUrl: '/partials/idea-detail.html', controller: IdeaDetailCtrl}).
    when('/user/edit', {templateUrl: '/partials/user-form.html', controller: UserFormCtrl}).
    when('/user/:userId', {templateUrl: '/partials/user-detail.html', controller: UserDetailCtrl}).
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

});

var SERVICE_ENDPOINT = "http://ideas-ba.com.ar/api/";
// var SERVICE_ENDPOINT = "http://ideas-jugar.rhcloud.com/api/";
// var SERVICE_ENDPOINT = "http://localhost:9000/api/";