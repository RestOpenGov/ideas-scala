/*globals $,_*/
'use strict';
function VoteCtrl($scope, $routeParams, $http) {

    $scope.init = function(){
      
    }
  
    $scope.votePositive = function(){

      // Call Rest Post method
      if ($scope.isIdea()){

        // PUT     /api/ideas/:id/up                  controllers.Ideas.up(id: Long)
      
        $http.put(SERVICE_ENDPOINT + 'ideas/'+ $scope.idea.id + '/up',undefined)
        .success(function(json) {
          
            $scope.idea.votes.pos += 1;
            // refresh entity
            // $http.get(SERVICE_ENDPOINT+'ideas/'+ $scope.idea.id ).success(function(json) {
            //   $scope.idea = json;
            // });
                
            
         }).error(function(data, status, headers, config) {
            throw new Error("Error al votar una idea.");
        });

      }else
      {
        // PUT     /api/ideas/:idea/comments/:id/up       controllers.Comments.up(idea: Long, id: Long)
        
        $http.put(SERVICE_ENDPOINT + 'ideas/'+ $scope.idea.id+ '/comments/'+ $scope.comment.id+ '/up',undefined)
        .success(function(json) {

            $scope.comment.votes.pos += 1;
            
        }).error(function(data, status, headers, config) {
            throw new Error("Error al votar un comentario.");
        });
      }
      
    };

    $scope.voteNegative = function(){

      // Call Rest Post method
      if ($scope.isIdea()){
        
        // PUT     /api/ideas/:id/down                controllers.Ideas.down(id: Long)
        
        $http.put(SERVICE_ENDPOINT + 'ideas/'+ $scope.idea.id+ '/down',undefined)
        .success(function(json) {

            $scope.idea.votes.neg += 1;
            // refresh entity
            // $http.get(SERVICE_ENDPOINT+'ideas/'+ $scope.idea.id ).success(function(json) {
            //   $scope.idea = json;
            // });
            
        }).error(function(data, status, headers, config) {
            throw new Error("Error al votar una idea.");
        });

      }else
      {
        
        // PUT     /api/ideas/:idea/comments/:id/down     controllers.Comments.down(idea: Long, id: Long)
        $http.put(SERVICE_ENDPOINT + 'ideas/'+ $scope.idea.id+ '/comments/'+ $scope.comment.id + '/down',undefined)
        .success(function(json) {
            
          $scope.comment.votes.neg += 1;
            
        }).error(function(data, status, headers, config) {
            throw new Error("Error al votar un comentario.");
        });


      }
    };


    $scope.isIdea= function(){
      // if the entity selected was and a idea, there is no comment set.
      return ($scope.comment === undefined) 
    };
    
    $scope.init();
    
};
