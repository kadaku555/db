var app = angular.module('myApp', ["xeditable", "paginator", "ngTagsInput", "ngRoute", "ui.bootstrap"]);

app.controller('homeCtrl', function($scope, $http) {
    $scope.title;

    $scope.setTitle = function(title) {
        $scope.title = title;
    };
});

app.controller('flowerCtrl', function($scope, $http, $timeout, $uibModal) {

    //__PRIVATE__//


    var allowRefresh = true;

    var load = function(){
            $http.get("/tickets").then(
                function(data) {
                    var stock = {};
                    $scope.newTicket = [];
                    $scope.todoTicket = [];
                    $scope.doneTicket = [];
                    $scope.bagTicket = [];
                    $scope.releasedTicket = [];
                    angular.forEach(data.data, function(ticket) {
                        switch(ticket.status) {
                            case "NEW":
                                $scope.newTicket.push(ticket);
                                angular.forEach(ticket.details, function(d) {
                                    stock[d.fleur] = (stock[d.fleur]?stock[d.fleur]:0) + d.quantite;
                                });
                                break;
                            case "TODO":
                                $scope.todoTicket.push(ticket);
                                angular.forEach(ticket.details, function(d) {
                                    stock[d.fleur] = (stock[d.fleur]?stock[d.fleur]:0) + d.quantite;
                                });
                                break;
                            case "DONE":
                                $scope.doneTicket.push(ticket);
                                angular.forEach(ticket.details, function(d) {
                                    stock[d.fleur] = (stock[d.fleur]?stock[d.fleur]:0) + d.quantite;
                                });
                                break;
                            case "BAG":
                                $scope.bagTicket.push(ticket);
                                angular.forEach(ticket.details, function(d) {
                                    stock[d.fleur] = (stock[d.fleur]?stock[d.fleur]:0) + d.quantite;
                                });
                                break;
                            case "RELEASED":
                                $scope.releasedTicket.push(ticket);
                                break;
                        }
                    });
                    $http.get("/fleurs").then(
                        function(data) {
                            $scope.fleurs = data.data;
                            angular.forEach($scope.fleurs, function(fleur) {
                                fleur.stock = fleur.stock - (stock[fleur.id+""]?stock[fleur.id+""]:0);
                            });
                        },
                        function(error) {
                            console.log(error);
                            alert(error);
                        }
                    );
                },
                function(error) {
                    console.log(error);
                }
            );

        };

    //__PUBLIC__//

    $scope.fleurs = [];

    $scope.newTicket = [];
    $scope.todoTicket = [];
    $scope.doneTicket = [];
    $scope.bagTicket = [];
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
        }
        $timeout($scope.refresh, 10000);
    }

    $scope.create = function() {
        $scope.open({status: "NEW", details:[]}, "Nouvelle commande").result.then(
            function (ticket) {
                $http.post("/ticket", ticket).then(
                    function() {
                        allowRefresh = true;
                        load();
                    },
                    function(error) {
                        console.log(error);
                        alert(error);
                        allowRefresh = true;
                        load();
                    }
                )
            },
            function () {
                console.log('Modal dismissed at: ' + new Date());
                allowRefresh = true;
                load();
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
                                load();
                            },
                            function(error) {
                                console.log(error);
                                alert(error);
                                allowRefresh = true;
                                load();
                            }
                        )
                    },
                    function () {
                        console.log('Modal dismissed at: ' + new Date());
                        allowRefresh = true;
                        load();
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
                return $scope.fleurs;
            }
          }
        });
        return modalInstance;
    };

    //__RUNTIME__//

    $scope.setTitle("Projet Jardiland!");
    $scope.refresh();

});

app.controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, ticket, title, fleurs) {

    var prices = {};

    $scope.ticket = ticket;
    $scope.title = title;
    $scope.fleurs = fleurs;

    $scope.getTotalBell = function() {
        var prix = 0;
        angular.forEach($scope.ticket.details, function(detail) {
            prix = prix + (detail.quantite * prices[detail.fleur].bell);
        });
        return prix;
    };

    $scope.getTotalTnm = function() {
        var prix = 0;
        angular.forEach($scope.ticket.details, function(detail) {
            prix = prix + (detail.quantite * prices[detail.fleur].tnm);
        });
        return prix;
    };

    $scope.addDetail = function() {
        $scope.ticket.details.push({quantite: 0});
    };

    $scope.ok = function () {
        $uibModalInstance.close($scope.ticket);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    angular.forEach($scope.fleurs, function(fleur) {
        prices[fleur.id] = {bell: fleur.clochette, tnm: fleur.tmn};
    });
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
    $scope.setTitle("Liste")
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
    $scope.setTitle("Liste des Animes");
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
        })
        .when('/flower', {
            templateUrl: 'flower.html',
            controller: 'flowerCtrl'
       });
    }
]);

app.run(['editableOptions', function(editableOptions) {
    editableOptions.theme = 'bs3'; // bootstrap3 theme. Can be also 'bs4', 'bs2', 'default'
}]);