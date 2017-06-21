(function () {
    angular.module('SwaggerModule')
        // this is a very compact way of making JSON pretty for display, there is also the angular directive ng-prettyjson that can be used
        .directive('swaggerPrettyJson', function ($log) {
            function replacer(match, pIndent, pKey, pVal, pEnd) {
                var key = '<span class=json-key>';
                var val = '<span class=json-value>';
                var str = '<span class=json-string>';
                var r = pIndent || '';
                if (pKey)
                    r = r + key + pKey.replace(/[": ]/g, '') + '</span>: ';
                if (pVal)
                    r = r + (pVal[0] == '"' ? str : val) + pVal + '</span>';
                return r + (pEnd || '');
            }

            function prettyPrint(obj) {
                $log.debug("Inside prettyPrint");
                var jsonLine = /^( *)("[\w]+": )?("[^"]*"|[\w.+-]*)?([,[{])?$/mg;
                return JSON.stringify(obj, null, 3)
                    .replace(/&/g, '&amp;').replace(/\\"/g, '&quot;')
                    .replace(/</g, '&lt;').replace(/>/g, '&gt;')
                    .replace(jsonLine, replacer);
            }
            return {
                restrict: 'A',
                scope: {
                    swaggerPrettyJson: '='
                },
                template: function ($element, $attributes) {
                    $log.debug(" Directive swaggerPrettyJson:template");
                    return "<div></div>";
                },
                link: function ($scope, $element, $attributes) {
                    $log.debug("Directive swaggerPrettyJson:link method, $scope.swaggerPrettyJson: ", $scope.swaggerPrettyJson);
                    $scope.$watch('swaggerPrettyJson', function (val) {
                        //$log.debug("val: ", val);
                        $log.debug("updated $scope.swaggerPrettyJson: ", $scope.swaggerPrettyJson);
                        if ($scope.swaggerPrettyJson != undefined) {
                            var prettyJson = prettyPrint($scope.swaggerPrettyJson);
                            //$log.debug("Updated prettyJson: ", prettyJson);
                            $element.html(prettyJson);
                            //$compile($element.contents() )($scope);
                        }
                    });
                }
            };
        })
})();