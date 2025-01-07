package ai.aitia.demo.sensor.measurements.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

import ai.aitia.arrowhead.application.library.util.ApplicationCommonConstants;

@Component
public class EFDataService {
	
	//=================================================================================================
	// members
	
	private static final String WEATHER_TEMPERATURE_DATA_CSV = "weather_temperature_data.csv";
	/**
     *  Weather temperature data from:
     *
     *  PV systems, Department of Sensor Technology, Aalborg University
     *  https://www.et.aau.dk/research-programmes/photovoltaic-systems/
     */
	private List<Double> weatherTemperatureData;
	private final double lightTemperatureData = 22.0d;
	
	private static final double L = 20.0d;
    private static final double H = 2.0d;
    private static final double W = 10.0d;
    private static final double U_BRICK = 2.6d;
    private static final double U_CONCRETE = 0.7d;
    private static final double U_ABESTOS = 5.7d;
    private static final double MATERIAL = 2.0d * L * H * U_BRICK + 2.0d * W * H * U_BRICK + L * W * U_CONCRETE + L * W * U_ABESTOS;
    private static final double VENTILATION = 1.2d;
    private static final double SCALE = 2.0d * 1000.0d;
    
    @Value(ApplicationCommonConstants.$APPLICATION_SYSTEM_NAME)
	private String mySystemName;
	
    //=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		if (mySystemName.contains("light") || mySystemName.contains("weather")) {
			readWeatherTemperatureData();		
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getWeatherTemperature(final LocalDateTime timestamp) {
		final LocalDateTime yearStart = LocalDateTime.of(timestamp.getYear(), 1, 1, 0, 0);
		final int hourOfYear = (int) yearStart.until(timestamp, ChronoUnit.HOURS);
		return weatherTemperatureData.get(Math.min(hourOfYear, weatherTemperatureData.size() - 1)); //due to leap years
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getLightTemperature(final LocalDateTime timestamp) {
		return Math.max(lightTemperatureData, getWeatherTemperature(timestamp));
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getHeatLoss(final LocalDateTime timestamp) {
        return Math.max(0, VENTILATION * (getLightTemperature(timestamp) - getWeatherTemperature(timestamp)) * MATERIAL / SCALE);
    }	
	
	//-------------------------------------------------------------------------------------------------
	public double getWaterHeat(final LocalDateTime timestamp) {
        return -0.01d * getWeatherTemperature(timestamp) + 0.3d;
    }
	
	//-------------------------------------------------------------------------------------------------
	public double getTotalHeat(final LocalDateTime timestamp) {
		return getWaterHeat(timestamp) + getHeatLoss(timestamp);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void readWeatherTemperatureData() throws IOException, URISyntaxException {
		final Resource resource = new ClassPathResource(WEATHER_TEMPERATURE_DATA_CSV);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
		final CSVReader csvReader = new CSVReader(reader);
		try {
			final List<String[]> readAll = csvReader.readAll();
			weatherTemperatureData = new ArrayList<Double>(readAll.size());
			for (final String[] strings : readAll) {
				weatherTemperatureData.add(Double.valueOf(strings[0]));
			}			
		} catch (final Exception ex) {
			throw ex;
		} finally {
			reader.close();
			csvReader.close();			
		}
	}
}