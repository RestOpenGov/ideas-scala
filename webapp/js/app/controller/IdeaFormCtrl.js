/*globals $,_*/
'use strict';
function IdeaFormCtrl($scope, $routeParams, $http, $location, $USER) {
	$scope.types = [];

	$scope.idea = {};

	$scope.area = {};

	$scope.timeout = {};

	$scope.suggestions = [];

	$scope.ignoreSuggestions = {};

	$scope.selectedGeoSuggestions = [];

	$scope.$on('$viewContentLoaded', function() {

		$scope.area = $('#areaMessage').wysihtml5(
			{
				"stylesheets": ["/css/bootstrap-wysihtml5-0.0.2.css"],
				events: {
			        "newword:composer" : 
			        	function() { 
		        			$scope.suggestions = [];
			        		$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'categorize?input=' + $scope.area.val(), null,
			        			function(json) {
			        				//filtrar las seleccionadas o las que ya le pregunté
			        				json = json.filter(function (e) {
									  return typeof($scope.ignoreSuggestions[e.original]) === "undefined" && e.original != "undefined";
									});
			        				$scope.suggestions = json;
					    		},
						    	function(error) {  
						      		console.log(error);
						    	},
					    	'text/plain');
						}
				}
			});

	    $(".tagManager").tagsManager({
	        preventSubmitOnEnter: true,
	        typeahead: true,
	        typeaheadAjaxSource: SERVICE_ENDPOINT +'tags',
	        typeaheadAjaxPolling: true,
	        // AjaxPush: SERVICE_ENDPOINT +'tags/', 
	        blinkBGColor_1: '#FFFF9C',
	        blinkBGColor_2: '#CDE69C',
	      });
	});

	$scope.init = function(){
		$scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'types',{},function(json) {  
	      $scope.types = json;
	    });
	};

	$scope.saveIdea = function(){

		//TODO validar el text area porque está oculto y el select porque la mierda esta no lo reconoce.

		//Completo la descripcion
		$scope.idea.description = $scope.area.val();

	    $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas',$scope.idea,function(savedIdea) {  
			// preparing the tags array
			var arrTags = $("input[name='hidden-tagsjax']").attr("value").split(",");

			//Help me!! Refactorize me using promises!
			$scope.ideaAjaxCall('PUT',SERVICE_ENDPOINT+'ideas/' + savedIdea.id + '/tags',arrTags,function(jsonTags) { 
				var geoItem = {},loaded=0;
				
				angular.forEach($scope.selectedGeoSuggestions, function(t, i){
					geoItem.lat = t.lat;
					geoItem.lng = t.lng;
					geoItem.name = t.text;

					$scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas/' + savedIdea.id + '/geo',geoItem,function(jsonTags) { 
						
						loaded++;

						if($scope.selectedGeoSuggestions.length==loaded){
							$location.path("/ideas/"+savedIdea.id).search();
						}

					},function(data, status, headers, config) {
			    		alert('ERROR AL DAR DE ALTA TAGS DE LA IDEA');
			    	});
					
				});
			
		    },function(data, status, headers, config) {
		    	alert('ERROR AL DAR DE ALTA TAGS DE LA IDEA');
		    });
		
		},function(data, status, headers, config) {
	    	alert('ERROR AL DAR DE ALTA LA IDEA');
	    });
	};

	$scope.removeSuggestion = function(i){
		$scope.sliceSuggestion(i,$scope.suggestions);
	};

	$scope.removeGeoSuggestion = function(i){
		$scope.sliceSuggestion(i,$scope.selectedGeoSuggestions);
	};

	$scope.addSuggestion = function(i){
		var newObj = $scope.sliceSuggestion(i,$scope.suggestions);
		angular.forEach(newObj.tags, function(t, i){
			$scope.addTag(t);
		});

		if(newObj.lat!=null){
			$scope.selectedGeoSuggestions.push(newObj);
		}

		//TODO add tag to textarea - hack the plugin!
		var wraper = '<span class="label label-success">'+newObj.original+'</span>';
		//$scope.area.val($scope.area.val().replace(newObj.original,wraper));
	};

	$scope.sliceSuggestion = function(i,array){
		var removedItem = array.splice(i,1);
		$scope.ignoreSuggestions[removedItem[0].original]=removedItem[0];
		return removedItem[0];
	};

	$scope.addTag = function(t){
		$(".tagManager").val(t).blur();
	};

	$scope.init();

}