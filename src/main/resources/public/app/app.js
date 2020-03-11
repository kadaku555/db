var app = angular.module('myApp', ["xeditable", "paginator", "ngTagsInput"]);

app.controller('myCtrl', function($scope, $http) {
    var selectedSerie = {};
    var selectedEpisode = {};

    $scope.filter = {filter:"name", value:"", strict: false};
    $scope.series = [];
    $scope.tags = [];
    $scope.dossier = "C:/Users/julien.saillant/Downloads/Anime";
    $scope.paginator = {actionafterinit: function(){
        $scope.paginator.setPageSize(10);
        $scope.paginator.setPredicate("name")}
    };

    $scope.load = function() {
        $http.get("/series").then(
            function(data){
                angular.forEach(data.data , function(data){
                    angular.forEach(data.tags , function(tag){
                        tag.text=tag.name;
                    });
                });
                $scope.series = data.data;
                $scope.paginator.applyQuery($scope.series);
            },
            function(error){
            }
        );
        $http.get("/tags").then(
            function(data){
                $scope.tags = [];
                angular.forEach(data.data , function(data){
                    data.text=data.name;
                    $scope.tags.push(data);
                });
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
        if (selectedEpisode.id == episode.id) {
                    selectedEpisode = {};
                } else {
                    selectedEpisode = episode;
                }
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

    $scope.setFilterTag = function(tag) {
        $scope.filter.filter = "tags.name";
        $scope.filter.value = tag.name;
        $scope.applyFilter();
    };

    $scope.applyFilter = function() {
        $scope.paginator.setFilter([$scope.filter]);
    };

    $scope.removeFilter = function() {
        $scope.filter.filter = "name";
        $scope.filter.value = "";
        $scope.applyFilter();
    };

    $scope.refresh = function(){
        $http.get("import?path="+$scope.dossier).then(
        function(data){
            $scope.load();
        },
        function(error){
        }
        )
    };

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

app.run(['editableOptions', function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs4', 'bs2', 'default'
}]);