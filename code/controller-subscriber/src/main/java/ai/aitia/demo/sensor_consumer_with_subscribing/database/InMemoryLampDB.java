package ai.aitia.demo.sensor_consumer_with_subscribing.database;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.stereotype.Component;

import ai.aitia.demo.sensor_consumer_with_subscribing.LampProviderConstants;
import ai.aitia.demo.sensor_consumer_with_subscribing.entity.Lamp;
import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class InMemoryLampDB extends ConcurrentHashMap<Integer, Lamp> {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -2462387539362748691L;
	
	private int idCounter = 0;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	//=================================================================================================
    // constructor

    public InMemoryLampDB() {
        initializeLamps();
    }

	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private void initializeLamps() {
		for(int i=0; i<LampProviderConstants.NUMBER_OF_LAMPS; i++){
        	create( 0);
		}
    }



	public Lamp create(final int status) {
		lock.readLock().lock();

		try{
			if (status>1 || status<0) {
				throw new InvalidParameterException("statusis null or empty");
			}
			
			idCounter++;
			this.put(idCounter, new Lamp(idCounter, status));
			return this.get(idCounter);
		} finally {
            lock.readLock().unlock();
        }
		
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<Lamp> getAll() {
		lock.readLock().lock();
		try{
			return List.copyOf(this.values());
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public Lamp getById(final int id) {
		lock.readLock().lock();

		try{
			if (this.containsKey(id)) {
				return this.get(id);
			} else {
				throw new InvalidParameterException("id '" + id + "' not exists");
			}
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public Lamp updateById(final int id, final Integer status) {
		lock.readLock().lock();

		try{
			if (this.containsKey(id)) {
				final Lamp lamp = this.get(id);
				
				if (status< 1 && status> 0 && status!=null) {
					lamp.setStatus(status);
				}
				this.put(id, lamp);
				return lamp;
			} else {
				throw new InvalidParameterException("id '" + id + "' not exists");
			}
		} finally {
            lock.readLock().unlock();
        }
	}
	
	//-------------------------------------------------------------------------------------------------
	public void removeById(final int id) {
		lock.readLock().lock();

		try{
			if (this.containsKey(id)) {
				this.remove(id);
			}
		} finally {
            lock.readLock().unlock();
        }
	}
}
