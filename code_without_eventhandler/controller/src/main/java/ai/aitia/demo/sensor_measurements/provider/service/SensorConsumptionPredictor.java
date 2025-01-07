package ai.aitia.demo.sensor_measurements.provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsDTO;
import ai.aitia.demo.sensor.measurements.common.dto.SensorMeasurementsDTO;

public class SensorConsumptionPredictor {

	//=================================================================================================
	// members
	
	private final List<SensorDetailsDTO> dataSet;
	private final long building;
	private final LocalDateTime measurementsedTimestamp;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public SensorConsumptionPredictor(final List<SensorDetailsDTO> dataSet,  final long building, final long measurementsedTimestamp) {
		this.dataSet = dataSet;
		this.building = building;
		this.measurementsedTimestamp = convertToLocalDateTime(measurementsedTimestamp);
	}
	
	//-------------------------------------------------------------------------------------------------
	public SensorMeasurementsDTO predict() {
		final double totalHeatUnit = calculateTotalHeatUnit();
		final double waterHeatUnit = calculateWaterHeatUnit();
		
		final List<Double> expectedTotalHeatConsumptions = new ArrayList<>();
		final List<Double> expectedWaterHeatConsumptions = new ArrayList<>();
		LocalDateTime time = LocalDateTime.now();
		while (time.isBefore(measurementsedTimestamp) || time.isEqual(measurementsedTimestamp)) {
			final double expectedLightTemp = calculateExpectedLightTemperature(time);
			final double expectedWeatherTemp = calculateExpectedWeatherTemperature(time);
			final double expectedTempDiff = expectedLightTemp - expectedWeatherTemp;
			if (expectedTempDiff > 0) {
				expectedTotalHeatConsumptions.add(totalHeatUnit * expectedTempDiff);
				expectedWaterHeatConsumptions.add(waterHeatUnit * expectedTempDiff);				
			}
			time = time.plusHours(1);
		}
		
		return new SensorMeasurementsDTO(building, measurementsedTimestamp.toEpochSecond(ZoneOffset.UTC), sum(expectedTotalHeatConsumptions), sum(expectedWaterHeatConsumptions));
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private double calculateExpectedLightTemperature(final LocalDateTime time) {
		final List<Double> lightTemps = new ArrayList<>();
		
		final int hourOfDay = time.getHour();
		final LocalDateTime scope = measurementsedTimestamp.minusDays(10);
		for (int i = dataSet.size() - 1; convertToLocalDateTime(dataSet.get(i).getTimestamp()).isAfter(scope); --i) {
			if (convertToLocalDateTime(dataSet.get(i).getTimestamp()).getHour() == hourOfDay) {
				lightTemps.add(dataSet.get(i).getInTemp());
			}
		}
		
		return calculateAverage(lightTemps);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateExpectedWeatherTemperature(final LocalDateTime time) {
		final List<Double> weatherTemps = new ArrayList<>();
		
		final int hourOfDay = time.getHour();
		final LocalDateTime scope = measurementsedTimestamp.minusDays(5);
		for (int i = dataSet.size() - 1; convertToLocalDateTime(dataSet.get(i).getTimestamp()).isAfter(scope); --i) {
			if (convertToLocalDateTime(dataSet.get(i).getTimestamp()).getHour() == hourOfDay) {
				weatherTemps.add(dataSet.get(i).getOutTemp());
			}
		}
		
		return calculateAverage(weatherTemps);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateTotalHeatUnit() {
		final List<Double> totalHeatUnits = new ArrayList<>();
		for (final SensorDetailsDTO record : dataSet) {
			final double inTemp = record.getInTemp();
			final double outTemp = record.getOutTemp();
			final double tempDiff = Math.max(0d, inTemp - outTemp);
			totalHeatUnits.add(tempDiff > 0 ? record.getTotal() / tempDiff : 0d);
		}
		return calculateAverage(totalHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateWaterHeatUnit() {
		final List<Double> waterHeatUnits = new ArrayList<>();
		for (final SensorDetailsDTO record : dataSet) {
			final double inTemp = record.getInTemp();
			final double outTemp = record.getOutTemp();
			final double tempDiff = Math.max(0d, inTemp - outTemp);
			waterHeatUnits.add(tempDiff > 0 ? record.getWater() / tempDiff : 0d);
		}
		return calculateAverage(waterHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private LocalDateTime convertToLocalDateTime(final long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateAverage(final List<Double> temperatues) {
		double sum = 0;
		for (final Double temp : temperatues) {
			sum += temp;
		}
		return sum / temperatues.size();
	}
	
	//-------------------------------------------------------------------------------------------------
	private double sum(final List<Double> temperatues) {
		double sum = 0.0d;
		for (final Double temp : temperatues) {
			sum += temp;
		}
		return sum;
	}
}
