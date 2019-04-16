#### Incident Priority Service

This service maintains the priority of unassigned incidents.

* Implemented with Vert.x, version 3.6.3.redhat-00009 (latest RHOAR 04/19)
* Uses reactive programming with rx-java
* Embedded rules engine to calculate the priority of an incident and the average priority. The rules engine uses a stateful rules session.
* When the process service is unable to assign a responder to an incident, an IncidentAssignmentEvent is sent to the Kafka broker. The incident priority service consumes these events and raises the priority for each failed assignment.
