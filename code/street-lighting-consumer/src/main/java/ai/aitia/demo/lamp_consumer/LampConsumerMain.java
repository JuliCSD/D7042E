package ai.aitia.demo.lamp_consumer;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import ai.aitia.arrowhead.application.library.ArrowheadService;
import ai.aitia.demo.smart_city_common.dto.LampRequestDTO;
import ai.aitia.demo.smart_city_common.dto.LampResponseDTO;
import ai.aitia.demo.smart_city_common.dto.LightSensorResponseDTO;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.SSLProperties;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.dto.shared.OrchestrationResponseDTO;
import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.common.dto.shared.ServiceInterfaceResponseDTO;
import eu.arrowhead.common.dto.shared.ServiceQueryFormDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, LampConsumerConstants.BASE_PACKAGE})
public class LampConsumerMain implements ApplicationRunner {
    
    //=================================================================================================
	// members
	
    @Autowired
	private ArrowheadService arrowheadService;
    
    @Autowired
	protected SSLProperties sslProperties;
    
    private final Logger logger = LogManager.getLogger(LampConsumerMain.class);
    
    //=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
    public static void main( final String[] args ) {
    	SpringApplication.run(LampConsumerMain.class, args);
    }

    //-------------------------------------------------------------------------------------------------
    @Override
	public void run(final ApplicationArguments args) throws Exception {
    	// createLampServiceOrchestrationAndConsumption();
    	getLampServiceOrchestrationAndConsumption();
	}
    
    
    //-------------------------------------------------------------------------------------------------
    public void getLampServiceOrchestrationAndConsumption() {
    	logger.info("Orchestration request for " + LampConsumerConstants.GET_LAMP_SERVICE_DEFINITION + " service:");
    	final ServiceQueryFormDTO serviceQueryForm = new ServiceQueryFormDTO.Builder(LampConsumerConstants.GET_LAMP_SERVICE_DEFINITION)
    																		.interfaces(getInterface())
    																		.build();
    	
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();
		final OrchestrationFormRequestDTO orchestrationFormRequest = orchestrationFormBuilder.requestedService(serviceQueryForm)
																					   .flag(Flag.MATCHMAKING, true)
																					   .flag(Flag.OVERRIDE_STORE, true)
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
			validateOrchestrationResult(orchestrationResult, LampConsumerConstants.GET_LAMP_SERVICE_DEFINITION);
			
			logger.info("Get all lamps:");
			final String token = orchestrationResult.getAuthorizationTokens() == null ? null : orchestrationResult.getAuthorizationTokens().get(getInterface());
			@SuppressWarnings("unchecked")
			final LampResponseDTO[] lampsArray = arrowheadService.consumeServiceHTTP(LampResponseDTO[].class, 
																					HttpMethod.valueOf(orchestrationResult.getMetadata().get(LampConsumerConstants.HTTP_METHOD)),
																					orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), 
																					orchestrationResult.getServiceUri(), getInterface(), token, null, new String[0]);
			final List<LampResponseDTO> allLamps = Arrays.asList(lampsArray);
			turnOnOff(allLamps);
			
			// logger.info("Get only ON lamps:");
			final String[] queryParamStatus= {orchestrationResult.getMetadata().get(LampConsumerConstants.REQUEST_PARAM_KEY_STATUS), "1"};			
			@SuppressWarnings("unchecked")
			final List<LampResponseDTO> onLamps = arrowheadService.consumeServiceHTTP(List.class, HttpMethod.valueOf(orchestrationResult.getMetadata().get(LampConsumerConstants.HTTP_METHOD)),
																					  orchestrationResult.getProvider().getAddress(), orchestrationResult.getProvider().getPort(), orchestrationResult.getServiceUri(),
																					  getInterface(), token, null, queryParamStatus);
			


			


			printOut(allLamps);
			
		}
    }
    
    //=================================================================================================
	// assistant methods
    
	private void turnOnOff(final List<LampResponseDTO> allLamps) {
		for (final LampResponseDTO lamp : allLamps) {
			int status = lamp.getStatus();
			int id = lamp.getId();

			if(status == 0){
				System.out.println("Turning off lamp with id: " + id);
				continue;
			}
			if(status == 1){
				System.out.println("Turning on lamp with id: " + id);
				continue;
			}
			System.out.println("Error in turning on/off lamp with id: " + id);
		}
		
	}

    //-------------------------------------------------------------------------------------------------
    private String getInterface() {
    	return sslProperties.isSslEnabled() ? LampConsumerConstants.INTERFACE_SECURE : LampConsumerConstants.INTERFACE_INSECURE;
    }
    
    //-------------------------------------------------------------------------------------------------
    private void validateOrchestrationResult(final OrchestrationResultDTO orchestrationResult, final String serviceDefinitin) {
    	if (!orchestrationResult.getService().getServiceDefinition().equalsIgnoreCase(serviceDefinitin)) {
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
