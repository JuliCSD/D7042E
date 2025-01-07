package ai.aitia.demo.sensor_consumer_with_subscribing;

public class SensorConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_SENSOR_SERVICE_DEFINITION = "create-sensor";
	public static final String UPDATE_SENSOR_SERVICE_DEFINITION = "update-sensor";
	public static final String GET_SENSOR_SERVICE_DEFINITION = "get-sensor";
	public static final String REQUEST_PARAM_KEY_NAME = "request-param-name";
	public static final String REQUEST_PARAM_KEY_VALUE = "request-param-value";
	public static final String $REORCHESTRATION_WD = "${reorchestration:false}";
	public static final String $MAX_RETRY_WD = "${max_retry:300}";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private SensorConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
