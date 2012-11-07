/*globals $,_*/
'use strict';
function IdeaDetailCtrl($scope, $routeParams, $http, $USER) {
  $scope.ideaId = $routeParams.ideaId;

  $scope.idea = {};
  
  $scope.idea.tags = [];
  
  $scope.newComment = {};

  $scope.comments = [];
  
  $scope.commentedMark = false;

  $scope.$on('$viewContentLoaded', function() {
      $scope.editor = new nicEditor({
        buttonList : ['bold','italic','underline','strikeThrough','ol','ul','forecolor','link','unlink'],
        iconsPath : '/img/nicEditorIcons.gif'
      })

      $scope.editorInstance = $scope.editor.panelInstance('commentText');
  });

  $scope.init = function(){

    //IDEA
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId,{},function(json) {  
      $scope.idea = json;

    });

    //COMMENTS
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'comments?q=idea.id:'+$scope.ideaId,{},function(json) {  
      $scope.comments = json;
    });
  
    //TAGS
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId + '/tags',{},function(json) {       
      $scope.idea.tags = json;
    });    

  };

  $scope.addComment = function(){
    $scope.editor.instanceById('commentText').saveContent();

    var $commentBox = $('#comment-box'),
      data = {
        "comment": $scope.editor.instanceById('commentText').getContent()
      };

    $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId+'/comment',data,function(json) {  
        $scope.editor.instanceById('commentText').setContent('');
        $scope.comments.push(json);
        $scope.commentedMark = true;
      },function(data, status, headers, config) {
          alert('Ocurrió un error al querer dar de alta el comentario. Por favor intenta nuevamente.');
      });

  };

  $scope.init();

};