package ai.aitia.demo.sensor.measurements.common;


public class EFCommonConstants {

	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String SENSOR_MEASUREMENTS_SERVICE = "sensor-measurements-details";
	public static final String SENSOR_MEASUREMENTS_SERVICE_URI = "/sensor";
	
	public static final String WEATHER_SENSOR_DETAILS_SERVICE = "weather-sensor-details";
	public static final String WEATHER_SENSOR_DETAILS_SERVICE_URI = "/weather";
	
	public static final String LIGHT_SENSOR_DETAILS_SERVICE = "light-sensor-details";
	public static final String LIGHT_SENSOR_DETAILS_SERVICE_URI = "/light";
	
	public static final String REQUEST_PARAM_KEY_BUILDING = "request-param-building";
	public static final String REQUEST_PARAM_BUILDING = "building";
	public static final String REQUEST_PARAM_KEY_FROM = "request-param-from";
	public static final String REQUEST_PARAM_FROM = "from";
	public static final String REQUEST_PARAM_KEY_TO = "request-param-to";
	public static final String REQUEST_PARAM_TO = "to";
	public static final String REQUEST_PARAM_KEY_TIMESTAMP = "request-param-timestamp";
	public static final String REQUEST_PARAM_TIMESTAMP = "timestamp";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-XML";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-XML";
	public static final String HTTP_METHOD = "http-method";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private EFCommonConstants() {
		throw new UnsupportedOperationException();
	}
}
