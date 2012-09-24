/*globals $,_*/
'use strict';
function Controller($scope, $http) {

  $scope.ideas_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas?order=id';
  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    console.log('we are ready...');
  });

  $scope.search = function(){
    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get($scope.ideas_endpoint).success(function(json) {
      $scope.ideas = json;

      // generate random votes -- mock
      angular.forEach($scope.ideas, function(value, key) {
        value.votes = $scope.votes();
      })
    });
  };

  $scope.votes = function() {
    return {
      pos: $scope.random(100),
      neg: $scope.random(20)
    }
  };

  $scope.random = function(top) {
    top = top || 100;
    return Math.floor(Math.random() * top+1);
  };

  $scope.search();

};