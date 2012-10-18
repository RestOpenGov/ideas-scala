/*globals $,_*/
'use strict';
function VoteCtrl($scope, $routeParams, $http) {

    $scope.init = function(){
      //do nothing
    }
  
    $scope.votePositive = function(){

      // Call Rest Post method
      $scope.vote('up');

    };

    $scope.voteNegative = function(){

      // Call Rest Post method
      $scope.vote('down');

    };


    $scope.isIdea= function(){
      // if the entity selected was and a idea, there is no comment set.
      return ($scope.comment === undefined) 
    };
    
    // Vote main function
    $scope.vote = function(action){
      var url,type;

      if ($scope.isIdea()){
        type = 'idea';
      }else{
        type = 'comment';
      }

      switch(type){
        case 'comment':
          url = $scope.idea.id + '/comments/'+ $scope.comment.id + '/' + action;
        break;
        case 'idea':
          url = $scope.idea.id + '/' + action;
        break;
      };

      if(url){
        $http.put(SERVICE_ENDPOINT + 'ideas/'+ url,undefined)
        .success(function(json) {
          switch(type){
            case 'comment':
             $scope.comment.votes = json.votes;
            break;
            case 'idea':
              $scope.idea.votes = json.votes;
            break;
          };
            
        }).error(function(data, status, headers, config) {
            var error = 'ERROR!: ';
            angular.forEach(data, function(e, i){
              error += ' - ' + e.message;
            });
            alert(error);
        });
      }

    };

    $scope.init();
    
};
