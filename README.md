Optimal Patient Scheduling with Business Resource Planner
=========================================

This POC application was created to provide a demonstrable playground to evaluate implementing a scheduling solution using the Red Hat Business Resource Planner (aka OptaPlanner)

High level goals include:

* Flexible integration with other systems
* Handle many "edge cases" (business rules)
* Visually represent what the scheduler is doing

### Flexible Integration
The POC uses a simple domain model, based on FHIR concepts and vocabulary.  The scheduling actions themselves are exposed through a simple ReST interface, making integration with disparate systems easy and without any leaky abstractions.

### Many Edge Cases
The solution leverages the Drools Business Rules Engine to easily add many business rules (listed below)

### Visual Representation of Scheduling
An interactive Single Page Web-app is included to run through scheduling scenarios, that can poll the scheduler and show in real-time what it is doing behind the scenes.

Use Case
--------
### Vocabulary

A simplified vocabulary was constructed that is based on FHIR concepts to be used in the scheduler (in the com.vizuri.patient.scheduler.model package):

* **Appointment** : concept of a meeting for a patient
  * e.g. a treatment schedule would have a single appointment in concept, but many (recurring) instances
* **Encounter** : instance of a meeting that includes the location, time and other participants
  * We created extensions to this to differentiate Treatment and Consultation encounters
  * Base class includes helpers to determine overlaps and gaps
* **Physician**
* **Patient** : Tied to particular clinic for this POC
* **Clinics**
* **ConsultingRoom**

### Seeding Data

The scheduler finds available appointment times for patients who need an ad-hoc consultation (lasting 45 minutes).  In order to schedule this, the scheduler will find the appropriate room, physician and time slot for the consultation.  The system can be seeded for the following parameters:

* Number of clinics
* Number of rooms per clinic
* Number of patients per clinics
  * Evenly distributes Treatment schedules
    * Monday-Wednesday-Friday vs. Tuesday-Thursday-Saturday
    * Shift 1 (8am - 12pm) vs. Shift 2 (12pm - 4pm) vs. Shift 3 (4pm - 8pm)
* Number of physicians available
* How many weeks in the future to consider scheduling

### Scheduling Scenarios
Once the system is scheduled, the 2 main scheduling events can be triggered:

1. Create a batch of consultation appointments : can request a percentage of patients to schedule
2. Request 10 options for a particular patient to schedule a consultation

The system will fill in the appropriate options for room, time and physician based on what is available and what "score" that combination deserves based on the business rules.  The score is broken into "hard", "medium" and "soft" scores, where the highest score is desired where zero is the highest possible score.  Any hard constraints violated means the solution is not considered feasible.

### Business Rules
The business rules are expressed in the file consultationScoreRules.drl that demonstrate the following scoring rules:

* **Hard**
  * Conflicts
    * Physicians, Patients and rooms can only be assigned to a single encounter for a particular time block
  * Encounters must occur during regular business hours
  * Encounters must include a physician
    * Special case, since this is an over-constraining type problem (we may not have the staff to fulfill a proposed solution)
* **Medium**
  * Consultations should occur on the same day as a treatment
  * Consultations should occur within one hour of treatment
  * Physicians should be assigned no more than 2 days a week
* **Soft**
  * Scheduling should favor using the same physician to cover a ConsultingRoom on the same day
  * Consultations should be scheduled sooner rather than later


# Building and Running
Build entire project with `mvn clean install`

Deploy to a locally running JBoss EAP7 standalone instance with `mvn -pl scheduler-webapp wildfly:deploy`

Open web app at [http://localhost:8080/scheduler-webapp]
