/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $http) {

  $scope.ideas_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas?order=id';
  // $scope.ideas_endpoint = 'http://localhost:9000/api/ideas?order=id';
  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.search = function(){
    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get($scope.ideas_endpoint).success(function(json) {
      $scope.ideas = json;
    });
  };

  $scope.search();

};

//-------------------------

function IdeaDetailCtrl($scope, $routeParams, $http) {
  $scope.ideaId = $routeParams.ideaId;
  $scope.idea_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas/';
  // $scope.ideas_endpoint = 'http://localhost:9000/api/ideas?order=id';
  $scope.idea = {};
  $scope.comments = [];
  
  $scope.search = function(){

    //IDEA
    $http.get($scope.idea_endpoint+$scope.ideaId).success(function(json) {
      $scope.idea = json;
    });

    //COMMENTS
    $http.get($scope.idea_endpoint+$scope.ideaId+'/comments').success(function(json) {
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
      url:$scope.idea_endpoint+$scope.ideaId+'/comment',
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