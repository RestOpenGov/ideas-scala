/*globals $,_*/
'use strict';
function IdeaFormCtrl($scope, $routeParams, $http, $location, $USER) {
	$scope.types = [];

	$scope.idea = {};

	$scope.editor = {};

	$scope.$on('$viewContentLoaded', function() {
		$scope.editor = new nicEditor({
			buttonList : ['bold','italic','underline','strikeThrough','ol','ul','forecolor','link','unlink'],
			iconsPath : '/img/nicEditorIcons.gif'
		})

		$scope.editorInstance = $scope.editor.panelInstance('areaMessage');
		
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
		$http.get(SERVICE_ENDPOINT+'types').success(function(json) {
	      $scope.types = json;
	    });
	};

	$scope.saveIdea = function(){
		//Completo la descripcion
		$scope.editor.instanceById('areaMessage').saveContent();
		$scope.idea.description = $scope.editor.instanceById('areaMessage').getContent();

		//Completo el user logueado
		$scope.idea.author = {id:$USER.getId()};

	    $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas',$scope.idea,function(json) {  
			$location.path("/ideas/"+json.id).search();
			
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

	$scope.init();

}