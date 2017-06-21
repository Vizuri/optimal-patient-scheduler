(function () {
    angular.module('HeaderModule', ['ngMaterial'])
        .directive('initHeader', function ($log, $location, $mdMenu) {
            return {
                restrict: 'A',
                templateUrl: 'client/app/components/header/view/header_view.html',
                controller: ['$scope', '$location', function ($scope, $location) {
                    $log.debug("Directive initHeader:controller method");

                    $scope.menuOptions = [{name: "Configure Planner", page: "config"},
                                          {name: "Clinic Schedule", page: "clinic"},
                                          {name: "Patient Schedule", page: "patient"},
                                          {name: "Physician Planner", page: "physician"},
                                          {name: "Swagger", page: "swagger"}
                                         ];

                    $scope.goTo = function (pageView) {
                        //$log.debug("Inside goTo, pageView[" + pageView + "]");
                        $location.url("/" + pageView);
                    };
                    $scope.showConfigurationSettings = function () {
                        $log.debug("Inside showConfigurationSettings");
                    };

                    $scope.closeMenu = function () {
                        $log.debug("Inside closeMenu", $mdMenu);

                        //$mdMenu.hide();
                    };

                    $scope.showMenu = function ($mdOpenMenu, event) {
                        $log.debug("Inside showMenu");

                        //$mdOpenMenu($event)
                        $mdOpenMenu(event);
                        //$mdMenu.open(event);
                        //$mdMenu.show();
                    }

                }],
                link: function ($scope, $element, $attributes) {
                    //$log.debug("Directive initHeader:link method, $attributes: ", $attributes);
                    $scope.pageView = $attributes.initHeader;
                    $scope.headerLabel = $attributes.headerLabel;
                }
            };
        })
})();