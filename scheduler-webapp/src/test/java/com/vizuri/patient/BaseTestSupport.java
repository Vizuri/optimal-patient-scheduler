package com.vizuri.patient;

import java.lang.reflect.Field;
import java.util.TimeZone;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public abstract class BaseTestSupport {

	private static final Logger logger = LoggerFactory.getLogger(BaseTestSupport.class);
	private  static Weld weld;
	private  static WeldContainer weldContainer;
	
	protected ObjectMapper objectMapper = new ObjectMapper(){{
		
	configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		// To change default date format
		// DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		// objectMapper.setDateFormat(df);
		// OR anotate field like this
		// @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
		// private Date date;
		
		// java to json : disable converting a date to an integer timestamp
		disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		// set the default time zone
		setTimeZone(TimeZone.getTimeZone("EST"));
	}};
	
	
	
	public void injectIntoBean(Object target, Object beanToInject,Class clazz){
		
		
		Field [] fields = target.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(field.getType().equals(clazz)){
				field.setAccessible(true);
				try {
					field.set(target, beanToInject);
				} catch (IllegalArgumentException e) {
				
					logger.error("",e);
				} catch (IllegalAccessException e) {
					logger.error("",e);
				}
				break;
			}
		}
	}
	
	@BeforeClass
	public static void doExpensiveSetup() throws Exception{
		logger.info("doExpensiveSetup");
		
		/**
		 * Setting up standalone CDI weld container
		 */
		weld = new Weld();
		
		weldContainer = weld.initialize();
		RequestContext requestContext= weldContainer.instance().select(RequestContext.class, UnboundLiteral.INSTANCE).get();
        requestContext.activate();
	}
	
	@AfterClass
	public static void cleanExpensiveSetup(){
		logger.info("cleanExpensiveSetup");
		
		weld.shutdown();

		
	

	}
	
	/**
	 * Resolving bean from CDI container, all the beans are dependency injected
	 */
	public <T> T getBean(Class<T> clazz) {
		
		T ret = weldContainer.instance().select(clazz).get();
		//injectEntityManager(ret, emf.createEntityManager());
		return (T) ret;
	}


	
}
