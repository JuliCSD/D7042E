package ai.aitia.demo.sensor_provider_with_publishing;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.arrowhead.application.library.config.ApplicationInitListener;
import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;
// import ai.aitia.demo.sensor_provider_with_publishing.database.InMemorySensorDB;
// import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;
import eu.arrowhead.application.skeleton.provider.security.ProviderSecurityConfig;
import eu.arrowhead.application.skeleton.publisher.constants.PublisherConstants;
import eu.arrowhead.application.skeleton.publisher.event.PresetEventType;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
// import eu.arrowhead.common.core.CoreSystemService;
import eu.arrowhead.common.dto.shared.EventPublishRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;

// import org.springframework.web.util.UriComponents;
// import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SensorProviderWithPublishingApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;

	// @Autowired
	// private InMemorySensorDB sensorDB;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;
	
	@Value(ApplicationCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
	private String mySystemName;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_ADDRESS_WD)
	private String mySystemAddress;
	
	@Value(ApplicationCommonConstants.$APPLICATION_SERVER_PORT_WD)
	private int mySystemPort;
	
	private final Logger logger = LogManager.getLogger(SensorProviderWithPublishingApplicationInitListener.class);

	
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {
		checkConfiguration();
		
		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICEREGISTRY);
		if (sslEnabled && tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);
			
			setTokenSecurityFilter();
		} else {
			logger.info("TokenSecurityFilter in not active");
		}		
		
		//Register services into ServiceRegistry
		final ServiceRegistryRequestDTO updateSensorsServiceRequest = createServiceRegistryRequest(SensorProviderWithPublishingConstants.CREATE_SENSOR_SERVICE_DEFINITION, SensorProviderWithPublishingConstants.SENSOR_URI, HttpMethod.POST);		
		arrowheadService.forceRegisterServiceToServiceRegistry(updateSensorsServiceRequest);
		
		final ServiceRegistryRequestDTO updateSensorServiceRequest = createServiceRegistryRequest(SensorProviderWithPublishingConstants.UPDATE_SENSOR_SERVICE_DEFINITION, SensorProviderWithPublishingConstants.SENSOR_URI, HttpMethod.POST);		
		arrowheadService.forceRegisterServiceToServiceRegistry(updateSensorServiceRequest);

		final ServiceRegistryRequestDTO getSensorServiceRequest = createServiceRegistryRequest(SensorProviderWithPublishingConstants.GET_SENSOR_SERVICE_DEFINITION,  SensorProviderWithPublishingConstants.SENSOR_URI, HttpMethod.GET);
		getSensorServiceRequest.getMetadata().put(SensorProviderWithPublishingConstants.REQUEST_PARAM_KEY_NAME, SensorProviderWithPublishingConstants.REQUEST_PARAM_NAME);
		getSensorServiceRequest.getMetadata().put(SensorProviderWithPublishingConstants.REQUEST_PARAM_KEY_VALUE, SensorProviderWithPublishingConstants.REQUEST_PARAM_VALUE);
		arrowheadService.forceRegisterServiceToServiceRegistry(getSensorServiceRequest);
		
		if (arrowheadService.echoCoreSystem(CoreSystem.EVENTHANDLER)) {
			arrowheadService.updateCoreServiceURIs(CoreSystem.EVENTHANDLER);
			
			// registerEventType("SENSOR_LIST_UPDATED");
		}
	}

	

// private void registerEventType(String eventTypeName) {
//     logger.info("Registering event type: {}", eventTypeName);

//     // Construye el endpoint completo del EventHandler
// 	String eventHandlerAddress = (String)arrowheadService.getCoreServiceUri(CoreSystemService.EVENT_PUBLISH_SERVICE).toString();
//     if (eventHandlerAddress == null || eventHandlerAddress.isEmpty()) {
//         throw new IllegalStateException("EventHandler core service URI is not available.");
//     }

//     UriComponents registerEventTypeUri = UriComponentsBuilder.fromHttpUrl(eventHandlerAddress)
//                                                              .path("/event-type")
//                                                              .build();

//     // Define el cuerpo de la solicitud para registrar el tipo de evento
//     Map<String, String> requestBody = Map.of("eventTypeName", eventTypeName);

//     try {
//         // Llama al EventHandler para registrar el evento
// 		arrowheadService.consumeServiceHTTP(
// 			Void.class,  // Tipo de respuesta esperado
// 			HttpMethod.POST,
// 			registerEventTypeUri,
// 			"HTTP-SECURE-JSON",  // Interfaces (puedes usar "HTTP-SECURE-JSON" si estás en modo seguro)
// 			requestBody
// 		);
//         logger.info("Event type '{}' registered successfully.", eventTypeName);
//     } catch (Exception e) {
//         logger.error("Failed to register event type '{}'. Error: {}", eventTypeName, e.getMessage());
//         throw new RuntimeException("Failed to register event type: " + eventTypeName, e);
//     }
// }
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		//Unregister service
		publishDestroyedEvent();
		arrowheadService.unregisterServiceFromServiceRegistry(SensorProviderWithPublishingConstants.CREATE_SENSOR_SERVICE_DEFINITION, SensorProviderWithPublishingConstants.SENSOR_URI);
		arrowheadService.unregisterServiceFromServiceRegistry(SensorProviderWithPublishingConstants.GET_SENSOR_SERVICE_DEFINITION, SensorProviderWithPublishingConstants.SENSOR_URI);
		arrowheadService.unregisterServiceFromServiceRegistry(SensorProviderWithPublishingConstants.UPDATE_SENSOR_SERVICE_DEFINITION, SensorProviderWithPublishingConstants.SENSOR_URI);

	}


	// @Scheduled(fixedRate = 30000) // Cada 30 segundos
    // public void publishCarList() { 
    //     List<Sensor> sensorList = sensorDB.getAll();

    //     // Crear el evento
    //     EventPublishRequestDTO publishRequest = createSensorListEvent(sensorList);

    //     // Publicar al EventHandler
    //     arrowheadService.publishToEventHandler(publishRequest);
    //     System.out.println("Published car list to EventHandler.");
    // }
	
	// private EventPublishRequestDTO createSensorListEvent(List<Sensor> sensorList) {
    //     // Define el tipo de evento
    //     final String eventType = "SENSOR_LIST_UPDATED";

    //     // Define el sistema que publica
    //     final SystemRequestDTO source = new SystemRequestDTO();
    //     source.setSystemName(mySystemName);
    //     source.setAddress(mySystemAddress);
    //     source.setPort(mySystemPort);
    //     if (sslEnabled) {
    //         source.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
    //     }

    //     // Metadata y payload
    //     final Map<String, String> metadata = new HashMap<>();
	// 	final String sensorListJson = Utilities.toJson(sensorList);
	// 	final String payload = Utilities.toPrettyJson(sensorListJson);

    //     // Timestamp
    //     final String timestamp = Utilities.convertZonedDateTimeToUTCString(ZonedDateTime.now());

    //     return new EventPublishRequestDTO(eventType, source, metadata, payload, timestamp);
    // }
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private void checkConfiguration() {
		if (!sslEnabled && tokenSecurityFilterEnabled) {			 
			logger.warn("Contradictory configuration:");
			logger.warn("token.security.filter.enabled=true while server.ssl.enabled=true");
		}
	}

	//-------------------------------------------------------------------------------------------------
	private void publishDestroyedEvent() {
		final String eventType = PresetEventType.PUBLISHER_DESTROYED.getEventTypeName();
		
		final SystemRequestDTO source = new SystemRequestDTO();
		source.setSystemName(mySystemName);
		source.setAddress(mySystemAddress);
		source.setPort(mySystemPort);
		if (sslEnabled) {
			source.setAuthenticationInfo(Base64.getEncoder().encodeToString( arrowheadService.getMyPublicKey().getEncoded()));
		}

		final Map<String,String> metadata = null;
		final String payload = PublisherConstants.PUBLISHR_DESTROYED_EVENT_PAYLOAD;
		final String timeStamp = Utilities.convertZonedDateTimeToUTCString( ZonedDateTime.now() );
		
		final EventPublishRequestDTO publishRequestDTO = new EventPublishRequestDTO(
				eventType, 
				source, 
				metadata, 
				payload, 
				timeStamp);
		
		arrowheadService.publishToEventHandler(publishRequestDTO);
	}

	//-------------------------------------------------------------------------------------------------
	private void setTokenSecurityFilter() {
		final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
		if (authorizationPublicKey == null) {
			throw new ArrowheadException("Authorization public key is null");
		}
		
		KeyStore keystore;
		try {
			keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
			keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
		} catch (final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
			throw new ArrowheadException(ex.getMessage());
		}			
		final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());
		
		providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
		providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);		

		if (sslEnabled && tokenSecurityFilterEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN.name());
			serviceRegistryRequest.setInterfaces(List.of(SensorProviderWithPublishingConstants.INTERFACE_SECURE));
		} else if (sslEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE.name());
			serviceRegistryRequest.setInterfaces(List.of(SensorProviderWithPublishingConstants.INTERFACE_SECURE));
		} else {
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE.name());
			serviceRegistryRequest.setInterfaces(List.of(SensorProviderWithPublishingConstants.INTERFACE_INSECURE));
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put(SensorProviderWithPublishingConstants.HTTP_METHOD, httpMethod.name());
		return serviceRegistryRequest;
	}
}