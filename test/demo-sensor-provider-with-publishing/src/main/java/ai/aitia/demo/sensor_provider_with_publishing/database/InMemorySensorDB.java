package ai.aitia.demo.sensor_provider_with_publishing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;
import eu.arrowhead.common.exception.InvalidParameterException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class InMemorySensorDB extends ConcurrentHashMap<Integer, Sensor> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	public InMemorySensorDB() {
		initializeSensors();
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	private void initializeSensors() {
		try (BufferedReader br = new BufferedReader(new FileReader("demo-sensor-provider-with-publishing/target/test.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length == 2) {
					create(values[0], values[1]);
				} else {
					throw new InvalidParameterException("Invalid line in CSV: " + line);
				}
			} 

		} catch (IOException e) {
			throw new InvalidParameterException("Error reading CSV file", e);
		}
	}

	public Sensor create(final String name, final String value) {

		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

		if (name == null || name.isBlank()) {
			throw new InvalidParameterException("name is null or empty");
		}		
		if (value == null || value.isBlank()) {
			throw new InvalidParameterException("value is null or empty");
		}
		
		idCounter++;
		this.put(idCounter, new Sensor(idCounter, name.toLowerCase().trim(), value.toLowerCase().trim()));
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Sensor> getAll() {
		return List.copyOf(this.values());
	}
	
	//-------------------------------------------------------------------------------------------------
	public Sensor getById(final int id) {
		if (this.containsKey(id)) {
			return this.get(id);
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public Sensor updateById(final int id, final String name, final String value) {
		if (this.containsKey(id)) {
			final Sensor sensor = this.get(id);
			if (name!= null && !name.isBlank()) {
				sensor.setName(name);
			}
			if (value != null && !value.isBlank()) {
				sensor.setValue(value);
			}
			this.put(id, sensor);
			return sensor;
		} else {
			throw new InvalidParameterException("id '" + id + "' not exists");
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeById(final int id) {
		if (this.containsKey(id)) {
			this.remove(id);
		}
	}

	public void updateAll() {
		System.out.println("Updating all sensors AAAAAAAAAAA");
		// this.clear();
		initializeSensors();
	}
}
