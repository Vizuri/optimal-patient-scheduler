package com.vizuri.patient.service.rest;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.scheduler.util.PlannerConfig;
import com.vizuri.patient.service.api.SchedulePlanner;
import com.vizuri.patient.service.api.util.SolutionResponse;
import com.vizuri.patient.service.domain.Appointment;
import com.vizuri.patient.service.domain.Location;
import com.vizuri.patient.service.domain.Patient;
import com.vizuri.patient.service.domain.Physician;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/planner")
@Api(value="/planner")
//@Stateless
public class PlannerService {

	private static final Logger log = LoggerFactory.getLogger(PlannerService.class);
	
	@Inject
	private SchedulePlanner planner;
	
	private static String successResponse = "{\"resp\" : \"SUCCESS\"}";
	private static String failureResponse = "{\"resp\" : \"FAIL\"}";
	
	
	@Path("/update/config")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON })
	@POST
	@ApiOperation(value = "Update the planner's configuration", 
	  notes = "Returns a list of ActionMessageResponse",
	  response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Invalid  Request"),
	      @ApiResponse(code = 500, message = "Exception in findActionsMessage") })
	public Response updatePlannerConfig(PlannerConfig config){
			
		log.info("inside updatePlannerConfig");
		
		String response;
		
		try{
		
			planner.seedFacts(config);
		
			response = successResponse;
			return sendResponse(200, response);
			
		} catch (Exception e) {
			log.error("updatePlannerConfig, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in updatePlannerConfig, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
	}
	
	@Path("/clinics/all")
	@Produces({MediaType.APPLICATION_JSON })
	@GET
	public Response getAllClinics(){
		log.info("inside getAllClinics");
		
		try{
			
			List<Location> list = planner.getAllClinics();
		
			return sendResponse(200, list);
			
		} catch (Exception e) {
			log.error("getAllClinics, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllClinics, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/find/clinic/{name}")
	@Produces({MediaType.APPLICATION_JSON })
	@POST
	public Response getClinic(@PathParam("name") String name){
		log.info("inside getClinic for clinic: " + name);
			
		Location clinic = planner.getClinic(name);
		
		return sendResponse(200, clinic);
				
	}
	
	@Path("/physician/all")
	@Produces({MediaType.APPLICATION_JSON })
	@GET
	public Response getAllPhysicians(){
		log.info("inside getAllPhysicians");
		
		try{
			
			List<Physician> list = planner.getAllPhysicians();
		
			return sendResponse(200, list);
			
		} catch (Exception e) {
			log.error("getAllPhysicians, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllPhysicians, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/physician/with/appointnments")
	@Produces({MediaType.APPLICATION_JSON })
	@GET
	public Response getPhysiciansWithAppointments(){
		log.info("inside getPhysiciansWithAppointments");
		
		try{
			
			List<Physician> list = planner.getPhysiciansWithAppointments();
		
			return sendResponse(200, list);
			
		} catch (Exception e) {
			log.error("getAllPhysicians, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllPhysicians, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/patient/all")
	@Produces({MediaType.APPLICATION_JSON })
	@GET
	public Response getAllPatients(){
		log.info("inside getAllPatients");
		
		try{
			
			List<Patient> list = planner.getAllPatients();
		
			return sendResponse(200, list);
			
		} catch (Exception e) {
			log.error("getAllPatients, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllClinics, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/patient/{name}")
	@Produces({MediaType.APPLICATION_JSON })
	@POST
	public Response getPatient(@PathParam("name") String name){	// path parameters are only added to the path if it is a POST, not for GET){
		log.info("inside getPatient, name: " + name);
		
		try{
			
			Patient p = planner.getPatient(name);
		
			return sendResponse(200, p);
			
		} catch (Exception e) {
			log.error("getPatient, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllClinics, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/patient/for_clinic/{name}")
	@Produces({MediaType.APPLICATION_JSON })
	@POST
	public Response getPatientsForClinic(@PathParam("name") String name){	// path parameters are only added to the path if it is a POST, not for GET){
		log.info("inside getPatientsForClinic, name: " + name);
		
		try{
			
			List<Patient> patients = planner.getPatientsForClinic(name);
		
			return sendResponse(200, patients);
			
		} catch (Exception e) {
			log.error("getPatientsForClinic, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAllClinics, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
				
	}
	
	@Path("/get/patients")
	@Produces({MediaType.APPLICATION_JSON })
	@GET
	public Response getPatients(){
		log.info("inside getPatients");
		return sendResponse(200, planner.getAllPatients());
	}
	
	@Path("/find/patient/appointment/{name}")
	@Produces({MediaType.APPLICATION_JSON })
	@POST
	public Response getAvailableAppointments(@PathParam("name") String name){
		log.info("inside getAvailableAppointments for patient: " + name);
			
		try{
			
			List<Appointment> availableAppointments = planner.getPatientAvailableAppointments(name);
			
			return sendResponse(200, availableAppointments);
			
		} catch (Exception e) {
			log.error("getAvailableAppointments, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getAvailableAppointments, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
			
	}
	

	@Path("/find/physician/appointments/{name}")
	@Produces({MediaType.APPLICATION_JSON })
	@POST
	public Response getPhysicianAppointments(@PathParam("name") String name){
		log.info("inside getPhysicianAppointments for physician: " + name);
			
		List<Appointment> availableAppointments = planner.getPhysicianAppointments(name);
		
		return sendResponse(200, availableAppointments);
				
	}
	
	@Path("/schedule/patient/appointment/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	@POST
	public Response schedulePatientAppointment(@PathParam("id") String id){
		log.info("inside schedulePatientAppointment, id: " + id);

		try{
			
			boolean success = planner.acceptPatientAppointment(id);
			
			if (success) {
				return sendResponse(200, successResponse);
			}
			else{
				return sendResponse(500, failureResponse);
				//return sendResponse(500, new ErrorResponse("Unable to schedule Patient Appointment\n"));
			}
					
			
		} catch (Exception e) {
			log.error("schedulePatientAppointment, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in schedulePatientAppointment, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
	
	}
	
	@Path("/schedule/generate")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response generateSchedule(@DefaultValue("unknown") @QueryParam("max") String maxAppointments){
		log.info("inside generateSchedule, maxAppointments: " + maxAppointments);
		planner.generateSchedule(maxAppointments);
		return sendResponse(200, successResponse);
	}
	
	
	@Path("/schedule/generate/terminate")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response terminateEarly(){
		log.info("inside terminateEarly");
		return sendResponse(200, planner.terminateEarly() ? successResponse : failureResponse);
	}
	
	@Path("/schedule/generate/acceptall")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response acceptAll(){
		log.info("inside acceptAll");
		return sendResponse(200, planner.acceptAllDrafts() ? successResponse : failureResponse);
	}
	
	@Path("/schedule/best/solution")
	@Produces({MediaType.APPLICATION_JSON})
	@GET
	public Response getNextBestSolution(){
			
		log.info("inside getNextBestSolution");
		
		try{
			
			SolutionResponse solution = planner.getNextBestSolution();
			
			return sendResponse(200, solution);
		
		} catch (Exception e) {
			log.error("getNextBestSolution, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in getNextBestSolution, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}		
		
	}
	
	@Path("/set_env")
	@GET
	@ApiOperation(value = "Set the deploy environment", 
	  notes = "Sets environment variables",
	  response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 500, message = "Invalid environment varaible"),
	@ApiResponse(code = 500, message = "Exception in setDeploySettings") })
	public Response setEnvironmentVariables(@DefaultValue("unknown") @QueryParam("env") String env) {
		
		try{
		
			log.info("inside setDeploySettings, env: " + env);	
		
			Properties props = System.getProperties();
			props.setProperty("rules.env", env);
		
			System.getProperties().list(System.out);

			return sendResponse(200, props.getProperty("rules.env"));
		
		} catch (Exception e) {
			log.error("setEnvironmentVariables, exception",e);
			return Response.serverError().entity(new ErrorResponse("Exception in setEnvironmentVariables, error: " + e.getMessage() + "\n" + e.getStackTrace())).build();
			
		}
		
	}
	
	private Response sendResponse(int status, Object result){
		
		return Response.status(status).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209601").entity(result).build();
	}
}
