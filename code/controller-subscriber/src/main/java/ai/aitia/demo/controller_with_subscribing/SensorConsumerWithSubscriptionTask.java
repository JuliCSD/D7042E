package ai.aitia.demo.controller_with_subscribing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
import ai.aitia.demo.smart_city_common.dto.LightSensorResponseDTO;
import ai.aitia.demo.smart_city_common.dto.WeatherSensorResponseDTO;
import ai.aitia.demo.controller_with_subscribing.database.InMemoryLampDB;
import ai.aitia.demo.controller_with_subscribing.entity.Lamp;
import eu.arrowhead.application.skeleton.subscriber.SubscriberUtilities;
import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberConstants;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.dto.shared.SubscriptionRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.dto.shared.SystemResponseDTO;
import eu.arrowhead.common.exception.InvalidParameterException;


public class SensorConsumerWithSubscriptionTask extends Thread {
	//=================================================================================================
	// members
	
	private boolean interrupted = false;
	
	private final Logger logger = LogManager.getLogger( SensorConsumerWithSubscriptionTask.class);
	
	@Resource( name = SubscriberConstants.NOTIFICATION_QUEUE )
	private ConcurrentLinkedQueue<EventDTO> notificatonQueue;
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
	
	@Value(ApplicationCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
	private String applicationSystemName;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_ADDRESS_WD)
	private String applicationSystemAddress;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_PORT_WD)
	private int applicationSystemPort;
	
	@Value(SensorConsumerConstants.$REORCHESTRATION_WD)
	private boolean reorchestration;
	
	@Value(SensorConsumerConstants.$MAX_RETRY_WD)
	private int max_retry;

	@Autowired
	private InMemoryLampDB lampDB;

	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@Override
	public void run() {
		logger.info("ConsumerTask.run started...");
		
		interrupted = Thread.currentThread().isInterrupted();

		OrchestrationResultDTO lightSensorRequestingService = null;
		OrchestrationResultDTO weatherSensorRequestingService = null;
		
		int counter = 0;
		while (!interrupted && (counter < max_retry)) {
			try {
				if (notificatonQueue.peek() != null) {
					for (final EventDTO event : notificatonQueue) {
						if (SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE.equalsIgnoreCase(event.getEventType())) {
							if (reorchestration) {
								logger.info("Recieved publisher destroyed event - started reorchestration.");
								
								lightSensorRequestingService = orchestrateGetLightSensorService();
								weatherSensorRequestingService = orchestrateGetWeatherSensorService();	

							} else {
								logger.info("Recieved publisher destroyed event - started shuting down.");
								System.exit(0);
							}
						} else {
							logger.info("ConsumerTask recevied event - with type: " + event.getEventType() + ", and payload: " + event.getPayload() + ".");
						}
					}
					
					notificatonQueue.clear();
				}

				if (lightSensorRequestingService != null && weatherSensorRequestingService != null) {
					List<LightSensorResponseDTO> allLightSensor = callLightSensorRequestingService(lightSensorRequestingService);
					List<WeatherSensorResponseDTO> allWeatherSensor = callWeatherSensorRequestingService(weatherSensorRequestingService);
					System.out.println("LampDB updated : "+updateLampStatus(allLightSensor, allWeatherSensor));


				} else {
					counter++;
					
					lightSensorRequestingService = orchestrateGetLightSensorService();
					weatherSensorRequestingService = orchestrateGetWeatherSensorService();
					
					if (weatherSensorRequestingService != null) {//lightSensorRequestingService != null && weatherSensorRequestingService != null) {
						counter = 0;
						
						final Set<SystemResponseDTO> sources = new HashSet<SystemResponseDTO>();
						
						sources.add(lightSensorRequestingService.getProvider());
						sources.add(weatherSensorRequestingService.getProvider());
						
						subscribeToDestoryEvents(sources);
					}
				}
			} catch (final Throwable ex) {
				logger.debug(ex.getMessage());
				
				lightSensorRequestingService = null;
				weatherSensorRequestingService = null;
			}	


			System.out.println("counter: " + counter);
			

			try {
				Thread.sleep(10000);
			} catch (final InterruptedException ex) {
				logger.debug("ConsumerTask interrupted");
				interrupted = true;
			}
		}
		
		System.exit(0);
	}
	
	//-------------------------------------------------------------------------------------------------	
	public void destroy() {
		logger.debug("ConsumerTask.destroy started...");
		
		interrupted = true;
	}
	
	//=================================================================================================
	//Assistant methods

	
	//-------------------------------------------------------------------------------------------------
	private void subscribeToDestoryEvents(final Set<SystemResponseDTO> providers) {
		final Set<SystemRequestDTO> sources = new HashSet<>(providers.size());
		
		for (final SystemResponseDTO provider : providers) {
			final SystemRequestDTO source = new SystemRequestDTO();
			source.setSystemName(provider.getSystemName());
			source.setAddress(provider.getAddress());
			source.setPort(provider.getPort());
			
			sources.add(source);
		}
		
		final SystemRequestDTO subscriber = new SystemRequestDTO();
		subscriber.setSystemName(applicationSystemName);
		subscriber.setAddress(applicationSystemAddress);
		subscriber.setPort(applicationSystemPort);
		
		if (sslEnabled) {
			subscriber.setAuthenticationInfo(Base64.getEncoder().encodeToString( arrowheadService.getMyPublicKey().getEncoded()));		
		}
		
		try {
			arrowheadService.unsubscribeFromEventHandler(SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE, applicationSystemName, applicationSystemAddress, applicationSystemPort);
		} catch (final Exception ex) {
			logger.debug("Exception happend in subscription initalization " + ex);
		}
		
		try {
			final SubscriptionRequestDTO subscription = SubscriberUtilities.createSubscriptionRequestDTO(SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE, subscriber, SubscriberConstants.PUBLISHER_DESTORYED_NOTIFICATION_URI);
			subscription.setSources(sources);
			
			arrowheadService.subscribeToEventHandler(subscription);
		} catch (final InvalidParameterException ex) {
			
			if (ex.getMessage().contains( "Subscription violates uniqueConstraint rules")) {
				logger.debug("Subscription is already in DB");
			} else {
				logger.debug(ex.getMessage());
				logger.debug(ex);
			}
		} catch (final Exception ex) {
			logger.debug("Could not subscribe to EventType: " + SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE );
		}
	}
	
    
    //-------------------------------------------------------------------------------------------------
    private OrchestrationResultDTO orchestrateGetLightSensorService() {
    	// logger.info("Orchestration request for " + LightSensorConsumerConstants.GET_LIGHT_SENSOR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(SensorConsumerConstants.GET_LIGHT_SENSOR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, false)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		// printOut(orchestrationFormRequest);		
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		
		// logger.info("Orchestration response:");
		// printOut(orchestrationResponse);		
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_LIGHT_SENSOR_SERVICE_DEFINITION);
			
			return orchestrationResult;
		}
		
		return null;
    } 

    //-------------------------------------------------------------------------------------------------
    private OrchestrationResultDTO orchestrateGetWeatherSensorService() {
    	// logger.info("Orchestration request for " + SensorConsumerConstants.GET_WEATHER_SENSOR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(SensorConsumerConstants.GET_WEATHER_SENSOR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, false)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		// printOut(orchestrationFormRequest);		
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		
		// logger.info("Orchestration response:");
		// printOut(orchestrationResponse);		
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_WEATHER_SENSOR_SERVICE_DEFINITION);
			
			return orchestrationResult;
		}
		
		return null;
    }
    
    
    //-------------------------------------------------------------------------------------------------
    private List<LightSensorResponseDTO> callLightSensorRequestingService( final OrchestrationResultDTO orchestrationResult) {
		validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_LIGHT_SENSOR_SERVICE_DEFINITION);
		
		// logger.info("Get all light_sensors:");
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		@SuppressWarnings("unchecked")
		
		final LightSensorResponseDTO[] lightSensorsArray = arrowheadService.consumeServiceHTTP(LightSensorResponseDTO[].class, 
		HttpMethod.valueOf(orchestrationResult.getMetadata().get(SensorConsumerConstants.HTTP_METHOD)),
		orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), 
		orchestrationResult.getServiceUri(), getInterface(), token, null, new String[0]);

		final List<LightSensorResponseDTO> allLightSensor = Arrays.asList(lightSensorsArray);

																				
		// printOut(allLightSensor);
		return allLightSensor;
		
    }

	//-------------------------------------------------------------------------------------------------
    private List<WeatherSensorResponseDTO> callWeatherSensorRequestingService( final OrchestrationResultDTO orchestrationResult) {
		validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_WEATHER_SENSOR_SERVICE_DEFINITION);
		
		// logger.info("Get all sensors:");
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		@SuppressWarnings("unchecked")
		
		final WeatherSensorResponseDTO[] weatherSensorsArray = arrowheadService.consumeServiceHTTP(WeatherSensorResponseDTO[].class, 
		HttpMethod.valueOf(orchestrationResult.getMetadata().get(SensorConsumerConstants.HTTP_METHOD)),
		orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), 
		orchestrationResult.getServiceUri(), getInterface(), token, null, new String[0]);

		final List<WeatherSensorResponseDTO> allWeatherSensor = Arrays.asList(weatherSensorsArray);

																				
		// printOut(allWeatherSensor);
		return allWeatherSensor;
		
    }
  

	//-------------------------------------------------------------------------------------------------
	private boolean updateLampStatus(final List<LightSensorResponseDTO> allLightSensor, final List<WeatherSensorResponseDTO> allWeatherSensor) {

		if (allLightSensor == null) {
			logger.error("allLightSensor is null");
			return false;
		}		
		if (allLightSensor.isEmpty()) {
			logger.info("No light_sensors found.");
			return false;
		}

		int allLightSensorSize = allLightSensor.size();
		boolean updated = false;
		logger.info("Preparing to iterate over allLightSensor: " + allLightSensorSize);

		for(final LightSensorResponseDTO lightSensor : allLightSensor) {
			int lightSensorId = lightSensor.getId();
			int value = 0;
			try {
				value = (int) Double.parseDouble(lightSensor.getValue());
			} catch (NumberFormatException e) {
				logger.error("Invalid lightSensor value: " + lightSensor.getValue());
				continue;
			}

			// logger.info("Processing lightSensor with ID: " + lightSensorId + ", Value: " + value);\
			// System.out.println("lightSensorId % LampProviderConstants.NUMBER_OF_LAMPS = "+ lightSensorId+"%"+LampProviderConstants.NUMBER_OF_LAMPS+" = " + lightSensorId % LampProviderConstants.NUMBER_OF_LAMPS);

			int lampId = (lightSensorId % LampProviderConstants.NUMBER_OF_LAMPS) + 1;
			Lamp lamp = lampDB.getById(lampId);
			if (lamp == null) {
				logger.warn("Lamp with ID " + lampId + " not found.");
				continue;
			}

			int currentStatus = lamp.getStatus();
			int lastRequestStatus = lamp.getlastRequestStatus();

			if(currentStatus == 1 && value < LampProviderConstants.OFF_THRESHOLD) {
				// lampDB.updateById(lampId, 0);
				lampDB.getById(lampId).setStatus(0);

				if(currentStatus != lastRequestStatus) {
					logger.info("Lamp ID " + lampId + " turned OFF.");
				}
				updated = true;

			} else if(currentStatus == 0 && value > LampProviderConstants.ON_THRESHOLD) {
				// lampDB.updateById(lampId, 1);
				lampDB.getById(lampId).setStatus(1);
				if(currentStatus != lastRequestStatus) {
					logger.info("Lamp ID " + lampId + " turned ON.");
				}
				updated = true;
				
			} else {
				// logger.info("Lamp ID " + lampId + " retains its state: " + currentStatus);
			}
			
			updated = true;
		}
		logger.info("Completed iteration over allLightSensor.");
		return updated;
	}

    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? SensorConsumerConstants.INTERFACE_SECURE : SensorConsumerConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinition) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinition)) {
			throw new InvalidParameterException("Requested and orchestrated service definition do not match");
		}
    	
    	boolean hasValidInterface = false;
    	for (final ServiceInterfaceResponseDTO serviceInterface : orchestrationResult.getInterfaces()) {
			if (serviceInterface.getInterfaceName().equalsIgnoreCase(getInterface())) {
				hasValidInterface = true;
				break;
			}
		}
    	if (!hasValidInterface) {
    		throw new InvalidParameterException("Requested and orchestrated interface do not match");
		}
    }
    
    //-------------------------------------------------------------------------------------------------
    private void printOut(final Object object) {
    	System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }
}