angular.module('ideas-ba', []).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/ideas', {templateUrl: 'partials/home.html',   controller: HomeCtrl}).
      when('/ideas/lista', {templateUrl: 'partials/idea-list.html',   controller: IdeaListCtrl}).
      when('/ideas/nueva', {templateUrl: 'partials/idea-form.html', controller: IdeaFormCtrl}).
      when('/ideas/:ideaId', {templateUrl: 'partials/idea-detail.html', controller: IdeaDetailCtrl}).
      otherwise({redirectTo: '/ideas'});
}]);

var SERVICE_ENDPOINT = "http://ideas-jugar.rhcloud.com/api/";