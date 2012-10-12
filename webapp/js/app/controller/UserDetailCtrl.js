/*globals $,_*/
'use strict';
function UserDetailCtrl($scope, $routeParams, $http) {
  $scope.userId = $routeParams.userId;

  $scope.user = {};
  
  $scope.comments = [];

  $scope.ideas = [];
  
  $scope.search = function(){

    //USER
    $http.get(SERVICE_ENDPOINT+'users/'+$scope.userId).success(function(json) {
      $scope.user = json;
    });

    //COMMENTS - TODO add filter per author
    $http.get(SERVICE_ENDPOINT+'comments?len=3').success(function(json) {
      $scope.comments = json;
    });

    //IDEAS - TODO add filter per author
    $http.get(SERVICE_ENDPOINT+'ideas?len=3').success(function(json) {
      $scope.ideas = json;
    });

  };

  $scope.search();
}