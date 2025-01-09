import csv
import time
import random

nb_weather_sensors = 100

# Inicializar los datos de los sensores
data = [(random.uniform(20, 25), random.uniform(40, 60), random.uniform(1010, 1020), random.uniform(0, 20)) for _ in range(nb_weather_sensors)]

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
                max(5, min(40, temp + random.uniform(-0.5, 0.5))),  # Mantener la temperatura entre 10 y 35
                max(0, min(100, hum + random.uniform(-1, 1))),  # Mantener la humedad entre 0 y 100
                max(1000, min(1050, pres + random.uniform(-0.1, 0.1))),  # Mantener la presión entre 1000 y 1050
                max(0, min(50, wind_speed + random.uniform(-2, 2)))  # Mantener la velocidad del viento entre 0 y 50
            )
            for temp, hum, pres, wind_speed in data
        ]
        
        # Ajustar la humedad para que raras veces sea superior a 80
        data = [(temp, hum if random.random() > 0.1 else random.uniform(40, 80), pres, wind_speed) for temp, hum, pres, wind_speed in data]
        
        # Ajustar la presión para que raras veces sea inferior a 1010
        data = [(temp, hum, pres if random.random() > 0.1 else random.uniform(1010, 1050), wind_speed) for temp, hum, pres, wind_speed in data]
        
        # Ajustar la velocidad del viento para que raras veces sea superior a 50
        data = [(temp, hum, pres, wind_speed if random.random() > 0.1 else random.uniform(0, 50)) for temp, hum, pres, wind_speed in data]
    
    # Esperar 1 segundo antes de sobrescribir el archivo nuevamente
    print('\n\n')
    time.sleep(1)