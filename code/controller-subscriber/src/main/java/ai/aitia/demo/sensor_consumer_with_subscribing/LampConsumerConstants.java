package ai.aitia.demo.sensor_consumer_with_subscribing;

public class LampConsumerConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	
	public static final String CREATE_LAMP_SERVICE_DEFINITION = "create-lamp";
	public static final String GET_LAMP_SERVICE_DEFINITION = "get-lamp";
	public static final String REQUEST_PARAM_KEY_STATUS = "request-param-status";
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LampConsumerConstants() {
		throw new UnsupportedOperationException();
	}

}
