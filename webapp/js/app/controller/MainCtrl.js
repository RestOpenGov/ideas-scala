/*globals $,_*/
'use strict';
function MainCtrl($scope, $routeParams, $http, $location, $USER) {

  $scope.searchFilter = '';
  $scope.selectedUserId = 1;
  $scope.user = SocialAuth.getUser();
  $scope.menuLogged = 'includes/menu-loggedoff.html';

  $scope.types = {
    '/ideas': 1,
    '/reclamos': 2,
    '/preguntas': 3,
    '/sugerencias': 4
  };

  $scope.typeId = 0;



  $('.authButton').click(function() {
    SocialAuth.authenticate($(this).attr('rel'));
    $(this).html('Cargando...');
  });

  SocialAuth.onAuthentication = function(data) {
      
    // Get IdeasToken
    $http({ 
      method: 'POST', 
      headers: { 'Content-Type': 'application/json'}, 
      url: SERVICE_ENDPOINT+'auth/', 
      data: JSON.stringify(data)}
    )
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

  if($('#login').length > 0) {
    SocialAuth.init();
  }

  $scope.$on('$viewContentLoaded', function() {

    var types = {
      '/ideas': 1,
      '/reclamos': 2,
      '/preguntas': 3,
      '/sugerencias': 4
    };

    var typeId = 0;

    if(typeof types[$location.path()] != 'undefined') {
      typeId = types[$location.path()];
    }

    $('.nav li').removeClass('active');
    $('#link_' + typeId).addClass('active');
    
    $('.popover-component').popover({
      trigger : 'hover',
      delay: { show: 500, hide: 2000 }
    });

  });
  
  $scope.init = function() {

  };

  $scope.logout = function() {
    SocialAuth.clearUser();
    SocialAuth.set('ideas-ba-token', '');
    window.location.reload();
  };

  $scope.search = function(){
    
    
    if(typeof $scope.types[$location.path()] != 'undefined') {
      
      var Typelocation = $location.path();
      if($scope.searchQuery){
        $location.path(Typelocation).search({"filter": $scope.searchQuery});
      }else{
        $location.path(Typelocation).search();
      }
    }
    else
    {
      // search on home()
        if($scope.searchQuery){
        $location.path('/home').search({"filter": $scope.searchQuery});
      }else{
        $location.path('/home').search();
      }
    }




    $scope.searchQuery = '';
  };

  $scope.init();
  
};