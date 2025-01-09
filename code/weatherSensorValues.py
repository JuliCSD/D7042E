import csv
import time
import random

nb_weather_sensors = 50

# Inicializar los datos de los sensores
data = [(random.uniform(20, 25), random.uniform(40, 60), random.uniform(1000, 1020), random.uniform(0, 20)) for _ in range(nb_weather_sensors)]

while True:
    # Abrir el archivo CSV para escribir (sobrescribir)
    with open('weather-sensor-provider/target/test.csv', mode='w', newline='') as file:
        writer = csv.writer(file)
        # Escribir el encabezado
        # writer.writerow(['Temperature(°C)', 'Humidity(%)', 'Pressure(hPa)', 'WindSpeed(km/h)'])
        
        # Escribir los datos
        for i, (temp, hum, pres, wind_speed) in enumerate(data):
            writer.writerow([temp, hum, pres, wind_speed])
            print(f'{temp}°C, {hum}%, {pres}hPa, {wind_speed}km/h')
        
        # Actualizar los datos con algunos cambios aleatorios
        data = [
            (
                temp + random.uniform(-0.5, 0.5),
                hum + random.uniform(-1, 1),
                pres + random.uniform(-0.1, 0.1),
                wind_speed + random.uniform(-2, 2),
            )
            for temp, hum, pres, wind_speed in data
        ]
    
    # Esperar 1 segundo antes de sobrescribir el archivo nuevamente
    print('\n\n')
    time.sleep(1)