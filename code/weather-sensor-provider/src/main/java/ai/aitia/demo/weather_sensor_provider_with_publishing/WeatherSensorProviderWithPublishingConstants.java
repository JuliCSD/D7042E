package ai.aitia.demo.weather_sensor_provider_with_publishing;

public class WeatherSensorProviderWithPublishingConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String CREATE_WEATHER_SENSOR_SERVICE_DEFINITION = "create-weather-sensor";
	public static final String GET_WEATHER_SENSOR_SERVICE_DEFINITION = "get-weather-sensor";
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String WEATHER_SENSOR_URI = "/weather-sensor";
	public static final String BY_ID_PATH = "/{id}";
	public static final String PATH_VARIABLE_ID = "id";

	public static final String REQUEST_PARAM_KEY_TEMPERATURE = "request-param-temperature";
	public static final String REQUEST_PARAM_TEMPERATURE = "temperature";

	public static final String REQUEST_PARAM_KEY_HUMIDITY = "request-param-humidity";
	public static final String REQUEST_PARAM_HUMIDITY = "humidity";

	public static final String REQUEST_PARAM_KEY_PRESSURE = "request-param-pressure";
	public static final String REQUEST_PARAM_PRESSURE = "pressure";

	public static final String REQUEST_PARAM_KEY_WIND = "request-param-wind";
	public static final String REQUEST_PARAM_WIND = "wind";
	
	public static final String SERVICE_LIMIT="service_limit";
	public static final int DEFAULT_SERVICE_LIMIT=200;
	public static final String $SERVICE_LIMIT_WD="${"+SERVICE_LIMIT+":"+DEFAULT_SERVICE_LIMIT+"}";

	public static final String UPDATE_WEATHER_SENSOR_SERVICE_DEFINITION = "update-weather-sensor";
	public static final String UPDATE_ALL_PATH = "update-all";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private WeatherSensorProviderWithPublishingConstants() {
		throw new UnsupportedOperationException();
	}
}
