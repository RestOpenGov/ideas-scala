/*globals $,_*/
'use strict';
function IdeaListCtrl($scope, $http) {

  $scope.ideas_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas?order=id';
  $scope.ideas = {};

  $scope.$on('$viewContentLoaded', function() {
    //console.log('we are ready...');
  });

  $scope.search = function(){
    // Just in case the web service doesn't support X-Requested-With header
    // delete $http.defaults.headers.common['X-Requested-With']
    $http.get($scope.ideas_endpoint).success(function(json) {
      $scope.ideas = json;

      // generate random votes -- mock
      angular.forEach($scope.ideas, function(value, key) {
        value.votes = votes();
      })
    });
  };

  $scope.search();

};

//-------------------------

function IdeaDetailCtrl($scope, $routeParams, $http) {
  $scope.ideaId = $routeParams.ideaId;
  $scope.idea_endpoint = 'http://ideas-jugar.rhcloud.com/api/ideas/';
  $scope.idea = {};
  $scope.comments = [];
  
  $scope.search = function(){

    //IDEA
    $http.get($scope.idea_endpoint+$scope.ideaId).success(function(json) {
      $scope.idea = json;
      $scope.idea.votes = votes();
      });

    //COMMENTS
    $http.get($scope.idea_endpoint+$scope.ideaId+'/comments').success(function(json) {
      $scope.comments = json;
      });

  };

  $scope.addComment = function(){
    $commentBox = $('#comment-box').slideUp();
    $commentBox.slideUp();
    var data = {
        "author": 1,
        "comment": $scope.msg
    };

    $http({
      url:$scope.idea_endpoint+$scope.ideaId+'/comment',
      data : data,
      method : 'POST',
      headers : {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'},
    }).success(function(){
      $commentBox.find('textarea').val('');
      $commentBox.slideDown();
    });

  };

  $scope.search();

};


function votes() {
  return {
    pos: random(100),
    neg: random(20)
  }
};

function random (top) {
  top = top || 100;
  return Math.floor(Math.random() * top+1);
};