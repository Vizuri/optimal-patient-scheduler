package com.vizuri.patient.service.rest;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vizuri.patient.BaseTestSupport;
import com.vizuri.patient.service.rest.PlannerService;
import com.vizuri.patient.service.rest.ServiceJacksonJsonProvider;

public class PlannerServiceRestTest extends BaseTestSupport {
	private static final Logger logger = LoggerFactory.getLogger(PlannerServiceRestTest.class);

	private Dispatcher dispatcher = null;
	private PlannerService plannerService = null;

	@Before
	public void setUp() {
		// mock rest dispatch
		dispatcher = MockDispatcherFactory.createDispatcher();

		// satellite service create from CDI
		plannerService = getBean(PlannerService.class);

		// Acutal rest resource to be served using satelliteService class
		dispatcher.getRegistry().addSingletonResource(plannerService);

		// Rest Provide class
		dispatcher.getProviderFactory().register(ServiceJacksonJsonProvider.class);

	}



}
