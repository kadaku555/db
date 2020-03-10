var app = angular.module('myApp', ["xeditable"]);

app.controller('myCtrl', function($scope, $http) {
    var selectedSerie = {};
    var selectedEpisode = null;

    $scope.series = [];
    $scope.tags = [];

    $scope.load = function() {
        $http.get("/series").then(
            function(data){
                $scope.series = data.data;
            },
            function(error){
            }
        );
        $http.get("/tags").then(
            function(data){
                $scope.tags = data.data;
            },
            function(error){
            }
        );
    };

    $scope.getSelectedSerie = function() {
        return selectedSerie;
    };

    $scope.getSelectedEpisode = function() {
        return selectedEpisode;
    };

    $scope.setSelectedSerie = function(serie) {
        if (selectedSerie.id == serie.id) {
            selectedSerie = {};
        } else {
            selectedSerie = serie;
        }
    };

    $scope.setSelectedEpisode = function(episode) {
        selectedEpisode = episode;
    };

    $scope.toggleViewedSerie = function(serie) {
        serie.viewed = !serie.viewed;
        $http.put("/serie/"+serie.id, serie).then(
            function(data) {
                $scope.load();
            },
            function(error) {
                console.log(error);
            }
        )
    };

    $scope.toggleViewedEpisode = function(episode) {
        episode.viewed = !episode.viewed;
        $http.put("/episode/"+episode.id, episode).then(
            function(data) {
                $scope.load();
            },
            function(error) {
                console.log(error);
            }
        )
    };

    $scope.updateSerie = function(serie) {
        return $http.put('/serie/'+serie.id, serie);
    };

    $scope.showTags = function(serie) {
        var selected = [];
        angular.forEach(serie.tags, function(t) {
            selected.push(t.name);
        });
        return selected.length ? selected.join(', ') : 'Not set';
    }

    //RUNTIME
    $scope.load();
});

app.directive("ngVideo", function(){
    return {
        restrict: 'E',
        scope: {
          episodeid: '='
        },
        template: "<video controls='controls' preload=auto width=100% src='/episode/{{episodeid}}/video'>"
    }
});

app.filter("trustUrl", function($sce) {
            return function(Url) {
                console.log(Url);
                return $sce.trustAsResourceUrl(Url);
            };
        });

app.run(['editableOptions', function(editableOptions) {
    editableOptions.theme = 'bs4'; // bootstrap3 theme. Can be also 'bs4', 'bs2', 'default'
}]);