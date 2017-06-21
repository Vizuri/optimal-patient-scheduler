(function () {
    angular.module('ClinicDetailModule', ['ngResource'])
        .controller('ClinicController',
            function ($rootScope, $scope, $http, $route, $location, $log, $filter, $compile, $timeout, uiCalendarConfig, $uibModal, poller, modalPopups, appointmentModalPopup, ClinicService, PlannerBestSolutionService, ClinicPollService, PatientAppointmentService) {
                $log.debug("Inside ClinicController");

                var date = new Date();
                var d = date.getDate();
                var m = date.getMonth();
                var y = date.getFullYear();

                $log.debug("y["+y+"], m["+m+"], d["+d+"]");

                $scope.clinicTabs = {active: 1};

                $scope.clinics = [];
                $scope.selectedClinic = {};

                //$scope.clinicSchedule;
                $scope.appointments = [];
                $scope.calendarRendered = false;
                $scope.selectedAppointment = {};
                //$scope.removeTreatments = false;

                $scope.events = [];
                //     //{title: 'All Day Event',start: new Date(y, m, 1), resourceIds: ['a', 'b']},
                //     {title: 'Appointment with P1',start: new Date(y, m, 1, 12, 0), end: new Date(y, m, 1, 12, 45), resourceIds: ['doc'], color: "blue", allDay: false },
                //     {title: 'Dialysis Session P1',start: new Date(y, m, 1, 8, 0), end: new Date(y, m, 1, 12, 0), resourceIds: ['a'], color: "green", allDay: false },
                //     {title: 'Dialysis Session P2',start: new Date(y, m, 1, 12, 0), end: new Date(y, m, 1, 18, 0), resourceIds: ['b'], allDay: false },
                //     {title: 'Dialysis Session P4',start: new Date(y, m, 2, 8, 0), end: new Date(y, m, 2, 12, 0), resourceIds: ['a'], allDay: false },
                //     {title: 'Dialysis Session P5',start: new Date(y, m, 2, 12, 0), end: new Date(y, m, 2, 18, 0), resourceIds: ['b'], allDay: false },
                //
                //     {title: 'Long Event',start: new Date(y, m, d - 5),end: new Date(y, m, d - 2)},
                //     {id: 999,title: 'Repeating Event',start: new Date(y, m, d - 3, 16, 0),allDay: false},
                //     {id: 999,title: 'Repeating Event',start: new Date(y, m, d + 4, 16, 0),allDay: false},
                //     {title: 'Birthday Party',start: new Date(y, m, d + 1, 19, 0),end: new Date(y, m, d + 1, 22, 30),allDay: false},
                //     {title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 29),url: 'http://google.com/'}
                // ];

                var resources = [
                    {
                        id: 'a',
                        title: 'A',
                        type:'A'
                    },
                    {
                        id: 'b',
                        title: 'B',
                        type:'A'
                    },
                    {
                        id: 'c',
                        title: 'C',
                        type:'A'
                    },
                    {
                        id: 'tr',
                        title: 'Treatment',
                        type:'T'
                    }
                ];

                function alertPopup(message, type, cb) {
                    modalPopups.alertWindowPopup(message, type, cb);
                }

                function appointmentPopup(message, type, event, cb) {
                    appointmentModalPopup.appointmentPopupWindow(message, type, event, cb);
                }

                /* alert on dayClick */
                $scope.alertDayOnClick = function( date, jsEvent, view){

                    $log.debug('Inside alertDayOnClick');
                    $scope.alertMessage = (date.title + ' was clicked ');
                };

                function clickEvent(calEvent, jsEvent, view) {
                    //alert(calEvent.title);
                    $log.debug('Inside clickEvent, calEvent: ', calEvent);

                    if (calEvent.status === "DRAFT") {

                        appointmentPopup("Appointment Details", 'C', calEvent, function(accept){

                            $log.debug('accept: ', accept);

                            if (accept == true){

                                $scope.selectedAppointment = calEvent;

                                $log.debug('calEvent: ', calEvent);

                                $scope.schedulePatientAppointment();
                            }

                        });
                    }
                    else{
                        appointmentPopup("Appointment Details", 'C', calEvent);
                    }

                    //alertPopup("Event Details\n" + calEvent.title , 'Info');
                }

                function mouseoverEvent(calEvent, jsEvent, view){

                    $log.debug('Inside mouseoverEvent, start: ' + new Date(calEvent.start) + ", end: " + new Date(calEvent.end) );

                    //alertPopup("Event Details\n" + calEvent.title , 'Info');
                }

                /* alert on Resize */
                $scope.alertOnResize = function(event, delta, revertFunc, jsEvent, ui, view ){
                    $log.debug('Inside alertOnResize');

                    $scope.alertMessage = ('Event Resized to make dayDelta ' + delta);
                };

                /* Render Tooltip */
                function eventRender( event, element, view ) {
                    $log.debug('Inside eventRender, event: ', event.title, event.id);

                   // element.attr({'uib-tooltip': event.title,
                   //     'tooltip-placement': 'auto'});

                    //'uib-popover': event.title 'uib-popover-template': 'eventPopoverTemplate.html'
                    //element.attr({'uib-popover-template': "'eventPopoverTemplate.html'", 'popover-title': event.title,
                    //              'popover-placement': 'bottom', 'popover-trigger': "'mouseenter'"});

                    //eventPopoverTemplate
                    $compile(element)($scope);
                }

                /* add custom event*/
                $scope.addEvent = function(appointment) {

                    // var ev = {
                    //     title: 'Open Sesame',
                    //     start: new Date(y, m, 24, 14,0),
                    //     end: new Date(y, m, 24, 15,0),
                    //     resourceIds: ['b'],
                    //     allDay: false
                    //
                    // };

                    $log.debug('Inside addEvent, event: ', appointment);
                    $scope.events.push(appointment);

                    $timeout(function() {
                        uiCalendarConfig.calendars.clinicSchedule.fullCalendar('rerenderEvents');
                    });
                };

                function addResource(resource){

                    $log.debug('Inside addResource, resource: ', resource);

                    $timeout(function() {
                        uiCalendarConfig.calendars.clinicSchedule.fullCalendar('addResource', resource);
                    }, 5);

                }

                function removeResource(resourceId){

                    $log.debug('Inside removeResource, resourceId: ', resourceId);

                    $timeout(function() {
                        uiCalendarConfig.calendars.clinicSchedule.fullCalendar('removeResource', resourceId);
                    }, 5);

                }

                $scope.eventsF = function (start, end, timezone, callback) {
                    $log.debug('Inside eventsF, event: ',  m,y);

                    $log.debug('start date: ',  new Date(start));
                    $log.debug('end date: ',  new Date(end));

                    var events = $scope.appointments;

                    //filter out treatments
                    // if ($scope.removeTreatments === true){
                    //
                    //     $scope.removeTreatments = false;
                    //     events = [];    //$scope.appointments;
                    //
                    //     //true if d is between the start and end (inclusive)
                    //     //false if d is before start or after end.
                    //     // NaN if one or more of the dates are illegal.
                    //     $scope.appointments.forEach(function(appointment){
                    //
                    //         $log.debug('check appointment type: ',  appointment.type);
                    //         if (appointment.type === "A"){
                    //
                    //             $log.debug('include appointment: ',  appointment);
                    //             events.push(appointment);
                    //         }
                    //         else{
                    //             $log.debug('remove treatment: ',  appointment);
                    //         }
                    //     });
                    // }

                   // var s = new Date(start).getTime() / 1000;
                   // var e = new Date(end).getTime() / 1000;
                    //var m = new Date(start).getMonth();
                    //var events = [{title: 'Feed Me ' + m,start: new Date(2017, 1, 28, 12,00) ,end: new Date(2017, 1, 28, 13,00),resourceIds: ['b'], allDay: false}];

                    //$log.debug('new events: ', events);
                    callback(events);
                    //callback($scope.appointments);
                };

                $scope.calEventsExt = {
                    events: [
                        {type:'party',title: 'Lunch',start: new Date(y, m, d, 12, 0),end: new Date(y, m, d, 14, 0),resourceIds: ['b'], allDay: false},
                        {type:'party',title: 'Lunch 2',start: new Date(y, m, d, 12, 0),end: new Date(y, m, d, 14, 0),resourceIds: ['b'], allDay: false},
                        {type:'party',title: 'Click for Google',start: new Date(y, m, 28),end: new Date(y, m, 28),resourceIds: ['b']}
                    ]
                };

                /* alert on Drop */
                function dropEvent(event, delta, revertFunc, jsEvent, ui, view){
                    $log.debug('Inside dropEvent');

                    $scope.alertMessage = ('Event Droped to make dayDelta ' + delta);
                }

                /* remove event */
                $scope.remove = function(index) {
                    $scope.events.splice(index,1);
                };


                //changeView('agendaDay', myCalendar)
                /* Change View */
                function changeView(view,calendarName) {

                    $log.debug('Inside changeView, view: ', view);

                    $timeout(function() {
                        uiCalendarConfig.calendars[calendarName].fullCalendar('changeView',view);
                    }, 5);

                }

                //with this you can handle the events that generated by each page render process
                function renderView(view){

                    $log.debug('Inside renderView, view: ', view);

                    // uiCalendarConfig.calendars.clinicSchedule.fullCalendar({
                    //     defaultDate: new Date(2016, 4, 20)
                    // });

                    // var date = new Date(view.calendar.getDate());
                    // $scope.currentDate = date.toDateString();
                    // $scope.$apply(function(){
                    //     $scope.alertMessage = ('Page render with date '+ $scope.currentDate);
                    // });
                }

                /* Update Calendar View */
                function renderCalender(calendarName, action) {

                    $log.debug('Inside renderCalender, calendarName: ', calendarName, action);

                    //uiCalendarConfig.calendars.calendar.fullCalendar('rerenderEvents');
                    //$scope.calendar.fullcalendar('render');
                    //uiCalendarConfig.calendars.clinicSchedule.fullCalendar('rerenderEvents');
                    //$scope.uiConfig.calendar.render();
                    //$scope.clinicSchedule.fullCalendar('render');

                    //var calendarTag = $('#' + calendarId);
                    //calendarTag.fullCalendar('render'); //rerenderEvents

                    $log.debug("render appointments: ", $scope.appointments.length);

                    if(uiCalendarConfig.calendars[calendarName]){

                        $log.debug('call: ' + action);

                        if (action === "render"){
                            $scope.calendarRendered = true;
                        }

                        // $timeout(function() {
                        //     uiCalendarConfig.calendars[calendarName].fullCalendar('refetchEvents');  //refetchEvents //render rerenderEvents
                        // }, 10); // delay 10 ms


                        $timeout(function() {
                            uiCalendarConfig.calendars[calendarName].fullCalendar(action);  //refetchEvents //render rerenderEvents
                        }, 20); // delay 10 ms
                    }
                }

                $scope.uiConfig = {
                    calendar:{
                        defaultView: 'timelineWeek',   //'agendaWeek', timelineMonth
                        //defaultDate : new Date(2017, 2, 18),
                        nowIndicator: true,
                        //gotoDate, date
                        firstDay: 1,
                        hiddenDays: [ 0 ], // hide Sunday
                        minTime: "08:00",
                        maxTime : "20:00",
                        allDaySlot: false,
                        // businessHours: {
                        //     // days of week. an array of zero-based day of week integers (0=Sunday)
                        //     dow: [ 1, 2, 3, 4, 5, 6 ], // Monday - Saturday
                        //     start: '08:00', // a start time (8am in this example)
                        //     end: '18:00' // an end time (6pm in this example)
                        // },
                        fixedWeekCount: true,
                        displayEventTime: true,
                        height: 515,
                        editable: false,
                        disableResizing:true,
                        header:{
                            left: 'listMonth timelineMonth month timelineWeek agendaWeek agendaDay',
                            center: 'title',
                            right: 'today prev,next'
                        },
                        footer:{
                            left: 'hideTreatmentsButton  showTreatmentsButton'

                        },
                        resourceColumns: [
                            {
                                labelText: "Type",
                                field: 'type',
                                width: 25   // pixels
                            },
                            {
                                labelText: "Name",
                                field: 'title'
                               // width: 80   // pixels
                            }
                        ],
                        resources: resources,
                        dayClick: $scope.alertDayOnClick,
                        eventClick: clickEvent,
                        eventMouseover: mouseoverEvent,
                        eventDrop: dropEvent,
                        eventResize: $scope.alertOnResize,
                        //eventRender: eventRender,
                        viewRender: renderView,
                        customButtons: {
                            hideTreatmentsButton: {
                                text: 'Hide Treatments',
                                click: function() {
                                    //alert('clicked the custom button!');
                                    //$scope.removeTreatments = true;
                                    removeResource("tr");
                                    $scope.refreshCalendar();
                                }
                            },
                            showTreatmentsButton: {
                                text: 'Show Treatments',
                                click: function() {
                                    //alert('clicked the custom button!');
                                    //$scope.removeTreatments = false;

                                    addResource({
                                        id: "tr",
                                        title: "Treatment",
                                        type: "T"
                                    });
                                    $scope.refreshCalendar();
                                }

                            }
                        },
                        // columnFormat: {
                        //     timelineMonth: 'dddd'
                        //
                        // },
                        views: {
                            timelineMonth: { // name of view
                                //titleFormat: 'YYYY, MM, DD',
                                //columnFormat: "dddd D",
                                buttonText: 'Month Time Line',
                                slotWidth: 100,   // in pixels
                                resourceAreaWidth: "13%",
                                resourceLabelText: "Rooms",
                                slotLabelFormat: [
                                   // 'MMMM YYYY', // top level of text
                                    'dddd D'        // lower level of text
                                ]
                            },
                            month: { // name of view
                                buttonText: 'Month'
                            },
                            agendaWeek: {
                                buttonText: 'Week'
                            },
                            agendaDay: {
                                buttonText: 'Day'
                            },
                            listMonth: {
                                buttonText: 'Month List'
                            },
                            timelineWeek: {
                                buttonText: 'Week Time Line'
                            }
                        },
                        schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives',
                        groupByDateAndResource: true
                    }
                };

                $scope.eventSources = [$scope.eventsF];  //$scope.events, , $scope.eventsF, $scope.calEventsExt

                // $scope.refreshAllClinics = function () {
                //     $log.debug('Inside refreshAllClinics');
                //
                //     $scope.loadAllClinics();
                //
                //     $scope.selectedClinic.name = sessionStorage.getItem('selClinicName');
                //
                //     $log.debug('selectedClinic: ', $scope.selectedClinic, $scope.selectedClinic.name);
                //
                //     if ($scope.selectedClinic.name != "null" && $scope.selectedClinic.name != undefined){
                //
                //         $scope.selectClinic();
                //     }
                //
                // };

                $scope.loadAllClinics = function (cb) {
                    $log.debug('Inside loadAllClinics');

                    $scope.showMouseHourGlass = true;

                    ClinicService.get(function (response) {

                            $log.debug('loadAllClinics received data: ', response);
                            $scope.showMouseHourGlass = false;
                            $scope.clinics = response;

                            cb(null);
                            //alertPopup('Successfully loaded all clinics', 'Info');

                           // sessionStorage.setItem('clinics', JSON.stringify($scope.clinics));

                        },
                        function (err) {
                            $log.error('loadAllClinics:received an error: ', err);
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

                            cb(err);
                            //alertPopup('Unable to load Clinic Data, error: ' + $scope.errorMessages, 'Error');
                        });


                };

                function addAppointment(appointment) {

                    //$log.debug('Inside addAppointment, appointment: ', appointment);

                    var appDate = appointment.appointmentDate;
                    var startTime =  new Date(appDate.year, (appDate.monthValue - 1), appDate.dayOfMonth,
                        appointment.startTime.hour, appointment.startTime.minute);

                    var endTime = new Date(appDate.year, (appDate.monthValue - 1), appDate.dayOfMonth,
                        appointment.endTime.hour, appointment.endTime.minute);

                    var title = "Treatment for " + appointment.patientName;
                    var appColor = "green";

                    if (appointment.type === "A"){

                        if (appointment.status === "DRAFT"){
                            appColor = "grey";
                            title = "Available Appointment for "
                        }
                        else{
                            appColor = "blue";
                            title = "Appointment for ";
                        }
                    }
                    else{
                        title = "Treatment for ";
                    }

                    title += appointment.patientName;

                    var newAppointment = {title: title,start: startTime, end: endTime,
                                          resourceIds: [appointment.roomId],
                                          clinicName: appointment.clinicName,
                                          patientName: appointment.patientName,
                                          color: appColor,
                                          status: appointment.status,
                                          id:appointment.id,
                                          physicianName: appointment.physicianName,
                                          type: appointment.type,
                                          allDay: false };

                    //$scope.calEventsExt.events.push(newAppointment);
                    $scope.appointments.push(newAppointment);
                    //$scope.events.push(newAppointment);

                    //$scope.addEvent(newAppointment);
                    //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('renderEvent',newAppointment,true);

                    //$log.debug("add event: ", newAppointment);
                }

                $scope.selectClinic = function () {
                    $log.debug("Inside selectClinic, name["+ $scope.selectedClinic.name + "]");

                    //$log.debug("first Clinic: ", $scope.clinics[0]);

                   // sessionStorage.setItem('selClinicName', $scope.selectedClinic.name);

                    // switch back to the first tab
                    //$scope.clinicTabs.active = 0;

                    var selected = $filter('filter')($scope.clinics, {name: $scope.selectedClinic.name}, true);
                    $scope.selectedClinic.obj = selected[0];
                    $scope.selectedClinic.obj.totalTreatments = 0;
                    $log.debug("selectClinic: ", $scope.selectedClinic.obj);

                    // reset the calendar by removing any previous appointments
                    //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('removeEvents');
                    //$log.debug("remove all previous events");

                    //$scope.events.splice(0,$scope.events.length);
                    //$scope.events = [];
                    //$scope.calEventsExt.events = [];
                    $scope.appointments = [];
                    //$scope.calEventsExt.events.splice(0, $scope.calEventsExt.events.length);

                    //check to see if we need to add/remove any rooms
                    if ($scope.selectedClinic.obj.rooms){

                        resources.forEach(function(resource){

                            // we only reset appointment rooms
                            if (resource.id !== "tr"){
                                removeResource(resource.id);
                            }
                        });

                        $scope.selectedClinic.obj.rooms.forEach(function(room){

                            $log.debug("add room : ", room.id);

                            if (room.label !== "tr") {
                                addResource({
                                    id: room.label,
                                    title: "Room " + room.label.toUpperCase(),
                                    type: room.type
                                });
                            }

                        });

                    }

                    refreshAppointments();

                    checkPollingSolution();

                    //$log.debug("all events: ", $scope.events);
                    //$scope.eventSources = [$scope.events];
                   //$scope.eventSources = { events: $scope.events};
                   // uiCalendarConfig.calendars['clinicSchedule'].fullCalendar('addEventSource', $scope.events[0]);
                    //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('refetchEvents');  // refetchEvents render rerenderEvents
                    //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('rerenderEvents');
                   // $scope.clinicSchedule.fullCalendar("rerenderEvents");

                    //window.calendar.fullCalendar('refetchEvents');
                   //window.calendar.fullCalendar('rerenderEvents');

                        //{title: 'All Day Event',start: new Date(y, m, 1), resourceIds: ['a', 'b']},
                    //     {title: 'Appointment with P1',start: new Date(y, m, 1, 12, 0), end: new Date(y, m, 1, 12, 45), resourceIds: ['doc'], color: "blue", allDay: false },
                    //     {title: 'Dialysis Session P1',start: new Date(y, m, 1, 8, 0), end: new Date(y, m, 1, 12, 0), resourceIds: ['a'], color: "green", allDay: false }
                    // ];

                };

                $scope.refreshCalendar = function(){

                    $log.debug("Inside refreshCalendar");

                   //  // the calendar must be visible to rerender
                   //  //if ($scope.clinicTabs.active === 1){
                   //
                   //      //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('renderEvent',$scope.appointments[0],true);
                   //
                   //      renderCalender("clinicSchedule", "render");
                   //
                   //      //changeView('agendaDay', "clinicSchedule");
                   // // }

                    if ($scope.calendarRendered === true){

                        renderCalender("clinicSchedule", "refetchEvents");
                    }
                    else{
                        renderCalender("clinicSchedule", "render");
                    }


                };

                $scope.showPatientsForClinic = function(){
                    $log.debug("Inside showPatientsForClinic, clinic["+ $scope.selectedClinic.name + "]");
                    //$location.url('/patient');    // path
                    $location.url('/patient?clinicName=' + $scope.selectedClinic.name);
                };

                $scope.goToPatientSchedule = function(patientName){
                    $log.debug("Inside goToPatientSchedule, patient["+ patientName + "]");
                };

                function refreshAppointments() {
                    $log.info("Inside refreshAppointments");

                    // reset the calendar by removing any previous appointments
                    //uiCalendarConfig.calendars["clinicSchedule"].fullCalendar('removeEvents');

                    //$log.debug("remove all previous events");

                    var treatmentCount = 0;
                    var firstAppointment = true;
                    var startDate;
                    if ($scope.selectedClinic.obj.appointments != undefined && $scope.selectedClinic.obj.appointments.length > 0){

                        // reset appointments
                        $scope.appointments = [];

                        $log.debug("total appointments: ", $scope.selectedClinic.obj.appointments.length);
                        $scope.selectedClinic.obj.appointments.forEach(function(appointment){

                            if (appointment.type === "T"){
                                treatmentCount++;
                            }
                            // this will let the calendar jump to the fist appointment
                            else if (firstAppointment === true ) {  // A

                                firstAppointment = false;

                                //var appDate = appointment.appointmentDate;
                                startDate = appointment.appointmentDate;
                                //startDate = new Date(appDate.year, (appDate.monthValue - 1), appDate.dayOfMonth,
                                //                          appointment.startTime.hour, appointment.startTime.minute);

                            }

                            addAppointment(appointment);

                        });

                        $scope.selectedClinic.obj.totalTreatments = treatmentCount;

                        $log.debug("active["+ $scope.clinicTabs.active + "]");

                        if ($scope.clinicTabs.active === 1){


                            //renderCalender("clinicSchedule", "refetchEvents");

                            if (startDate != undefined){

                                var calendar = uiCalendarConfig.calendars.clinicSchedule.fullCalendar('getCalendar');

                                var sd =  startDate.year +"-" + getTwoDigit(startDate.monthValue) + "-" + getTwoDigit(startDate.dayOfMonth);    // '2017-03-10';

                                $log.debug("startDate: " + sd);

                                var m = calendar.moment(sd);  //moment('2017-05-01');
                                uiCalendarConfig.calendars.clinicSchedule.fullCalendar('gotoDate', m);

                                // $timeout(function() {
                                // //    uiCalendarConfig.calendars.clinicSchedule.fullCalendar('defaultDate', new Date(2016, 4, 20));  // scrollTime gotoDate  '2017-03-18', defaultDate defaultDate: new Date(2016, 4, 20)
                                //
                                // uiCalendarConfig.calendars.clinicSchedule.fullCalendar({
                                //     defaultDate: new Date(2016, 4, 20)
                                // });
                                //   $log.debug("jump to appointment: ", startDate);
                                // }, 5); // delay 10 ms

                                //$scope.uiConfig.calendar.defaultDate = new Date(2017, 2, 18);
                            }

                            $scope.refreshCalendar();



                        }

                    }

                }

                function getTwoDigit(value) {
                    //var month = date.getMonth() + 1;
                    return value < 10 ? '0' + value : '' + value; // ('' + month) for string result
                }

                function checkPollingSolution() {
                    $log.info("Inside checkPollingSolution, isPolling: ", ($rootScope.polling ? $rootScope.polling.isPolling : false));

                    //$scope.selectedClinic.name = sessionStorage.getItem('selClinicName');

                    $log.debug('selectedClinic: ', $scope.selectedClinic, $scope.selectedClinic.name);

                    // if ($scope.selectedClinic.name != "null" && $scope.selectedClinic.name != undefined){
                    //
                    //     $scope.selectClinic();
                    // }

                    if (($rootScope.polling) && ($rootScope.polling.isPolling === true) && ($scope.selectedClinic.name != undefined)){

                        var clinicPoller = poller.get(ClinicPollService,{
                            action: 'poll',
                            delay: 3000,
                            argumentsArray: [
                                {
                                    name: $scope.selectedClinic.name

                                }
                            ]
                        });

                        clinicPoller.promise.then(null,null,
                            function(result){

                                if (result.$resolved) {

                                    $log.debug('c polling result', result);

                                    $scope.selectedClinic.obj = result;

                                    //var clinic = $filter('filter')($scope.clinics, {name: $scope.selectedClinic.name}, true);
                                    //clinic[0].appointments = result.appointments;
                                    //clinic[0] = result;

                                    //$scope.selectClinic();

                                    if (result.status === "STOPPED"){
                                        $log.debug('c poller stopped');
                                        poller.stopAll();
                                    }

                                    // ****** Refresh calendar **************
                                    refreshAppointments();

                                }
                                else {

                                    $log.error('c error polling', result);
                                    // Error handler: (data, status, headers, config)
                                    if (result.status === 503) {
                                        // Stop poller or provide visual feedback to the user etc
                                        poller.stopAll();
                                    }
                                }
                            });
                    }
                }

                $scope.schedulePatientAppointment = function () {

                    $log.debug("Inside schedulePatientAppointment, id: ", $scope.selectedAppointment.id);

                    $scope.showMouseHourGlass = true;

                    // now schedule the given appointment
                    PatientAppointmentService.get({id: $scope.selectedAppointment.id}, function (response) {

                            $log.debug('schedulePatientAppointment received data: ', response);
                            $scope.showMouseHourGlass = false;

                            alertPopup('Successfully scheduled appointment', 'Info');

                            // now reload all appointments for this clinic
                            $scope.reloadClinic();

                        },
                        function (err) {
                            $log.error('schedulePatientAppointment:received an error: ', err);
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
                            alertPopup("Unable to schedule Patient's appointment, error: " + $scope.errorMessages, 'Error');
                        });

                };

                $scope.reloadClinic = function () {
                    $log.debug('Inside reloadClinic, name: ', $scope.selectedClinic.name);

                    $scope.showMouseHourGlass = true;
                    ClinicPollService.poll({name:$scope.selectedClinic.name}, function (response) {

                        $log.debug('reloadClinic received data: ', response);
                        $scope.showMouseHourGlass = false;

                        //alertPopup('Successfully loaded Patient', 'Info');
                        //$location.path('/patient');

                        // response.appointments.forEach(function(appointment){
                        //     $log.debug('found appointment id/type: ', appointment.id, appointment.type);
                        //
                        //     if ($scope.selectedAppointment.id === appointment.id){
                        //
                        //         $log.debug('got updated appointment: ', appointment);
                        //     }
                        // });

                        // It is important to update patient in clinic patient list
                        if ($scope.clinics){

                            var clinic = $filter('filter')($scope.clinics, {name: $scope.selectedClinic.name}, true);
                            clinic[0].appointments = response.appointments;

                            $scope.selectClinic();
                        }
                        else{
                            $log.error('reloadClinic no clinics to select from');
                        }


                    },
                    function (err) {
                        console.error('getPatient:received an error: ', err);
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
                        //alertPopup('Unable to load Patient, error: ' + $scope.errorMessages, 'Error');
                    });


                };

                function init() {
                    $log.info("Inside init");

                    // this is used fir when we want to cache clinic data
                    //$scope.clinics = JSON.parse(sessionStorage.getItem('clinics'));
                    //
                    // if ($scope.clinics && $scope.clinics.length > 0){
                    //     $log.debug('already loaded All Clinics');
                    // }
                    // else{
                    //     $scope.loadAllClinics();
                    // }

                    $scope.loadAllClinics(function(err){

                        // this is for viewing the duplicate record
                        if ($route.current.params != undefined && $route.current.params.clinicName != undefined) {

                            $log.debug("Inside init:PARAMS", $route.current.params);
                            $scope.selectedClinic.name = $route.current.params.clinicName;

                            $log.debug("clinicName: " + $scope.selectedClinic.name);

                            //$scope.reloadClinic();
                            $scope.selectClinic();
                        }
                    });




                    //checkPollingSolution();

                }

                init();

            });
})();