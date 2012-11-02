/*globals $,_*/
'use strict';
function UserDetailCtrl($scope, $routeParams, $http) {
  $scope.userId = $routeParams.userId;

  $scope.user = {};
  
  $scope.comments = [];

  $scope.ideas = [];
  
  $scope.search = function(){

    //USER
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'users/'+$scope.userId,{},function(json) { 
      $scope.user = json;
    });

    //COMMENTS - TODO add filter per author
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'comments?len=3&q=author.id:'+ $scope.userId,{},function(json) { 
      $scope.comments = json;
    });

    //IDEAS - TODO add filter per author
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas?len=3&q=author.id:'+ $scope.userId,{},function(json) {       
      $scope.ideas = json;
    });

  };

  $scope.search();
}