(function () {
    angular.module('SwaggerModule', ['ngPrettyJson'])
        .controller('SwaggerController',
            function ($scope, $http, $location, $log, SwaggerJson) {
                $log.debug("Inside SwaggerController");
                $scope.swaggerJson = {};
                SwaggerJson.get(
                    function (response) {
                        $log.debug("SwaggerJson received data: ", response);
                        //$scope.swaggerJson = library.json.prettyPrint(response);
                        $scope.swaggerJson = response; //JSON.stringify(response, null, 4);
                    },
                    function (err) {
                        $log.error('SwaggerJson:received an error: ', err);
                        if (err.data) {
                            $scope.errorMessages = err.data.message;
                            $log.error("Error: ", err.data.message);
                        } else {
                            $log.error("Unknown Error: ", err);
                        }
                    });
            });
})();