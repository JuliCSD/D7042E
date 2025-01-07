package ai.aitia.demo.sensor_consumer_with_subscribing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
// import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
import ai.aitia.demo.smart_city_common.dto.SensorRequestDTO;
import ai.aitia.demo.smart_city_common.dto.SensorResponseDTO;
import ai.aitia.demo.sensor_consumer_with_subscribing.database.InMemoryLampDB;
import ai.aitia.demo.sensor_consumer_with_subscribing.entity.Lamp;
import ai.aitia.demo.smart_city_common.dto.LampRequestDTO;
import ai.aitia.demo.smart_city_common.dto.LampResponseDTO;
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

@Component
public class SensorConsumerWithSubscriptionTask extends Thread {
	//=================================================================================================
	// members

	@Autowired
	private InMemoryLampDB lampDB;
	
	private boolean interrupted = false;
	
	private final Logger logger = LogManager.getLogger(SensorConsumerWithSubscriptionTask.class);
	
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


	// final Semaphore semaphore = new Semaphore(1);


	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@Override
	public void run() {
		logger.info("ConsumerTask.run started...");
	
		interrupted = Thread.currentThread().isInterrupted();
	
		OrchestrationResultDTO sensorRequestingService = null;
	
		int counter = 0;
		while (!interrupted && (counter < max_retry)) {

			// try {
			// 	semaphore.acquire();
			// 	System.out.println("Semaphore acquired");

			// 	List<SensorResponseDTO> allSensor = new ArrayList<SensorResponseDTO>();

				try {
					if (notificatonQueue.peek() != null) {
						for (final EventDTO event : notificatonQueue) {
							if (SubscriberConstants.PUBLISHER_DESTROYED_EVENT_TYPE.equalsIgnoreCase(event.getEventType())) {
								if (reorchestration) {
									logger.info("Recieved publisher destroyed event - started reorchestration.");
		
									sensorRequestingService = orchestrateGetSensorService();
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
		
					if (sensorRequestingService != null) {
						// allSensor =  List.copyOf(callSensorRequestingService(sensorRequestingService));
						// System.out.println("allSensor size before updateLampStatus: " + allSensor.size());
						
					} else {
						counter++;
		
						sensorRequestingService = orchestrateGetSensorService();
		
						if (sensorRequestingService != null) {
							counter = 0;
		
							final Set<SystemResponseDTO> sources = new HashSet<SystemResponseDTO>();
		
							sources.add(sensorRequestingService.getProvider());
		
							subscribeToDestoryEvents(sources);
						}
					}
				} catch (final Throwable ex) {
					logger.debug(ex.getMessage());
		
					sensorRequestingService = null;
				}
		

				// boolean updated = updateLampStatus(allSensor);
				// System.out.println("LampDB updated: " + updated);
				System.out.println("counter: " + counter);
		
				try {
					Thread.sleep(10000);
				} catch (final InterruptedException ex) {
					logger.debug("ConsumerTask interrupted");
			// 		interrupted = true;
			// 	}

			// } catch (InterruptedException e) {
			// 	logger.error("Semaphore acquisition interrupted", e);
			// 	Thread.currentThread().interrupt();
 			// } finally {
			// 	semaphore.release();
			// 	System.out.println("Semaphore released");
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
    private OrchestrationResultDTO orchestrateGetSensorService() {
    	logger.info("Orchestration request for " + SensorConsumerConstants.GET_SENSOR_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(SensorConsumerConstants.GET_SENSOR_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, false)
																					   .flag(Flag.OVERRIDE_STORE, true)
																					   .flag(Flag.PING_PROVIDERS, true)
																					   .build();
		
		printOut(orchestrationFormRequest);		
		
		final OrchestrationResponseDTO orchestrationResponse = arrowheadService.proceedOrchestration(orchestrationFormRequest);
		
		logger.info("Orchestration response:");
		printOut(orchestrationResponse);		
		
		if (orchestrationResponse == null) {
			logger.info("No orchestration response received");
		} else if (orchestrationResponse.getResponse().isEmpty()) {
			logger.info("No provider found during the orchestration");
		} else {
			final OrchestrationResultDTO orchestrationResult = orchestrationResponse.getResponse().get(0);
			validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_SENSOR_SERVICE_DEFINITION);
			
			return orchestrationResult;
		}
		
		return null;
    }
    
	//-------------------------------------------------------------------------------------------------
	private List<SensorResponseDTO> callSensorRequestingService(final OrchestrationResultDTO orchestrationResult) {
		validateOrchestrationResult(orchestrationResult, SensorConsumerConstants.GET_SENSOR_SERVICE_DEFINITION);

		logger.info("Get all sensors:");
		final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
		@SuppressWarnings("unchecked")
		final List<SensorResponseDTO> allSensor = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(SensorConsumerConstants.HTTP_METHOD)),
				orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
				getInterface(), token, null, new String[0]);
		printOut(allSensor);
		System.out.println("allSensor size in callSensorRequestingService: " + allSensor.size());
		return allSensor;
	}

	
	//-------------------------------------------------------------------------------------------------
	private boolean updateLampStatus(final List<SensorResponseDTO> allSensor) {


			if (allSensor == null) {
				logger.error("allSensor is null");
				return false;
			}
			if (allSensor.isEmpty()) {
				logger.info("No sensors found.");
				return false;
			}

			System.out.println("allSensor: " + allSensor.size());
			boolean updated = false;

			for (final SensorResponseDTO sensor : allSensor) {
				logger.info("Sensor ID: " + sensor.getId() + ", Value: " + sensor.getValue());
				System.out.println("Update lamp by id:");
				int sensorId = sensor.getId();
				int value = 0;

				try {
					value = Integer.parseInt(sensor.getValue());
				} catch (NumberFormatException e) {
					logger.error("Invalid sensor value: " + sensor.getValue());
					continue;
				}

				int lampId = (sensorId / 2) + 1;
				if (lampId > 20) {
					lampId = 20;
				}
				Lamp lampToUpdate = lampDB.getById(lampId);
				if (lampToUpdate == null) {
					System.out.println("Lamp with ID " + lampId + " not found.");
					continue;
				}

				if (value < 10) {
					lampToUpdate.setStatus(0);
					System.out.println("lampId: " + lampId + " OFF");
					updated = true;
				} else {
					lampToUpdate.setStatus(1);
					System.out.println("lampId: " + lampId + " ON");
					updated = true;
				}
			}
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