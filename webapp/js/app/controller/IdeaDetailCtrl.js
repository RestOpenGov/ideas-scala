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
    console.log('hola');
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

  };

  $scope.addComment = function(){
    console.log($scope.area);
    var data = {
        "comment": $('#commentText').val()
      };

    $scope.ideaAjaxCall('POST',SERVICE_ENDPOINT+'ideas/'+$scope.ideaId+'/comment',data,function(json) {  
        $scope.area.val('');
        $scope.comments.push(json);
        $scope.commentedMark = true;
      },function(data, status, headers, config) {
          alert('ERROR AL DAR DE ALTA EL COMMENT');
      });

  };

  $scope.init();

};