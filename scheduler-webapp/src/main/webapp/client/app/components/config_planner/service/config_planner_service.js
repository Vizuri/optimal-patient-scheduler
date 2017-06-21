(function () {
    angular.module('ConfigModule')
        .factory('PlannerService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/update/config', {}, {
                'update': {
                    method: 'POST',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PlannerScheduleService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/schedule/generate', {}, {
                'calculate': {
                    method: 'GET',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                    ,params: {max: '@max'}  // use this to pass query paramenters
                }
            });
        })
        .factory('PlannerBestSolutionService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/schedule/best/solution', {}, {
                'poll': {
                    method: 'GET',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PlannerBestSolutionStopService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/schedule/generate/terminate', {}, {
                'terminate': {
                    method: 'GET',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PlannerAcceptAllService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/schedule/generate/acceptall', {}, {
                'accept': {
                    method: 'GET',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        });


})();