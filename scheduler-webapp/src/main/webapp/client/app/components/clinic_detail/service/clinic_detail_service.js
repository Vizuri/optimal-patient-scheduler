
angular.module('ClinicDetailModule')
    .factory('ClinicService', function ($resource) {
        return $resource('/scheduler-webapp/rest/planner/clinics/all', {}, {
            'get': {
                method: 'GET',
                isArray: true,
                headers: {
                    Accept: 'application/json'
                }
            }
        });
    })
    .factory('ClinicPollService', function ($resource) {
        return $resource('/scheduler-webapp/rest/planner/find/clinic/:name', {name: '@name'}, {
            'poll': {
                method: 'POST',
                isArray: false,
                headers: {
                    Accept: 'application/json'
                }
            }
        });
    });
