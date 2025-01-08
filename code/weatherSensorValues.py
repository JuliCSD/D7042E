import csv
from datetime import datetime, timedelta
import random
import time

start_time = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
minutes_in_a_day = 1440
min_luminosity = 0
max_luminosity = 1000
nb_light_sensors = 50


file_path = 'weather-sensor-provider/target/test.csv'


for minute in range(minutes_in_a_day):
        current_time = start_time + timedelta(minutes=minute)
        hour = current_time.hour + current_time.minute / 60.0

        # Simular el ciclo de luminosidad del sol
        if 6 <= hour < 18:
            # Durante el día, la luminosidad aumenta hasta el mediodía y luego disminuye
            if hour < 12:
                luminosity = min_luminosity + (max_luminosity - min_luminosity) * ((hour - 6) / 6)
            else:
                luminosity = max_luminosity - (max_luminosity - min_luminosity) * ((hour - 12) / 6)
        else:
            # Durante la noche, la luminosidad es baja
            luminosity = min_luminosity

        light_sensor_data = []
        for i in range(nb_light_sensors):
            luminosity += random.uniform(-50, 50)
            luminosity = max(min_luminosity, min(max_luminosity, luminosity))
            luminosity = round(luminosity, 1)

            light_sensor_data.append([f'light_sensor{i+1}', luminosity])

        
        # Overwrite the data in the CSV file
        with open(file_path, mode='w', newline='') as file:
            writer = csv.writer(file)
            # writer.writerow(['light_sensor_type', 'luminosity'])
            writer.writerows(light_sensor_data)
        
        print(f"Weather values for {nb_light_sensors} sensors have been overwritten in {file_path}")    
        # Wait for 1 seconds before generating new values
        time.sleep(1)