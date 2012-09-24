angular.module('ideas-ba', []).
  config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/ideas', {templateUrl: 'partials/idea-list.html',   controller: IdeaListCtrl}).
      when('/ideas/:ideaId', {templateUrl: 'partials/idea-detail.html', controller: IdeaDetailCtrl}).
      otherwise({redirectTo: '/ideas'});
}]);