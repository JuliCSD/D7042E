package ai.aitia.demo.controller_with_subscribing;

public class LightSensorConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_LIGHT_SENSOR_SERVICE_DEFINITION = "create-light-sensor";
	public static final String UPDATE_LIGHT_SENSOR_SERVICE_DEFINITION = "update-light-sensor";
	public static final String GET_LIGHT_SENSOR_SERVICE_DEFINITION = "get-light-sensor";
	public static final String REQUEST_PARAM_KEY_NAME = "request-param-name";
	public static final String REQUEST_PARAM_KEY_VALUE = "request-param-value";
	public static final String $REORCHESTRATION_WD = "${reorchestration:false}";
	public static final String $MAX_RETRY_WD = "${max_retry:300}";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LightSensorConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
