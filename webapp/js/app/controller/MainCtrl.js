/*globals $,_*/
'use strict';
function MainCtrl($scope, $routeParams, $http, $location, $USER) {

  $scope.searchFilter = '';

  $scope.userList = [];
  $scope.selectedUserId = 1;
  $scope.user = {};

  $scope.menuLogged = 'includes/menu-loggedoff.html';

  var userCookie = getCookie('user');

  if(userCookie != null && userCookie != "") {
    var usr = JSON.parse(userCookie);
    $USER.setUser(usr);
    $scope.user = usr;
    $scope.menuLogged = 'includes/menu-loggedin.html';
  }

  $scope.$on('$viewContentLoaded', function() {
    
    $('.popover-component').popover({
      trigger : 'hover',
      delay: { show: 500, hide: 2000 }
    });

  });
  
  $scope.init = function() {
    $http.get(SERVICE_ENDPOINT+'users').success(function(json) {
      $scope.userList = json;
    });
    $scope.changeUser();//select first
  };

  var getIdeasToken = function(data) {
      
    // Get IdeasToken
    $http.post(SERVICE_ENDPOINT+'auth/', data)
      .success(function(response) {

        // Get User
        $http.get(SERVICE_ENDPOINT+'users/token/' + response.access_token)
          .success(function(user) {

            $scope.user = user;
            $USER.setUser(user);
            $scope.menuLogged = 'includes/menu-loggedin.html';
            setCookie('user', JSON.stringify(user));
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

  $scope.authenticateGoogle = function() {

    var config = {
        client_id: Auth.ids.google,
        scope: [ 
            'https://www.googleapis.com/auth/userinfo.email', 
            'https://www.googleapis.com/auth/userinfo.profile' 
        ]
    };

    gapi.auth.authorize(config, function() {
        getIdeasToken({ provider: 'google', accessToken: gapi.auth.getToken().access_token })
    });
  };

  $scope.authenticateTwitter = function() {
     twttr.anywhere(function (T) {
      if(T.isConnected()) {
        getIdeasToken({ provider: 'twitter', accessToken: twttr.anywhere.token })
      } else {
        T.signIn();
        T.bind("authComplete", function (e, user) {
          getIdeasToken({ provider: 'twitter', accessToken: twttr.anywhere.token })
        });
      }
    });
  };

  $scope.authenticateFacebook = function() {

    FB.getLoginStatus(function(response) {
      if(response.status == 'connected') {
        getIdeasToken({ provider: 'facebook', accessToken: response.authResponse.accessToken })
      } else {
        FB.login(function(response) {
          if(response.authResponse) {
            getIdeasToken({ provider: 'facebook', accessToken: response.authResponse.accessToken });
          }
        });
      }
    });
  };

  $scope.changeUser = function() {
    $http.get(SERVICE_ENDPOINT+'users/'+$scope.selectedUserId).success(function(json) {
      $USER.setUser(json);
    }); 
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