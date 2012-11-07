/*globals $,_*/
'use strict';
function IdeaFormCtrl($scope, $routeParams, $http, $location, $USER) {
	$scope.types = [];

	$scope.idea = {};

	$scope.area = {};

	$scope.timeout = {};

	$scope.suggestions = [];

	$scope.selectedSuggestions = [];

	$scope.$on('$viewContentLoaded', function() {


		$scope.area = $('#areaMessage').wysihtml5(
			{
				"stylesheets": ["/css/bootstrap-wysihtml5-0.0.2.css"],
				events: {
			        "newword:composer" : 
			        	function() { 
		        			$scope.suggestions = [];
			        		$scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'tests/categorize',$scope.area.val(),
			        			function(json) {
			        				//filtrar las seleccionadas o las que ya le pregunté
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
		//Completo la descripcion
		$scope.idea.description = $scope.area.val();

	    $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas',$scope.idea,function(json) {  
			// preparing the tags array
			var arrTags = $("input[name='hidden-tagsjax']").attr("value").split(",");
			$scope.ideaAjaxCall('PUT',SERVICE_ENDPOINT+'ideas/' + json.id + '/tags',arrTags,function(jsonTags) { 
				
				// put tags for the new idea and redirect
				$location.path("/ideas/"+json.id).search();
				
		    },function(data, status, headers, config) {
		    	alert('ERROR AL DAR DE ALTA TAGS DE LA IDEA');
		    });
		    
		
		},function(data, status, headers, config) {
	    	alert('ERROR AL DAR DE ALTA LA IDEA');
	    });
	};

	$scope.removeSuggestion = function(i){
		//TODO
		$scope.suggestions.unshift(i);
		$('#suggestion-'+i).remove();
	};

	$scope.addSuggestion = function(i){
		//TODO
		console.log(i);
	};

	$scope.init();

}