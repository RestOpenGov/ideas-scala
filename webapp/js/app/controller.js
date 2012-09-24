/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $http) {

  $scope.ideas_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas?order=id';
  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.search = function(){
    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get($scope.ideas_endpoint).success(function(json) {
      $scope.ideas = json;

      // generate random votes -- mock
      angular.forEach($scope.ideas, function(value, key) {
        value.votes = votes();
      })
    });
  };

  $scope.search();

};

//-------------------------

function IdeaDetailCtrl($scope, $routeParams, $http) {
  $scope.ideaId = $routeParams.ideaId;
  $scope.idea_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas/'+$scope.ideaId;
  $scope.idea = {};
  
  $scope.search = function(){
    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get($scope.idea_endpoint).success(function(json) {
      $scope.idea = json;
      $scope.idea.votes = votes();
      });
  };

  $scope.search();

};


function votes() {
  return {
    pos: random(100),
    neg: random(20)
  }
};

function random (top) {
  top = top || 100;
  return Math.floor(Math.random() * top+1);
};