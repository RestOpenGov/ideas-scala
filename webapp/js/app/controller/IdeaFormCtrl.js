/*globals $,_*/
'use strict';
function IdeaFormCtrl($scope, $routeParams, $http) {
	$scope.types = [];

	$scope.idea = {};

	$scope.init = function(){
		$http.get(SERVICE_ENDPOINT+'types').success(function(json) {
	      $scope.types = json;
	         	      console.log(json);
	    });
	};

	$scope.saveIdea = function(){
		$scope.idea.author = {id:1};
		$http.post(SERVICE_ENDPOINT+'ideas',$scope.idea).success(function(json) {
	      console.log('saved!');
   	      console.log(json);
	    }).error(function(data, status, headers, config) {
	    	alert('ERROR AL DAR DE ALTA LA IDEA');
	    });
	};

	$scope.init();

}