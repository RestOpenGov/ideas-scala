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

            $('#authModal').modal('hide');
            
            $scope.user = user;
            $USER.setUser(user);
            $scope.menuLogged = 'includes/menu-loggedin.html';
            
            SocialAuth.setUser(user);
            SocialAuth.set('ideas-ba-token', response.token);

            window.location.reload(); // FIX THIS

          })
          .error(function(json) {
            console.log(json);
            $('#authModal').modal('hide');
          });
      })
      .error(function(json) {
        console.log(json);
        $('#authModal').modal('hide');
        $('#errorModal .modal-body').html("Ocurrió un error al iniciar sesión. Por favor intenta nuevamente.");
        $('#errorModal').modal();
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

    if($('#login').length > 0) {
      SocialAuth.init();
    }

    $('.authButton').click(function() {
      SocialAuth.authenticate($(this).attr('rel'));
      $(this).html('Cargando...');
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