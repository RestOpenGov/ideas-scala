/*globals $,_*/
'use strict';
function MapCtrl($scope, $routeParams, $http) {

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

           $scope.infowindow = new google.maps.InfoWindow({
           		maxWidth : 200
           	});

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
 				console.log(element);
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

            	google.maps.event.addListener(marker, 'click', function() {
            		var info = $scope.infowindow;
            		var nameIdea = element.idea.name;
            		var ubication = element.name;
            		var link = HOST + '/#/ideas/' + element.idea.id;

            		var message = "";
            		//message += '<div id="content">';
            		message += '<a href="' + link +'"><h5>' + nameIdea + '</h5></a>';
            		message += '<p> Ubicacion: ' + ubication + '</p>' ;
            		//message += '</div>';
            		info.setContent(message);
            		info.setPosition(latLng);
            		info.open($scope.map, marker);
        		});

 			});

		});
    };

    $scope.init();
    
};
