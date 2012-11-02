/*globals $,_*/
'use strict';
function HomeCtrl($scope, $routeParams, $http, $USER) {

	$scope.rows = [];

	var types = [];

	$scope.$on('$viewContentLoaded', function() {

	});

	//Types
	$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'types',{},function(json) {
      var row = 0,obj = {},loadedTypes=0;
      types = json;

      //Iterate ideas type
      angular.forEach(json, function(t, i){
      	var type = t.id;

      	//Retrieve 3 ideas per type TODO add type filter
      	$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas?len=3&order=created DESC&q=type.id:'+type,{},function(json) {

	      	types[i]['ideas'] = json;
	      	loadedTypes++;
	      	
	      	//split by rows
	      	if(types.length == loadedTypes){
 				angular.forEach(types, function(t, i){
		          	if(i%2==0){
			     		row++;
			      		$scope.rows[row]={};
			      	}
			      	$scope.rows[row][t.id] = t;
	      		});
	      	}

		});
      });
    });



}


