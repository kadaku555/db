var app = angular.module('myApp', ["xeditable", "paginator", "ngTagsInput", "ngRoute", "ui.bootstrap"]);

app.controller('homeCtrl', function($scope, $http) {

});

app.controller('flowerCtrl', function($scope, $http, $timeout, $uibModal) {

    //__PRIVATE__//

    var fleurs = [];
    var allowRefresh = true;

    var load = function(){
            $http.get("/tickets").then(
                function(data) {
                    $scope.newTicket = [];
                    $scope.todoTicket = [];
                    $scope.doneTicket = [];
                    $scope.releasedTicket = [];
                    angular.forEach(data.data, function(ticket) {
                        switch(ticket.status) {
                            case "NEW":
                                $scope.newTicket.push(ticket);
                                break;
                            case "TODO":
                                $scope.todoTicket.push(ticket);
                                break;
                            case "DONE":
                                $scope.doneTicket.push(ticket);
                                break;
                            case "RELEASED":
                                $scope.releasedTicket.push(ticket);
                                break;
                        }
                    })
                },
                function(error) {
                    console.log(error);
                }
            );
        };

    //__PUBLIC__//

    $scope.newTicket = [];
    $scope.todoTicket = [];
    $scope.doneTicket = [];
    $scope.releasedTicket = [];

    $scope.nextStatus = function(id) {
        $http.put("/ticket/"+id+"/next").then(
            function(data) {
                load();
            },
            function(error) {
                console.log(error);
            }
        )
    };

    $scope.prevStatus = function(id) {
        $http.put("/ticket/"+id+"/prev").then(
            function(data) {
                load();
            },
            function(error) {
                console.log(error);
            }
        )
    };

    $scope.refresh = function() {
        if (allowRefresh) {
            load();
            $timeout($scope.refresh, 30000);
        }
    }

    $scope.create = function() {
        $scope.open({status: "NEW"}, "Nouvelle commande").result.then(
            function (ticket) {
                $http.post("/ticket", ticket).then(
                    function() {
                        allowRefresh = true;
                        $scope.refresh();
                    },
                    function(error) {
                        console.log(error);
                        alert(error);
                        allowRefresh = true;
                        $scope.refresh();
                    }
                )
            },
            function () {
                console.log('Modal dismissed at: ' + new Date());
            }
        );
    };

    $scope.update = function(id) {
        $http.get("ticket/"+id).then(
            function(data) {
                $scope.open(data.data, "Commande No: "+id).result.then(
                    function (ticket) {
                        $http.put("/ticket/"+id, ticket).then(
                            function() {
                                allowRefresh = true;
                                $scope.refresh();
                            },
                            function(error) {
                                console.log(error);
                                alert(error);
                                allowRefresh = true;
                                $scope.refresh();
                            }
                        )
                    },
                    function () {
                        console.log('Modal dismissed at: ' + new Date());
                    }
                );
            },
            function(error) {
                console.log(error);
                alert(error);
            }
        );
    };

    $scope.delete = function(id) {
        if (confirm("Voulez-vous supprimer le ticket "+id+"?")) {
            $http.delete("ticket/"+id).then(
                function(data) {
                    load();
                },
                function(error) {
                    console.log(error);
                    alert(error);
                }
            );
        }
    };

    $scope.open = function (ticket, title) {
        allowRefresh = false;
        var modalInstance = $uibModal.open({
          templateUrl: 'ticket.html',
          controller: 'ModalInstanceCtrl',
          resolve: {
            ticket: function () {
                return ticket;
            },
            title: function() {
                return title;
            },
            fleurs: function() {
                return fleurs;
            }
          }
        });
        return modalInstance;
    };

    //__RUNTIME__//

    $scope.refresh();

    $http.get("/fleurs").then(
        function(data) {
            fleurs = data.data;
        },
        function(error) {
            console.log(error);
            alert(error);
        }
    );
});

app.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, ticket, title, fleurs) {
    $scope.ticket = ticket;
    $scope.title = title;
    $scope.fleurs = fleurs;

    $scope.addDetail = function() {
        $scope.ticket.details.push({quantite: 0});
    };

    $scope.ok = function () {
        $uibModalInstance.close($scope.ticket);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});

app.controller('specialCtrl', function($scope, $http) {
    var selected = {};

    //__PUBLIC__//
    $scope.list = [];
    $scope.paginator = {
        actionafterinit: function() {
            $scope.paginator.setPageSize(10);
            $scope.paginator.setPredicate("date")
        }
    };

    $scope.load = function() {
        $http.get("/h").then(
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

    //__RUNTIME__//
    $scope.load();

});

app.controller('mainCtrl', function($scope, $http) {
    var selectedSerie = {};
    var selectedEpisode = {};

    $scope.filter = {filter:"name", value:"", strict: false};
    $scope.series = [];
    $scope.tags = [];
    $scope.dossier = "C:/Users/julien.saillant/Downloads/Anime";
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
            },
            function(error) {
                console.log(error);
            }
        );
    };

    $scope.load = function() {
        $http.get("/series").then(
            function(data) {
                angular.forEach(data.data , function(data) {
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

    $scope.refresh = function() {
        $http.get("import?path="+$scope.dossier).then(
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
           controller: 'mainCtrl'
         })
         .when('/special', {
           templateUrl: 'special.html',
           controller: 'specialCtrl'
         })
         .when('/flower', {
           templateUrl: 'flower.html',
           controller: 'flowerCtrl'
         });

   }])

app.run(['editableOptions', function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs4', 'bs2', 'default'
}]);