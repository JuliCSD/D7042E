package ai.aitia.demo.sensor.measurements.common.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SensorDetailsListDTO")
public class SensorDetailsListDTO implements Serializable {
	
	//=================================================================================================
	// members

	private static final long serialVersionUID = 323321104311683878L;

	private List<SensorDetailsDTO> sensorDetails;
	
	@JacksonXmlProperty
	private long fromTS;
	
	@JacksonXmlProperty
	private long toTS;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public SensorDetailsListDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public SensorDetailsListDTO(final List<SensorDetailsDTO> sensorDetails, final long fromTS, final long toTS) {
		this.sensorDetails = sensorDetails;
		this.fromTS = fromTS;
		this.toTS = toTS;
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<SensorDetailsDTO> getSensorDetails() { return sensorDetails; }
	public long getFromTS() { return fromTS; }
	public long getToTS() { return toTS; }
	
	//-------------------------------------------------------------------------------------------------
	public void setSensorDetails(final List<SensorDetailsDTO> sensorDetails) { this.sensorDetails = sensorDetails; }
	public void setFromTS(final long fromTS) { this.fromTS = fromTS; }
	public void setToTS(final long toTS) { this.toTS = toTS; }	
}
