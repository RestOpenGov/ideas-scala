/*globals $,_*/
'use strict';
function IdeaDetailCtrl($scope, $routeParams, $http, $USER) {
  $scope.ideaId = $routeParams.ideaId;

  $scope.idea = {};
  
  $scope.idea.tags = [];
  
  $scope.errorTextArea = false;

  $scope.comments = [];
  
  $scope.commentedMark = false;

  $scope.$on('$viewContentLoaded', function() {
    $('#commentText').wysihtml5({
        stylesheets: ["/css/bootstrap-wysihtml5-0.0.2.css"]
      });
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

    //GEOS
    $scope.ideaAjaxCall('GET',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId + '/geo',{},function(json) {       
      $scope.idea.geos = json;
    });  

  };

  $scope.addComment = function(){
    var data = {
      "comment": $('#commentText').val()
    }; 
    if ($scope.isValidForm()){
      $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId+'/comment',data,function(json) {  
        $('#commentText').val('');
        $scope.comments.push(json);
        $scope.commentedMark = true;
      },function(data, status, headers, config) {
          alert('Ocurrió un error al querer dar de alta el comentario. Por favor intenta nuevamente.');
      });
    }
  };

  $scope.isValidForm = function(){
    var c = $('#commentText').val();
    if (c=='') {
      $scope.errorTextArea = true;
    } else {
      $scope.errorTextArea = false;
    }
    return ($scope.errorTextArea == false);
  }
  
  $scope.init();

};