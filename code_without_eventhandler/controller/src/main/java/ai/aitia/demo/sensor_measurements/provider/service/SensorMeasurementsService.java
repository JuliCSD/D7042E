package ai.aitia.demo.sensor_measurements.provider.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import ai.aitia.demo.sensor.measurements.common.EFUtilities;
import ai.aitia.demo.sensor.measurements.common.dto.SensorDetailsDTO;
import ai.aitia.demo.sensor.measurements.common.dto.SensorMeasurementsDTO;

@Component
public class SensorMeasurementsService {

	//=================================================================================================
	// members
	
	private static final String CSV_DATA_SET_NAME_PATTERN = "building_%d_data.csv";
	private static final String DATA_SET_HEADER = "Timestamp,Light,Weather,Sensor Consumption kWh HEAT,SENSOR Consumption kWh Water\n";
	
	@Autowired
	private SensorMeasurementsDriver sensorMeasurementsDriver;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public SensorMeasurementsDTO measurements(final long building, final long measurementsedTimestamp) throws IOException, URISyntaxException {
		System.out.println("measurements initiated");
		
		final List<String[]> dataSet = updateDataSet(building, measurementsedTimestamp);
		return predict(dataSet, building, measurementsedTimestamp);
	}
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	private List<String[]> updateDataSet(final long building, final long measurementsedTimestamp) throws IOException, URISyntaxException {

		System.out.println("updateDataSet initiated");

		final String fileName = String.format("data_model/" + CSV_DATA_SET_NAME_PATTERN, building);
		createIfAbsentCSV(fileName);
		final List<String[]> dataSet = readCSV(fileName);
		
		long lastTimestamp;
		if (dataSet.size() == 1) {
			lastTimestamp = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC);
		} else {
			lastTimestamp = Long.valueOf(dataSet.get(dataSet.size() - 1)[0]);
		}
		
		final List<SensorDetailsDTO> lightNewData = sensorMeasurementsDriver.getLightSensorDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getSensorDetails();
		final List<SensorDetailsDTO> weatherNewData = sensorMeasurementsDriver.getWeatherSensorDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getSensorDetails();
		
		final List<String[]> newData = new ArrayList<>(lightNewData.size());
		for (int i = 0; i < lightNewData.size(); i++) {
			if (lightNewData.get(i).getTimestamp() == lastTimestamp) {
				continue;
			}			
			final String timestamp = String.valueOf(lightNewData.get(i).getTimestamp());
			final String inTemp = lightNewData.get(i).getInTemp().toString();
			final String outTemp = weatherNewData.get(i).getOutTemp().toString();
			final String totalHeat = String.valueOf(weatherNewData.get(i).getTotal());
			final String waterHeat = String.valueOf(weatherNewData.get(i).getWater());
			newData.add(new String[] {timestamp, inTemp, outTemp, totalHeat, waterHeat});
		}
		
		writeCSV(fileName, newData);
		return readCSV(fileName);
	}
	
	//-------------------------------------------------------------------------------------------------
	private SensorMeasurementsDTO predict(final List<String[]> dataSet, final long building, final long measurementsedTimestamp) {
		System.out.println("predict initiated");

		final List<SensorDetailsDTO> convertedDataSet = new ArrayList<>(dataSet.size());
		for (final String[] record : dataSet) {
			if (NumberUtils.isCreatable(record[0])) {				
				final SensorDetailsDTO sensorDetails = new SensorDetailsDTO.Builder(Long.valueOf(record[0]), Long.valueOf(building))
						.setInTemp(Double.valueOf(record[1]))
						.setOutTemp(Double.valueOf(record[2]))
						.setTotal(Double.valueOf(record[3]))
						.setWater(Double.valueOf(record[4]))
						.build();
				convertedDataSet.add(sensorDetails);
			}
		}		
		return new SensorConsumptionPredictor(convertedDataSet, building, measurementsedTimestamp).predict();
	}
	
	//-------------------------------------------------------------------------------------------------
	private void createIfAbsentCSV(final String fileName) throws IOException {
		System.out.println("createIfAbsentCSV initiated");

		final File file = new File(fileName);
		file.getParentFile().mkdirs();
		if (file.createNewFile()) {
			final FileWriter writer = new FileWriter(new File(fileName));
			writer.write(DATA_SET_HEADER);
			writer.flush();
			writer.close();
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<String[]> readCSV(final String fileName) throws IOException, URISyntaxException {
		System.out.println("readCSV initiated");

		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		final CSVReader csvReader = new CSVReader(reader);
		final List<String[]> dataSet = csvReader.readAll();
		reader.close();
		csvReader.close();
		return dataSet;		
	}
	
	//-------------------------------------------------------------------------------------------------
	private void writeCSV(final String fileName, final List<String[]> newData) throws IOException, URISyntaxException {
		System.out.println("writeCSV initiated");

		final CSVWriter writer = new CSVWriter(new FileWriter(new File(fileName), true));
		writer.writeAll(newData);
		writer.close();
	}
}
