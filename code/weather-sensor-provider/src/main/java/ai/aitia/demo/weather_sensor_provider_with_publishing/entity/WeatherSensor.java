package ai.aitia.demo.weather_sensor_provider_with_publishing.entity;

public class WeatherSensor {

	//=================================================================================================
	// members

	private final int id;
	private String temperature;
	private String humidity;
	private String pressure;
	private String wind;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public WeatherSensor(final int id, final String temperature, final String humidity, final String pressure, final String wind) {
		this.id = id;
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
		this.wind = wind;
	}

	//-------------------------------------------------------------------------------------------------
	public int getId() { return id; }
	public String getTemperature() { return temperature; }
	public String getHumidity() { return humidity; }
	public String getPressure() { return pressure; }
	public String getWind() { return wind; }

	//-------------------------------------------------------------------------------------------------
	public void setTemperature(final String temperature) { this.temperature = temperature; }
	public void setHumidity(final String humidity) { this.humidity = humidity; }	
	public void setPressure(final String pressure) { this.pressure = pressure; }
	public void setWind(final String wind) { this.wind = wind; }
}
