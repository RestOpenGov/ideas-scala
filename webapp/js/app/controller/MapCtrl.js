/*globals $,_*/
'use strict';
function MapCtrl($scope, $routeParams, $http) {

	$scope.currentPage = 1;

	$scope.maxGeoIdeas = 300;

	$scope.geoIdeasPerPage = 1;

	$scope.loadedGeoIdeas = 0;

	$scope.intervalId = 0;

	$scope.$on('$viewContentLoaded', function() {
		var latlng = new google.maps.LatLng(-34.597042, -58.359375);
	    var myOptions = {
	      zoom: 11,
	      center: latlng,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    var map = new google.maps.Map(document.getElementById("ideas_map"), myOptions);
	});

    $scope.init = function(){
    	$scope.intervalId = setInterval(function(){$scope.getIdeas();},500);
    }

    $scope.getIdeas = function() {
    	$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'geo?len='+$scope.geoIdeasPerPage+'&page='+$scope.currentPage,{},function(json) {
	      	$scope.currentPage++;
	      	$scope.loadedGeoIdeas += json.length;
	      	if(json.length==0 || $scope.loadedGeoIdeas>=$scope.maxGeoIdeas){
	      		clearInterval($scope.intervalId);
	      	}

 			angular.forEach(json, function(element, index){
 				//TODO procesar markers
 			});

		});
    };

    $scope.init();
    
};
