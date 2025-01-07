package ai.aitia.demo.sensor_provider_with_publishing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

import ai.aitia.demo.sensor_provider_with_publishing.entity.Sensor;
import eu.arrowhead.common.exception.InvalidParameterException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Component
public class InMemorySensorDB extends ConcurrentHashMap<Integer, Sensor> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();


	public InMemorySensorDB() {
		initializeSensors();
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	private void initializeSensors() {
		lock.readLock().lock();

		try (BufferedReader br = new BufferedReader(new FileReader("light-sensor-provider/target/test.csv"))) {
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
		} finally {
            lock.readLock().unlock();
        }
	}

	public Sensor create(final String name, final String value) {
		lock.readLock().lock();
        try {
			if (name == null || name.isBlank()) {
				throw new InvalidParameterException("name is null or empty");
			}		
			if (value == null || value.isBlank()) {
				throw new InvalidParameterException("value is null or empty");
			}
			
			idCounter++;
			this.put(idCounter, new Sensor(idCounter, name.toLowerCase().trim(), value.toLowerCase().trim()));

		} finally {
            lock.readLock().unlock();
        }
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Sensor> getAll() {
		lock.readLock().lock();
        try {
			return List.copyOf(this.values());
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public Sensor getById(final int id) {
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
	public Sensor updateById(final int id, final String name, final String value) {
		lock.writeLock().lock();
        try {
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
