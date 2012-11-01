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

  $scope.$watch("idea.created",function(value){
    console.log(value);
  });

  $scope.init = function(){

    //IDEA
    $http.get(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId).success(function(json) {
      $scope.idea = json;

      //Tags
      $http.get(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId + '/tags').success(function(jsonResult) {
        $scope.idea.tags = jsonResult;
      });
      
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
        "comment": $scope.editor.instanceById('commentText').getContent()
      };

    $http.post(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId+'/comment',data).success(function(json) {
        $scope.editor.instanceById('commentText').setContent('');
        $scope.comments.push(json);
        $scope.commentedMark = true;
        console.log(json);
      }).error(function(data, status, headers, config) {
        alert('ERROR AL DAR DE ALTA EL COMENTARIO');
      });

  };

  $scope.init();

};