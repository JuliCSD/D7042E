package ai.aitia.demo.weather_sensor_provider_with_publishing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

import ai.aitia.demo.weather_sensor_provider_with_publishing.entity.WeatherSensor;
import eu.arrowhead.common.exception.InvalidParameterException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Component
public class InMemoryWeatherSensorDB extends ConcurrentHashMap<Integer, WeatherSensor> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();


	public InMemoryWeatherSensorDB() {
		initializeWeatherSensors();
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	private void initializeWeatherSensors() {
		lock.readLock().lock();

		try (BufferedReader br = new BufferedReader(new FileReader("weather-sensor-provider/target/test.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (values.length == 4) {
					create(values[0], values[1], values[2], values[3]);
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

	public WeatherSensor create(final String temperature, final String humidity, final String pressure, final String wind) { 
		lock.readLock().lock();
        try {
			if (temperature == null || temperature.isBlank()) {
				throw new InvalidParameterException("name is null or empty");
			}		
			if (humidity == null || humidity.isBlank()) {
				throw new InvalidParameterException("value is null or empty");
			}
			if (pressure == null || pressure.isBlank()) {
				throw new InvalidParameterException("name is null or empty");
			}		
			if (wind == null || wind.isBlank()) {
				throw new InvalidParameterException("value is null or empty");
			}
			
			idCounter++;
			this.put(idCounter, new WeatherSensor(idCounter, temperature.toLowerCase().trim(), humidity.toLowerCase().trim(), pressure.toLowerCase().trim(), wind.toLowerCase().trim()));

		} finally {
            lock.readLock().unlock();
        }
		return this.get(idCounter);
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<WeatherSensor> getAll() {
		lock.readLock().lock();
        try {
			return List.copyOf(this.values());
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public WeatherSensor getById(final int id) {
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
	public WeatherSensor updateById(final int id, final String temperature, final String humidity, final String pressure, final String wind) {
		lock.writeLock().lock();
        try {
			if (this.containsKey(id)) {
				final WeatherSensor weatherSensor = this.get(id);
				if (temperature!= null && !temperature.isBlank()) {
					weatherSensor.setTemperature(temperature);
				}
				if (humidity != null && !humidity.isBlank()) {
					weatherSensor.setHumidity(humidity);
				}
				if (pressure!= null && !pressure.isBlank()) {
					weatherSensor.setTemperature(pressure);
				}
				if (wind != null && !wind.isBlank()) {
					weatherSensor.setWind(wind);
				}
				this.put(id, weatherSensor);
				return weatherSensor;
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
