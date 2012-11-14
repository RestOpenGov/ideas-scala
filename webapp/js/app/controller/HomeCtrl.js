/*globals $,_*/
'use strict';
function HomeCtrl($scope, $routeParams, $http, $USER) {

	$scope.rows = [];

	var types = [];

	$scope.currentPage = 1;
	$scope.maxGeoIdeas = 300;
	$scope.geoIdeasPerPage = 1;
	$scope.loadedGeoIdeas = 0;
	$scope.intervalId = 0;

	$scope.$on('$viewContentLoaded', function() {
		var latlng = new google.maps.LatLng(-34.597042, -58.386475);
	    var myOptions = {
	      zoom: 12,
	      center: latlng,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	    };
	    $scope.map = new google.maps.Map(document.getElementById("ideas_map"), myOptions);
	});

	$scope.init = function(){
    	$scope.intervalId = setInterval(function(){$scope.getIdeas();},1000);
    }

    $scope.getIdeas = function() {

    	$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'geo?len='+$scope.geoIdeasPerPage+'&page='+$scope.currentPage,{},function(json) {
	      	$scope.currentPage++;
	      	$scope.loadedGeoIdeas += json.length;
	      	if(json.length==0 || $scope.loadedGeoIdeas>=$scope.maxGeoIdeas){
	      		clearInterval($scope.intervalId);
	      	}
 			angular.forEach(json, function(element, index){
 				var typeId = element.idea.type.id
 				var image = '/img/markers/idea.png'; // default.
 				if (typeId === 1) {
 					image = '/img/markers/idea.png';
 				} else if (typeId === 2) {
 					image = '/img/markers/reclamo.png';
 				} else if (typeId === 3) {
 					image = '/img/markers/pregunta.png';
 				} else if (typeId === 4) {
 					image = '/img/markers/sugerencia.png';
 				}

				var latLng = new google.maps.LatLng(element.lat, element.lng);
            	var marker = new google.maps.Marker({
                position: latLng,
                map: $scope.map,
                icon: image
            });
 			});

		});
    };

    $scope.init();

  	$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas?len=10&order=created desc',{}, function(json) {
      $scope.ideas = json;
	});



}


