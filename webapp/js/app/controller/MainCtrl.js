/*globals $,_*/
'use strict';
function MainCtrl($scope, $routeParams, $http, $location) {

  $scope.searchFilter = '';

  $scope.$on('$viewContentLoaded', function() {
    
    $('.popover-component').popover({
      trigger : 'hover',
      delay: { show: 500, hide: 2000 }
    });

  });
      
  $scope.search=function(){
    console.log($scope.searchQuery);
    if($scope.searchQuery){
      $location.path("/ideas/lista").search({"filter": "description:"+$scope.searchQuery});      
    }else{
      console.log('else');
      $location.path("/ideas/lista").search();
    }
    $scope.searchQuery = '';
  };

};