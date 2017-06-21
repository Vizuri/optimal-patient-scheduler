(function () {
    angular.module('PatientModule')
        .factory('PatientService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/patient/:name', {name: '@name'}, {
                'get': {
                    method: 'POST',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PatientClinicService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/patient/for_clinic/:name', {name: '@name'}, {
                'get': {
                    method: 'POST',
                    isArray: true,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PatientAvailableAppointmentService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/find/patient/appointment/:name', {name: '@name'}, {
                'get': {
                    method: 'POST',
                    isArray: true,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        .factory('PatientAppointmentService', function ($resource) {
            return $resource('/scheduler-webapp/rest/planner/schedule/patient/appointment/:id', {id: '@id'}, {
                'get': {
                    method: 'POST',
                    isArray: false,
                    headers: {
                        Accept: 'application/json'
                    }
                }
            });
        })
        // Service to support clicking on appointments in the calendar
        .service('selectAppointmentModalPopup', ['$rootScope', '$location', '$injector', '$log', function ($rootScope, $location, $injector, $log) {
            // ********* Appointment Popup Window *****************
            this.appointmentPopupWindow = function (patientName, cb) {

                $log.debug("Show patient available appointments window, patientName: " + patientName);
                var $modal = $injector.get('$uibModal');

                var modalInstance = $modal.open({
                    templateUrl: 'availableAppointmentsTemplate.html',
                    //size: sm      // sm, lg or md
                    windowClass: "alert_popup",
                    controller: AppointmentWindowModalInstanceCtrl,
                    // these will be passed to the controller
                    resolve: {
                        patientName: function () {
                            return patientName;
                        }

                    }
                });
                // call this when closing the popup
                modalInstance.result.then(function (selAppointment) { // close
                    $log.debug("Closing Select Appointment Popup Window, selAppointment", selAppointment);
                    // cb is optional
                    if (cb) {
                        cb(selAppointment); // notify caller that modal was closed
                    }

                }, function () { // dismiss
                    $log.debug("Cancel popup");
                });
            };
            var AppointmentWindowModalInstanceCtrl = function ($scope, $location, $uibModalInstance, PatientAvailableAppointmentService, patientName) {
                $log.debug("Inside AppointmentWindowModalInstanceCtrl, patientName: " + patientName);

                $scope.patientName = patientName;
                $scope.appointments = [];
                $scope.selectedAppointment = {};
                $scope.loadingAvailableAppointments = false;

                $scope.popupClose = function () {
                    console.log("click Close");
                    //$uibModalInstance.close();
                    $uibModalInstance.dismiss();
                };
                $scope.popupAccept = function () {
                    $log.debug("click accept, selectedAppointment: ", $scope.selectedAppointment);

                    $uibModalInstance.close($scope.selectedAppointment);

                };

                function showPatientAvailableAppointments() {

                    $log.info("Inside showPatientAvailableAppointments", $scope.patientName);

                    $scope.loadingAvailableAppointments = true;
                    $scope.showMouseHourGlass = true;

                    PatientAvailableAppointmentService.get({name: $scope.patientName}, function (response) {

                            $log.debug('showPatientAvailableAppointments received data: ', response);
                            $scope.showMouseHourGlass = false;
                            $scope.loadingAvailableAppointments = false;
                            $scope.appointments = response;
                            //alertPopup('Successfully loaded all clinics', 'Info');

                        },
                        function (err) {
                            $log.error('showPatientAvailableAppointments:received an error: ', err);
                            $scope.showMouseHourGlass = false;
                            $scope.loadingAvailableAppointments = false;

                            // err: {data: "", status: 415, config: Object, statusText: "Unsupported Media Type"}
                            if (err.data) {
                                $scope.errorMessages = err.data.message;
                            }
                            else if (err.statusText) {
                                $scope.errorMessages = [err.status + ":" + err.statusText];
                            }
                            else {
                                $scope.errorMessages = ['Unknown  server error'];
                            }
                            //alertPopup("Unable to retrieve available appointment for patient, error: " + $scope.errorMessages, 'Error');
                        });

                }

                showPatientAvailableAppointments();
            };
        }]);
})();