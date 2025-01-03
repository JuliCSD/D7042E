package ai.aitia.demo.lamp_provider;

public class LampProviderConstants {
	
	//=================================================================================================
	// members
	
	public static final String BASE_PACKAGE = "ai.aitia";
	
	public static final String CREATE_LAMP_SERVICE_DEFINITION = "create-lamp";
	public static final String GET_LAMP_SERVICE_DEFINITION = "get-lamp";
	public static final String INTERFACE_SECURE = "HTTP-SECURE-JSON";
	public static final String INTERFACE_INSECURE = "HTTP-INSECURE-JSON";
	public static final String HTTP_METHOD = "http-method";
	public static final String LAMP_URI = "/lamp";
	public static final String BY_ID_PATH = "/{id}";
	public static final String PATH_VARIABLE_ID = "id";
	public static final String REQUEST_PARAM_KEY_STATUS = "request-param-status";
	public static final String REQUEST_PARAM_STATUS = "status";	
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private LampProviderConstants() {
		throw new UnsupportedOperationException();
	}
}
