package ai.aitia.demo.light_sensor_provider_with_publishing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

import ai.aitia.demo.light_sensor_provider_with_publishing.entity.LightSensor;
import eu.arrowhead.common.exception.InvalidParameterException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Component
public class InMemoryLightSensorDB extends ConcurrentHashMap<Integer, LightSensor> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();


	public InMemoryLightSensorDB() {
		initializeLightSensors();
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	private void initializeLightSensors() {
		lock.readLock().lock();

		try (BufferedReader br = new BufferedReader(new FileReader("light-sensor-provider/target/test.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length == 1) {
					create(values[0]);
				} else {
					throw new InvalidParameterException("Invalid line in CSV: " + line);
				}
			} 

		} catch (IOException e) {
			throw new InvalidParameterException("Error reading CSV file", e);
		} finally {
            lock.readLock().unlock();
        }
	}

	public LightSensor create(final String value) {
		lock.readLock().lock();
        try {
					
			if (value == null || value.isBlank()) {
				throw new InvalidParameterException("value is null or empty");
			}
			
			idCounter++;
			this.put(idCounter, new LightSensor(idCounter, value.toLowerCase().trim()));

		} finally {
            lock.readLock().unlock();
        }
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<LightSensor> getAll() {
		lock.readLock().lock();
        try {
			return List.copyOf(this.values());
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public LightSensor getById(final int id) {
		lock.writeLock().lock();
		try{
			if (this.containsKey(id)) {
				return this.get(id);
			} else {
				throw new InvalidParameterException("id '" + id + "' not exists");
			}
		} finally {
            lock.writeLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public LightSensor updateById(final int id, final String value) {
		lock.writeLock().lock();
        try {
			if (this.containsKey(id)) {
				final LightSensor lightSensor = this.get(id);
				
				if (value != null && !value.isBlank()) {
					lightSensor.setValue(value);
				}
				this.put(id, lightSensor);
				return lightSensor;
			} else {
				throw new InvalidParameterException("id '" + id + "' not exists");
			}
		} finally {
            lock.writeLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeById(final int id) {

		lock.writeLock().lock();
        try {
			if (this.containsKey(id)) {
				this.remove(id);
			}
		} finally {
            lock.writeLock().unlock();
        }
	}

}
