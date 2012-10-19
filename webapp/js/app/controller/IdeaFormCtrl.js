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

		$http.post(SERVICE_ENDPOINT+'ideas',$scope.idea).success(function(json) {
			$location.path("/ideas/"+json.id).search();
	    }).error(function(data, status, headers, config) {
	    	alert('ERROR AL DAR DE ALTA LA IDEA');
	    });
	};

	$scope.init();

}