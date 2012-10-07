/*globals $,_*/
'use strict';
function IdeaDetailCtrl($scope, $routeParams, $http) {
  $scope.ideaId = $routeParams.ideaId;

  $scope.idea = {};
  $scope.comments = [];
  
  $scope.search = function(){

    //IDEA
    $http.get(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId).success(function(json) {
      $scope.idea = json;
    });

    //COMMENTS
    $http.get(SERVICE_ENDPOINT+'ideas/'+$scope.ideaId+'/comments').success(function(json) {
      $scope.comments = json;
    });

  };

  $scope.addComment = function(){
    var $commentBox = $('#comment-box').slideUp(),data = {
        "author": 1,
        "comment": $scope.msg
    };

    $commentBox.slideUp();

    $http({
      url:SERVICE_ENDPOINT+$scope.ideaId+'/comment',
      data : data,
      method : 'POST',
      headers : {'Content-Type':'application/json; charset=UTF-8'},
    }).success(function(json){
      $commentBox.find('textarea').val('');
      $scope.comments.push(json);
      $commentBox.slideDown();
    });

  };

  $scope.search();

};