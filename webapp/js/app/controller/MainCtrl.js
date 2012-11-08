/*globals $,_*/
'use strict';
function MainCtrl($scope, $routeParams, $http, $location, $USER) {

  $scope.searchFilter = '';
  $scope.selectedUserId = 1;
  $scope.user = SocialAuth.getUser();
  $scope.menuLogged = 'includes/menu-loggedoff.html';

  SocialAuth.onAuthentication = function(data) {
      
    // Get IdeasToken
    $.post(SERVICE_ENDPOINT+'auth/', data)
      .success(function(response) {

        // Get User
        $http.get(SERVICE_ENDPOINT+'users/token/' + response.token)
          .success(function(user) {
            
            $scope.user = user;
            $USER.setUser(user);
            $scope.menuLogged = 'includes/menu-loggedin.html';
            
            SocialAuth.setUser(user);
            SocialAuth.set('ideas-ba-token', response.token);

            window.location.reload(); // FIX THIS

          })
          .error(function(json) {
            console.log(json);
          });
      })
      .error(function(json) {
        console.log(json);
      }); 
  };

  var user = SocialAuth.getUser();

  if(user) {
    $USER.setUser(user);
    $scope.user = user;
    $scope.menuLogged = 'includes/menu-loggedin.html';
  }

  $scope.$on('$viewContentLoaded', function() {
    
    $('.popover-component').popover({
      trigger : 'hover',
      delay: { show: 500, hide: 2000 }
    });

  });
  
  $scope.init = function() {

  };

  $scope.logout = function() {
    SocialAuth.clearUser();
    window.location.reload();
  };

  $scope.search = function(){
    if($scope.searchQuery){
      $location.path("/ideas/lista").search({"filter": $scope.searchQuery});
    }else{
      $location.path("/ideas/lista").search();
    }
    $scope.searchQuery = '';
  };

  $scope.init();
  
};