/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $routeParams, $http) {

  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.search = function(){

  /*  if(!$scope.filter){
      $scope.filter = '';
    }*/

    console.log($routeParams);

    //todo filter

    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get(SERVICE_ENDPOINT+'ideas?'+ $.param($routeParams)).success(function(json) {
      $scope.ideas = json;
    });
  };

  $scope.search();

};