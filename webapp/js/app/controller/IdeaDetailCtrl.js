/*globals $,_*/
'use strict';
function IdeaDetailCtrl($scope, $routeParams, $http, $USER) {
  $scope.ideaId = $routeParams.ideaId;

  $scope.idea = {};
  
  $scope.newComment = {};

  $scope.comments = [];
  
  $scope.$on('$viewContentLoaded', function() {
      $scope.editor = new nicEditor({
        buttonList : ['bold','italic','underline','strikeThrough','ol','ul','forecolor','link','unlink'],
        iconsPath : '/img/nicEditorIcons.gif'
      })

      $scope.editorInstance = $scope.editor.panelInstance('commentText');
  });

  $scope.init = function(){

    //IDEA
    $http.get(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId).success(function(json) {
      $scope.idea = json;
    });

    //COMMENTS
    $http.get(SERVICE_ENDPOINT+'comments?q=idea.id:'+$scope.ideaId).success(function(json) {
      $scope.comments = json;
    });

  };

  $scope.addComment = function(){
    $scope.editor.instanceById('commentText').saveContent();

    var $commentBox = $('#comment-box'),
      data = {
        "author": {id: $USER.getId()},
        "idea": {id: $scope.ideaId},
        "comment": $scope.editor.instanceById('commentText').getContent()
      };

    $http.post(SERVICE_ENDPOINT+$scope.ideaId+'/comment',data).success(function(json) {
        $commentBox.find('textarea').val('');
        $scope.comments.push(json);
      }).error(function(data, status, headers, config) {
        alert('ERROR AL DAR DE ALTA EL COMENTARIO');
      });

  };

  $scope.init();

};