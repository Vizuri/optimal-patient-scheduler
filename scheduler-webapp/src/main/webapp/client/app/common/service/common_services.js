(function () {
    var module = angular.module('CommonModule', ['ui.bootstrap'])
        .factory('sharedProperties', function ($rootScope, $log) {
            var ratingResults = {};
            var futureRatingResults = {};
            var riskType = "";
            return {
                setRatingResults: function (results) {
                    ratingResults = results;
                },
                getRatingResults: function () {
                    return ratingResults;
                },
                setFutureRatingResults: function (results) {
                    futureRatingResults = results;
                },
                getFutureRatingResults: function () {
                    return futureRatingResults;
                },
                setRiskType: function (type) {
                    riskType = type;
                },
                getRiskType: function () {
                    return riskType;
                }
            }
        })
        // Service to support common popup windows
        .service('modalPopups', ['$rootScope', '$location', '$injector', '$log', function ($rootScope, $location, $injector, $log) {
            // ********* Generic Alert Window popup *****************
            this.alertWindowPopup = function (message, type, cb) {
                $log.debug("Show alert window  popup message: " + message);
                var $modal = $injector.get('$uibModal');
                if (type == undefined) {
                    type = "Info";
                }
                var modalInstance = $modal.open({
                    templateUrl: 'alertWindow.html',
                    //size: sm      // sm, lg or md
                    windowClass: "alert_popup",
                    controller: AlertWindowModalInstanceCtrl,
                    resolve: {
                        message: function () {
                            return message;
                        },
                        type: function () {
                            return type;
                        }
                    }
                });
                // call this when closing the popup
                modalInstance.result.then(function () { // close
                        console.log("Closing Alert Window Popup");
                        // cb is optional
                        if (cb) {
                            cb(null); // notify caller that modal was closed
                        }
                        //window.location("/init/login");
                        // $location.url('/init/login');
                    }
                    // ,
                    // function () {  // dismiss
                    //
                    //                console.log("Closing unauthorized access popup, no input");
                    //
                    //            }
                );
            };
            var AlertWindowModalInstanceCtrl = function ($scope, $uibModalInstance, message, type) {
                console.log("Inside AlertWindowModalInstanceCtrl, message: " + message + ", type[" + type + "]");
                $scope.message = message;
                $scope.type = type;
                $scope.alertOk = function () {
                    console.log("alertOk");
                    $uibModalInstance.close();
                };
            };
        }])
        // Service to support clicking on appointments in the calendar
        .service('appointmentModalPopup', ['$rootScope', '$location', '$injector', '$log', function ($rootScope, $location, $injector, $log) {
            // ********* Appointment Popup Window *****************
            this.appointmentPopupWindow = function (message, type, event, cb) {

                $log.debug("Show patient appointment details window message: " + message);
                var $modal = $injector.get('$uibModal');

                var modalInstance = $modal.open({
                    templateUrl: 'eventPopoverTemplate.html',
                    //size: sm      // sm, lg or md
                    windowClass: "alert_popup",
                    controller: AppointmentWindowModalInstanceCtrl,
                    resolve: {
                        message: function () {
                            return message;
                        },
                        type: function () {
                            return type;
                        },
                        event: function () {
                            return event;
                        }
                    }
                });
                // call this when closing the popup
                modalInstance.result.then(function (selAppointment) { // close
                    $log.debug("Closing Appointment Popup Window");
                    // cb is optional
                    if (cb) {
                        cb(selAppointment); // notify caller that modal was closed
                    }
                    //window.location("/init/login");
                    // $location.url('/init/login');
                }, function () { // dismiss
                    console.log("Closing activity popup");
                });
            };
            var AppointmentWindowModalInstanceCtrl = function ($scope, $location, $uibModalInstance, message, type, event) {
                console.log("Inside AppointmentWindowModalInstanceCtrl, message: " + message + ", type[" + type + "], event: ", event);
                $scope.message = message;
                $scope.type = type;
                $scope.event = event;

                $scope.startDate = new Date(event.start);
                //$scope.startTime = $scope.startDate.getHours() + ":" + $scope.startDate.getMinutes();

                $scope.endDate = new Date(event.end);
                //$scope.endTime = $scope.endDate.getHours() + ":" + $scope.endDate.getMinutes();

                $scope.popupClose = function () {
                    console.log("click Close");
                    $uibModalInstance.close(false);
                };
                $scope.popupAccept = function () {
                    console.log("click Accept");
                    $uibModalInstance.close(true);
                };
                $scope.popupGoTo = function () {
                    console.log("click goTo, type["+$scope.type+"]");

                    $uibModalInstance.close();
                    if ($scope.type === 'C'){
                        $location.url('/patient?clinicName=' + $scope.event.clinicName + '&patientName=' + $scope.event.patientName);
                    }
                    else if ($scope.type === 'P'){
                        $location.url('/physician?physicianName=' + $scope.event.physicianName + '&patientName=' + $scope.event.patientName);
                    }

                };
            };
        }]);
})();