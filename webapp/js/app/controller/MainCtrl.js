/*globals $,_*/
'use strict';
function MainCtrl($scope, $routeParams, $http, $location, $USER) {

  $scope.searchFilter = '';

  $scope.userList = [];
  $scope.selectedUserId = 1;

  $scope.$on('$viewContentLoaded', function() {
    
    $('.popover-component').popover({
      trigger : 'hover',
      delay: { show: 500, hide: 2000 }
    });

  });
  
  $scope.init = function() {
    $http.get(SERVICE_ENDPOINT+'users').success(function(json) {
      $scope.userList = json;
    });
    $scope.changeUser();//select first
  }

  $scope.changeUser = function() {
    $http.get(SERVICE_ENDPOINT+'users/'+$scope.selectedUserId).success(function(json) {
      $USER.setUser(json);
    }); 
  };

  $scope.search = function(){
    if($scope.searchQuery){
      $location.path("/ideas/lista").search({"filter": $scope.searchQuery});      
    }else{
      $location.path("/ideas/lista").search();
    }
    $scope.searchQuery = '';
  };

  $scope.init();
  
};