(function () {
    var module = angular.module('SwaggerModule')
        .factory('SwaggerJson', function ($resource) {
            return $resource('/scheduler-webapp/rest/swagger.json', {}, {
                'get': {
                    method: 'GET',
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        });
})();