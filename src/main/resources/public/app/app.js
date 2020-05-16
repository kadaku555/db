var app = angular.module('myApp', ["xeditable", "paginator", "ngTagsInput", "ngRoute"]);

app.controller('homeCtrl', function($scope, $http) {

});

app.controller('hCtrl', function($scope, $http, $routeParams) {
    var selected = {};

    //__PUBLIC__//
    $scope.params = $routeParams;
    $scope.list = [];
    $scope.paginator = {
        actionafterinit: function() {
            $scope.paginator.setPageSize(10);
            $scope.paginator.setPredicate("name")
        }
    };

    $scope.load = function() {
        $http.get("/h"+($scope.params.folder?"?folder="+$scope.params.folder:"")).then(
            function(data) {
                $scope.list = data.data;
                $scope.paginator.applyQuery($scope.list);
            },
            function(error) {
                console.log(error);
            }
        );
    };

    $scope.getSelected = function() {
        return selected;
    };

    $scope.setSelected = function(episode) {
        if (selected.name == episode.name) {
            selected = {};
        } else {
            selected = episode;
        }
    };

    $scope.move = function(dest, as) {
        if (selected.name) {
            $http.put("/h/moveto/"+dest+"/as/"+as+"?path="+selected.path).then(
            function() {
                selected = {};
                $scope.load();
            },
            function(error) {
                console.log(error);
            });
        }
    };

    //__RUNTIME__//
    $scope.load();

});

app.controller('mainCtrl', function($scope, $http, base, dossier) {
    var selectedSerie = {};
    var selectedEpisode = {};

    $scope.filter = {filter:"name", value:"", strict: false};
    $scope.series = [];
    $scope.tags = [];
    $scope.dossier = dossier;
    $scope.newTag = "";
    $scope.paginator = {
        actionafterinit: function() {
            $scope.paginator.setPageSize(10);
            $scope.paginator.setPredicate("name")
        }
    };

    $scope.createTag = function() {
        var first = $scope.newTag.substring(0,1);
        var rest = $scope.newTag.substring(1);
        $http.post("/tag?name="+first.toUpperCase()+rest.toLowerCase()).then(
            function(data) {
                $scope.newTag = "";
                $http.get(base+"/tags").then(
                    function(data) {
                        $scope.tags = [];
                        angular.forEach(data.data , function(data) {
                            data.text=data.name;
                            $scope.tags.push(data);
                        });
                    },
                    function(error) {
                        console.Log(error);
                    }
                );
            },
            function(error) {
                console.log(error);
            }
        );
    };

    $scope.load = function() {
        $http.get(base+"/series").then(
            function(data) {
                angular.forEach(data.data , function(data) {
                    if (data.episodes.length == 0) {console.log(data);}
                    angular.forEach(data.tags , function(tag) {
                        tag.text=tag.name;
                    });
                });
                $scope.series = data.data;
                $scope.paginator.applyQuery($scope.series);
            },
            function(error) {
                console.log(error);
            }
        );
        $http.get("/tags").then(
            function(data) {
                $scope.tags = [];
                angular.forEach(data.data , function(data) {
                    data.text=data.name;
                    $scope.tags.push(data);
                });
            },
            function(error) {
                console.Log(error);
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
        return $http.put(base+"/serie/"+serie.id, serie);
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

    $scope.refresh = function() {
        $http.get(base+"/import?path="+$scope.dossier).then(
            function(data) {
                $scope.load();
            },
            function(error) {
                console.log(error);
            }
        )
    };

    $scope.deleteSerie = function(serie) {
        $http.delete(base+"/serie/"+serie.id).then(
             function(data) {
                 $scope.load();
             },
             function(error) {
                 console.log(error);
             }
        )
    };

    $scope.deleteEpisode = function(episode) {
        $http.delete("/episode/"+episode.id).then(
             function(data) {
                 $scope.load();
             },
             function(error) {
                 console.log(error);
             }
        )
    };

    //RUNTIME
    $scope.load();
});

app.directive("ngVideo", function() {
    return {
        restrict: 'E',
        scope: {
          episodeid: '='
        },
        template: "<video controls='controls' preload=auto width=100% src='/episode/{{episodeid}}/video'>"
    }
});

app.directive("ngSpecialVideo", function() {
    return {
        restrict: 'E',
        scope: {
          src: '='
        },
        template: "<video controls='controls' preload=auto width=100% src='/h/video?path={{src}}'>"
    }
});

app.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider
        .when('/', {
            templateUrl: 'main.html',
            controller: 'mainCtrl',
            resolve: {
                base: function(){return "/normal";},
                dossier: function(){return "F:/Anime";}
            }
        })
        .when('/super', {
            templateUrl: 'main.html',
            controller: 'mainCtrl',
            resolve: {
                base: function(){return "/super";},
                dossier: function(){return "F:/perso/Ecchi";}
            }
        })
        .when('/special', {
            templateUrl: 'main.html',
            controller: 'mainCtrl',
            resolve: {
                base: function(){return "/special";},
                dossier: function(){return "F:/portable/samsung/video";}
            }
        })
        .when('/h', {
            templateUrl: 'h.html',
            controller: 'hCtrl'
        });
    }
]);

app.run(['editableOptions', function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs4', 'bs2', 'default'
}]);