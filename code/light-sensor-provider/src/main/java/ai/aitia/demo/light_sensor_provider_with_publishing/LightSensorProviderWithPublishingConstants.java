package ai.aitia.demo.light_sensor_provider_with_publishing;

public class LightSensorProviderWithPublishingConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String CREATE_LIGHT_SENSOR_SERVICE_DEFINITION = "create-light-sensor";
	public static final String GET_LIGHT_SENSOR_SERVICE_DEFINITION = "get-light-sensor";
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String LIGHT_SENSOR_URI = "/light-sensor";
	public static final String BY_ID_PATH = "/{id}";
	public static final String PATH_VARIABLE_ID = "id";
	public static final String REQUEST_PARAM_KEY_NAME = "request-param-name";
	public static final String REQUEST_PARAM_NAME = "name";
	public static final String REQUEST_PARAM_KEY_VALUE = "request-param-value";
	public static final String REQUEST_PARAM_VALUE = "value";
	
	public static final String SERVICE_LIMIT="service_limit";
	public static final int DEFAULT_SERVICE_LIMIT=200;
	public static final String $SERVICE_LIMIT_WD="${"+SERVICE_LIMIT+":"+DEFAULT_SERVICE_LIMIT+"}";

	public static final String UPDATE_LIGHT_SENSOR_SERVICE_DEFINITION = "update-light-sensor";
	public static final String UPDATE_ALL_PATH = "update-all";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LightSensorProviderWithPublishingConstants() {
		throw new UnsupportedOperationException();
	}
}
