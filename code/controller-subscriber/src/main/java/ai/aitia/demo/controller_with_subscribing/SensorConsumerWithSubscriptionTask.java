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
								logger.info("Received publisher destroyed event - started reorchestration.");
								
								lightSensorRequestingService = orchestrateGetLightSensorService();
								weatherSensorRequestingService = orchestrateGetWeatherSensorService();	

							} else {
								logger.info("Received publisher destroyed event - started shuting down.");
								System.exit(0);
							}
						} else {
							logger.info("ConsumerTask received event - with type: " + event.getEventType() + ", and payload: " + event.getPayload() + ".");
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
					
					if (lightSensorRequestingService != null && weatherSensorRequestingService != null) {
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
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		// printOut(orchestrationFormRequest);		
		long startTime = System.currentTimeMillis();
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		long endTime = System.currentTimeMillis();
		System.out.println("Orchestration time (light): " + (endTime - startTime) + "ms");
		
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
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		// printOut(orchestrationFormRequest);		
		long startTime = System.currentTimeMillis();
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		long endTime = System.currentTimeMillis();
		System.out.println("Orchestration time (weather): " + (endTime - startTime) + "ms");
		
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
		
		// logger.info("Get all light sensors:");
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
		
		// logger.info("Get all weather sensors:");
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

		if (allLightSensor == null || allWeatherSensor == null) {
			logger.error("allLightSensor or allWeatherSensor is null.");
			return false;
		}		
		if (allLightSensor.isEmpty() || allWeatherSensor.isEmpty()) {
			logger.info("allLightSensor or allWeatherSensor is empty.");
			return false;
		}

		// logger.info("Starting iteration over allLightSensor.");
		List <Lamp> lamps = lampDB.getAll();
		if (lamps.isEmpty()) {
			logger.info("No lamps found.");
			return false;
		}
		
		boolean updated = false;
		
		for (Lamp lamp : lamps) {
			// System.out.println("Lamp ID: " + lamp.getId());
			int lampId = lamp.getId();
			boolean turnOn = shouldTurnOnLamp(allLightSensor, allWeatherSensor, lampId);
			// System.out.println(lampId + " should turn on: " + turnOn);
			
			int newStatus = turnOn ? 1 : 0;
			int currentStatus = lamp.getStatus();
			int lastStatus = lamp.getlastRequestStatus();

			if(lastStatus != newStatus) {
				if (turnOn) {
					lamp.setStatus(1);
					lamp.setSendToLamp(true);
					logger.info("Lamp ID " + lampId + " is turned on.");
					updated = true;
				} else {
					lamp.setStatus(0);
					lamp.setSendToLamp(true);
					logger.info("Lamp ID " + lampId + " is turned off.");
					updated = true;
				}
			} else {
				logger.info("Lamp ID " + lampId + " retains its state: " + currentStatus);
			}
		}
		// logger.info("Completed iteration over allLightSensor.");
		return updated;
	}

	private boolean shouldTurnOnLamp(final List<LightSensorResponseDTO> allLightSensor, final List<WeatherSensorResponseDTO> allWeatherSensor, final int lampId) {

		// System.out.println("Lamp ID: " + lampId);
		// double allLightSensorSize = allLightSensor.size();
		// double allWeatherSensorSize = allWeatherSensor.size();

		// System.out.println("allLightSensorSize: " + allLightSensorSize);

		List<LightSensorResponseDTO> lightSensors = new ArrayList<>();
		for (LightSensorResponseDTO sensor : allLightSensor) {
			if ( (sensor.getId()% LampProviderConstants.NUMBER_OF_LAMPS + 1) == lampId) {
				lightSensors.add(sensor);
				System.out.println("Light sensor ID: " + sensor.getId()+ " Value: " + sensor.getValue());
			}
		}
		List<WeatherSensorResponseDTO> weatherSensors = new ArrayList<>();
		for (WeatherSensorResponseDTO weatherSensor : allWeatherSensor) {
			if ( (weatherSensor.getId()% LampProviderConstants.NUMBER_OF_LAMPS) + 1 == lampId) {
				weatherSensors.add(weatherSensor);
				// System.out.println("Weather sensor ID: " + weatherSensor.getId() + " Temperature: " + weatherSensor.getTemperature() + " Humidity: " + weatherSensor.getHumidity() + " Pressure: " + weatherSensor.getPressure() + " Wind: " + weatherSensor.getWind());
			}
		}
		
		Double luminosity = 0.0;
		for(LightSensorResponseDTO sensor : lightSensors) {
			Double value = Double.parseDouble(sensor.getValue());
			luminosity += value;
		}
		double lightSensorsSize = lightSensors.size();
		luminosity = luminosity / lightSensorsSize ;
		if(luminosity < LampProviderConstants.OFF_THRESHOLD) {
			logger.info("Luminosity is below threshold: " + luminosity);
			return true;
		}

		Double temperature = 0.0;
		Double humidity = 0.0;
		Double pressure = 0.0;
		Double wind = 0.0;
		for(WeatherSensorResponseDTO sensor : weatherSensors) {
			temperature += Double.parseDouble(sensor.getTemperature());
			humidity += Double.parseDouble(sensor.getHumidity());
			pressure += Double.parseDouble(sensor.getPressure());
			wind += Double.parseDouble(sensor.getWind());
		}
		double weatherSensorsSize = lightSensors.size();


		temperature = temperature / weatherSensorsSize;
		humidity = humidity / weatherSensorsSize;
		pressure = pressure / weatherSensorsSize;
		wind = wind / weatherSensorsSize;
		int is_extreme_weather = temperature < LampProviderConstants.TEMP_MIN || 
									temperature > LampProviderConstants.TEMP_MAX || 
									humidity > LampProviderConstants.HUMIDITY_MAX || 
									pressure < LampProviderConstants.PRESSURE_MIN || 
									wind > LampProviderConstants.WIND_MAX
									? 1 : 0;

		if(is_extreme_weather == 1) {
			logger.info("Extreme weather conditions detected.");
			if(luminosity > LampProviderConstants.ON_THRESHOLD){
				logger.info("Luminosity is above threshold.");
				return false;
			} else {
				logger.info("Luminosity is below threshold.");
				return true;
			}
		}
		return false;
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