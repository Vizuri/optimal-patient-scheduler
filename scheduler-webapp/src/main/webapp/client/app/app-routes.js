(function () {
    // Define any routes for the app
    // Note that this app is a single page app, and each partial is routed to using the URL fragment.
    angular.module('vizuri-schedule-planner', ['ngRoute', 'ngMaterial', 'ui.calendar', 'ui.bootstrap', 'ui.mask', 'rzModule', 'emguo.poller', 'CommonModule', 'HeaderModule', 'ConfigModule', 'ClinicDetailModule', 'PhysicianModule', 'PatientModule', 'SwaggerModule'])
        .config(['$httpProvider', '$routeProvider', function ($httpProvider, $routeProvider) {
            $routeProvider.
            when('/config', {
                    templateUrl: 'client/app/components/config_planner/view/config_planner_view.html',
                    controller: 'ConfigController'
                })
                .when('/clinic', {
                    templateUrl: 'client/app/components/clinic_detail/view/clinic_detail_view.html',
                    controller: 'ClinicController'
                })
                .when('/physician', {
                    templateUrl: 'client/app/components/physician_detail/view/physician_detail_view.html',
                    controller: 'PhysicianController'
                })
                .when('/patient', {
                    templateUrl: 'client/app/components/patient_detail/view/patient_detail_view.html',
                    controller: 'PatientController'
                })
                .when('/swagger', {
                    templateUrl: 'client/app/components/swagger/view/swagger_view.html',
                    controller: 'SwaggerController'
                })
                .otherwise({
                    redirectTo: '/config'
                });
        }])
        .config(['$logProvider', function ($logProvider) {
            var pageUrl = window.location.href || 'unknown';
            if (pageUrl.indexOf('localhost') != -1) {
                console.log("Enabled debug logging");
            } else {
                console.log("Disabled debug logging");
                // It is set for $log.debug
                $logProvider.debugEnabled(false); // default is true
            }
        }]);
})();