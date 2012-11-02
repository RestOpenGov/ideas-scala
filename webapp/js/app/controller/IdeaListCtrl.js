/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $routeParams, $http) {

  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.init = function(){
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas?'+ $.param($routeParams),{},function(json) { 
      $scope.ideas = json;
    });
  };

  $scope.init();

};