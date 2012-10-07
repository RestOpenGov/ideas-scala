/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $routeParams, $http) {

  $scope.filter = $routeParams.filter || false;

  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.search = function(){

    var filter = '?';

    if($scope.filter){
      filter += 'q='+$scope.filter;
    }

    //todo filter

    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get(SERVICE_ENDPOINT+'ideas'+filter).success(function(json) {
      $scope.ideas = json;
    });
  };

  $scope.search();

};