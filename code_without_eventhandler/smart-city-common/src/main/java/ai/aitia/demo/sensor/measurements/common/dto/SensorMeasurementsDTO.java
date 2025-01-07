package ai.aitia.demo.sensor.measurements.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SensorDetailsDTO")
public class SensorMeasurementsDTO implements Serializable {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -4791068444949469627L;
	
	@JacksonXmlProperty
	private long building;
	
	@JacksonXmlProperty
	private long measurementsTime;
	
	@JacksonXmlProperty
	private double measurementsedTotalHeatConsumptionKWH;
	
	@JacksonXmlProperty
	private double measurementsedWaterHeatConsumptionKWH;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------	
	public SensorMeasurementsDTO() {}

	//-------------------------------------------------------------------------------------------------	
	public SensorMeasurementsDTO(final long building, final long measurementsTime, final double measurementsedTotalHeatConsumptionKWH, final double measurementsedWaterHeatConsumptionKWH) {
		this.building = building;
		this.measurementsTime = measurementsTime;
		this.measurementsedTotalHeatConsumptionKWH = measurementsedTotalHeatConsumptionKWH;
		this.measurementsedWaterHeatConsumptionKWH = measurementsedWaterHeatConsumptionKWH;
	}

	//-------------------------------------------------------------------------------------------------
	public long getBuilding() { return building; }
	public long getMeasurementsTime() { return measurementsTime; }
	public double getMeasurementsedTotalHeatConsumptionKWH() { return measurementsedTotalHeatConsumptionKWH; }
	public double getMeasurementsedWaterHeatConsumptionKWH() { return measurementsedWaterHeatConsumptionKWH; }

	//-------------------------------------------------------------------------------------------------
	public void setBuilding(final long building) { this.building = building; }
	public void setMeasurementsTime(final long measurementsTime) { this.measurementsTime = measurementsTime; }
	public void setMeasurementsedTotalHeatConsumptionKWH(final double measurementsedTotalHeatConsumptionKWH) { this.measurementsedTotalHeatConsumptionKWH = measurementsedTotalHeatConsumptionKWH; }
	public void setMeasurementsedWaterHeatConsumptionKWH(final double measurementsedWaterHeatConsumptionKWH) { this.measurementsedWaterHeatConsumptionKWH = measurementsedWaterHeatConsumptionKWH; }	
}
