import csv
import random
from datetime import datetime, timedelta

def generate_luminosity_values_for_one_day():
    # Configuración inicial
    start_time = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    minutes_in_a_day = 1440
    min_luminosity = 0
    max_luminosity = 1000

    # Crear y abrir el archivo CSV
    with open('luminosity_values.csv', mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['Timestamp', 'Luminosity'])

        # Generar valores de luminosidad para cada minuto del día
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

            # Añadir un poco de variación aleatoria
            luminosity += random.uniform(-50, 50)
            luminosity = max(min_luminosity, min(max_luminosity, luminosity))

            writer.writerow([current_time.strftime('%Y-%m-%d %H:%M:%S'), round(luminosity)])

    print("Luminosity values generated successfully.")

if __name__ == "__main__":
    generate_luminosity_values_for_one_day()