(function () {
    angular.module('PhysicianModule')
        .factory('PhysicianService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/physician/all', {}, {
                'get': {
                    method: 'GET',
                    isArray: true,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PhysicianAppointmentService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/find/physician/appointments/:name', {name: '@name'}, {
                'get': {
                    method: 'POST',
                    isArray: true,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        });
})();