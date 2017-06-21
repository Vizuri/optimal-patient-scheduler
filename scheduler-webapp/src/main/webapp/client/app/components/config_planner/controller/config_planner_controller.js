(function () {
    angular.module('ConfigModule', ['ngResource'])
        .controller('ConfigController',
            function ($rootScope, $scope, $http, $location, $log, $timeout, poller, modalPopups, PlannerService, PlannerScheduleService, PlannerBestSolutionService, PlannerBestSolutionStopService, PlannerAcceptAllService) {
                $log.debug('Inside ConfigController');

                $scope.showMouseHourGlass = false;
                $scope.errorMessages;
                $scope.inputTabs = {active: 0};

                $scope.plannerSeeded = false;
                $rootScope.polling = {isPolling : false,
                                      status: "STOPPED"};


                $scope.haveInputData = true;
                $scope.plannerConfiguration = {};    // in weeks

                $scope.maxPatientsSlider = {

                    options: {
                        showSelectionBar: true,
                        floor: 1,
                        ceil: 100,
                        getSelectionBarColor: function(value) {
                            return '#005595';
                        },
                        getPointerColor: function(value) {

                            return '#005595';
                        },
                        onChange: function(id) {
                            $log.debug('Slider['+id+'] changed');
                        }
                    }
                };

                $scope.scheduleWindowSlider = {

                    options: {
                        showSelectionBar: true,
                        floor: 1,
                        ceil: 10,
                        getSelectionBarColor: function(value) {
                            return '#005595';
                        },
                        getPointerColor: function(value) {

                            return '#005595';
                        }
                    }
                };

                function alertPopup(message, type, cb) {
                    modalPopups.alertWindowPopup(message, type, cb);
                }


                $scope.goToPage = function(type) {

                    $log.debug('Inside goToPage, type: ', type);

                    if (type === 'C'){
                        $location.url('/clinic');
                    }
                    else if (type === 'D'){
                        $location.url('/physician');
                    }
                    else if (type === 'P'){
                        $location.url('/patient');
                    }

                };

                $scope.seedPlanner = function(){

                    $log.debug('Inside seedPlanner');

                    $scope.showMouseHourGlass = true;
                    PlannerService.update($scope.plannerConfiguration, function (response) {

                            $log.debug('seedPlanner received data: ', response);
                            $scope.showMouseHourGlass = false;
                            $scope.plannerSeeded = true;

                            sessionStorage.setItem('plannerSeeded', JSON.stringify($scope.plannerSeeded));
                            sessionStorage.setItem('plannerConfiguration', JSON.stringify($scope.plannerConfiguration));
                            // reset clinics
                            sessionStorage.removeItem('clinics');
                            sessionStorage.removeItem('selClinicName');

                            alertPopup('Successfully updated the Planner Configuration', 'Info');
                            //$location.path('/clinic');
                        },
                        function (err) {
                            console.error('seedPlanner:received an error: ', err);
                            $scope.showMouseHourGlass = false;
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
                            alertPopup('Unable to update Planner config, error: ' + $scope.errorMessages, 'Error');
                        });
                };

                $scope.calculateSchedule = function () {
                    $log.debug('Inside calculateSchedule');

                    $scope.showMouseHourGlass = true;

                    // save configuration
                    sessionStorage.setItem('plannerConfiguration', JSON.stringify($scope.plannerConfiguration));

                    PlannerScheduleService.calculate({max: $scope.plannerConfiguration.maxPatientAppointments}, function (response) {

                        $log.debug('calculateSchedule received data: ', response);
                        $scope.showMouseHourGlass = false;

                        alertPopup('Planner optimization Started', 'Info');
                        startPoller();
                        //$location.url('/clinic');
                    },
                    function (err) {
                        console.error('calculateSchedule:received an error: ', err);
                        $scope.showMouseHourGlass = false;
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
                        alertPopup('Unable to update Planner config, error: ' + $scope.errorMessages, 'Error');
                    });


                };


                $scope.resetConfiguration = function () {
                    $scope.configForm.$setPristine();

                    sessionStorage.setItem('plannerSeeded', JSON.stringify(false));
                    init();
                };

                // this is just to solve a rendering issue when the slider is inside a tabset
                $scope.refreshSliders = function (id) {
                    $log.debug('Inside refreshSliders, id: ' + id);

                    $timeout(function() {

                        $scope.$broadcast('rzSliderForceRender');

                    }, 15);
                };

                function startPoller(){

                    $log.debug('Inside startPoller');

                    var solutionPoller = poller.get(PlannerBestSolutionService,{
                        action: 'poll',
                        delay: 3000
                    });

                    solutionPoller.promise.then(null,null,
                        function(result){

                            if (result.$resolved) {

                                $log.debug('polling result', result);

                                $rootScope.polling = {status: (result.status == "STOPPED" ? "COMPLETE": "SOLVING" ),
                                                      score: result.score,
                                                      treatmentCount: result.treatmentCount,
                                                      draftConsultCount: result.draftConsultCount,
                                                      acceptedConsultCount:result.acceptedConsultCount,
                                                      message:result.message,
                                                      attempts: result.attempts};

                                //sessionStorage.setItem('polling', JSON.stringify($rootScope.polling));

                                if (result.status === "STOPPED"){
                                    $log.debug('poller stopped');
                                    poller.stopAll();
                                    $rootScope.polling.isPolling = false;
                                }
                                else{
                                    $rootScope.polling.isPolling = true;
                                }


                            }
                            else {

                                $log.error('error polling', result);
                                // Error handler: (data, status, headers, config)
                                if (result.status === 503) {
                                    // Stop poller or provide visual feedback to the user etc
                                    poller.stopAll();
                                }
                            }
                    });
                }

                $scope.acceptAllAppointment = function(){

                    $log.debug('Inside acceptAllAppointment');

                    PlannerAcceptAllService.accept(function (response) {

                            $log.debug('accept received data: ', response);
                            $scope.showMouseHourGlass = false;

                            alertPopup('All appointments accepted', 'Info');

                        },
                        function (err) {
                            console.error('accept:received an error: ', err);
                            $scope.showMouseHourGlass = false;
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
                            alertPopup('Unable to accept all appointments, error: ' + $scope.errorMessages, 'Error');
                        });
                };

                $scope.stopPolling = function () {
                    $log.debug('Inside stopPolling');

                    // Stop all pollers.
                    poller.stopAll();


                    PlannerBestSolutionStopService.terminate(function (response) {

                            $log.debug('terminate received data: ', response);
                            $scope.showMouseHourGlass = false;

                            $rootScope.polling.isPolling = false;
                            $rootScope.polling.status = "STOPPED";
                        },
                        function (err) {
                            console.error('terminate:received an error: ', err);
                            $scope.showMouseHourGlass = false;
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
                            alertPopup('Unable to terminate Planner engine, error: ' + $scope.errorMessages, 'Error');
                        });

                };


                function init() {
                    $log.debug('Inside init');

                    $log.debug('b plannerSeeded: ', JSON.parse(sessionStorage.getItem('plannerSeeded')));

                    $scope.plannerSeeded = Boolean(JSON.parse(sessionStorage.getItem('plannerSeeded')));

                    $log.debug('plannerSeeded: ', $scope.plannerSeeded);

                    if ($scope.plannerSeeded === true){
                        $log.debug('already seeded Planner');
                        $scope.plannerConfiguration = JSON.parse(sessionStorage.getItem('plannerConfiguration'));
                    }
                    else{
                        // Default
                        $scope.plannerConfiguration = {
                            maxPhysicians: 50,
                            maxPatientsPerClinic: 10,
                            maxRooms: 3,
                            maxClinics: 10,
                            maxPatientAppointments: 10,  // %
                            scheduleWindow : 4 // in weeks
                        };
                    }

                }

                init();

            });
})();