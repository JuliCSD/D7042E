package ai.aitia.demo.sensor_provider_with_publishing.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.exception.InvalidParameterException;

@Component
public class CsvDataLoader implements CommandLineRunner {
    
    @Autowired
    private final InMemorySensorDB sensorDB;

    public CsvDataLoader(InMemorySensorDB sensorDB) {
        this.sensorDB = sensorDB;
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            try {
                Thread.sleep(2000);
                loadCsvData();
                if (sensorDB.getAll().size() > 0) {
                    System.out.println(sensorDB.getAll().get(0).getValue());
                } else {
                    System.out.println("No data available in sensorDB.");
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadCsvData() {
        System.out.println("Loading data from CSV file...");
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the number of threads as needed

        try (BufferedReader br = new BufferedReader(new FileReader("demo-sensor-provider-with-publishing/target/test.csv"))) {
            String line;
            int id = 0;
            while ((line = br.readLine()) != null) {
                final int sensorId = id;
                final String[] values = line.split(",");
                if (values.length == 2) {
                    executorService.submit(() -> {
                        sensorDB.updateById(sensorId, values[0], values[1]);
                    });
                } else {
                    throw new InvalidParameterException("Invalid line in CSV: " + line);
                }
                id++;
            }
        } catch (IOException e) {
            throw new InvalidParameterException("Error reading CSV file", e);
        } finally {
            executorService.shutdown();
        }
    }
}