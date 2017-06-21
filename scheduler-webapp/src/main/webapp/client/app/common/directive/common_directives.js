(function () {
    angular.module('CommonModule')
        .directive('scrollToTag', function ($location, $log) {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    element.bind('click', function (event) {
                        var tagId = attrs.scrollToTag;
                        $log.debug("Jump to tag id[" + tagId + "]");
                        var el = document.getElementById(tagId);
                        setTimeout(function () {
                            window.scrollTo(0, el.offsetTop - 150);
                        }, 20);
                    });
                }
            }
        });
})();