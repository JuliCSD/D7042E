package ai.aitia.demo.smart_city_common.dto;

import java.io.Serializable;

public class WeatherSensorResponseDTO implements Serializable {

	//=================================================================================================
	// members

	private static final long serialVersionUID = -8371510478751740542L;
	
	private int id;
	private String temperature;
	private String humidity;
	private String pressure;
	private String wind;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public WeatherSensorResponseDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public WeatherSensorResponseDTO(final int id, final String temperature, final String humidity, final String pressure, final String wind) {
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
	public void setId(final int id) {this.id = id; }
	public void setTemperature(final String temperature) { this.temperature = temperature; }
	public void setHumidity(final String humidity) { this.humidity = humidity; }	
	public void setPressure(final String pressure) { this.pressure = pressure; }
	public void setWind(final String wind) { this.wind = wind; }
}
