
var MapHelper = {

  mapIdeas: function(json, map, info) {

    angular.forEach(json, function(element, index) {

      var imgs = ['idea', 'idea', 'reclamo', 'pregunta', 'sugerencia'];
      ideaType = element.idea.type.id;
      if (!ideaType || ideaType > imgs.length) ideaType = 0;
      var image = '/img/markers/' + imgs[ideaType] + '.png'; // default.

      var latLng = new google.maps.LatLng(element.lat, element.lng);
      var marker = new google.maps.Marker({
        position: latLng,
        map: map,
        icon: image,
        title: element.name
      });

      google.maps.event.addListener(marker, 'click', function() {
        var nameIdea = element.idea.name;
        var ubication = element.name;
        var link = HOST + '/#/ideas/' + element.idea.id;

        var message = "";
        message += '<div id="content">';
        message += '<a href="' + link +'"><h5>' + nameIdea + '</h5></a>';
        message += '<p> <b>Ubicaci&oacute;n:</b> ' + ubication + '</p>' ;
        message += '</div>';
        info.setContent(message);
        info.open(map, marker);
      });

    });
  }

};
