package ai.aitia.demo.controller_with_subscribing;

public class SensorConsumerConstants {
	
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


	public static final String CREATE_WEATHER_SENSOR_SERVICE_DEFINITION = "create-weather-sensor";
	public static final String UPDATE_WEATHER_SENSOR_SERVICE_DEFINITION = "update-weather-sensor";
	public static final String GET_WEATHER_SENSOR_SERVICE_DEFINITION = "get-weather-sensor";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private SensorConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
