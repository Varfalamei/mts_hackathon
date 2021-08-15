# libs
import json
import math
from PIL import Image, ImageOps
import threading
import urllib.request
import io
import numpy as np
import time
from config import *


def from_geo_to_pixels(lat, lon, z):
    rho = math.pow(2, z + 8) / 2
    beta = lat * math.pi / 180
    phi = (1 - e * math.sin(beta)) / (1 + e * math.sin(beta))
    theta = math.tan(math.pi / 4 + beta / 2) * math.pow(phi, e / 2)

    x_p = rho * (1 + lon / 180)
    y_p = rho * (1 - math.log(theta) / math.pi)

    return [x_p / 256, y_p / 256]


# calculating the quality of mobile operators
def calc_qual_mob_operators(name, operators, zoom, coordinates):
    # for name in ['mts', 'beeline', 'yota']:
    dict_json = {}
    for i in ['2g', '3g', '4g']:
        dict_json[i] = {}
        for z in zoom:
            dict_json[i][z] = []

    for z in zoom:
        x1, y1 = list(map(int, from_geo_to_pixels(coordinates[0], coordinates[1], z)))
        x2, y2 = list(map(int, from_geo_to_pixels(coordinates[2], coordinates[3], z)))
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for g in ['2g', '3g', '4g']:
                    link = operators[name][g]
                    link += f'{z}/{x}/{y}'
                    if name == 'beeline' or name == 'yota':
                        link += '.png'

                    data = urllib.request.urlopen(link).read()
                    quality = 0
                    if name == 'beeline':
                        quality = (np.array(ImageOps.grayscale(Image.open(io.BytesIO(data)))) < 255).sum() / (256 * 256)
                    elif name == 'mts' or name == 'yota':
                        quality = (np.array(ImageOps.grayscale(Image.open(io.BytesIO(data)))) > 0).sum() / (256 * 256)
                    dict_json[g][z].append([x, y, link, round(quality, 4)])

    with open(f'data/{name}.json', 'w', encoding='utf-8') as f:
        json.dump(dict_json, f, ensure_ascii=False, indent=4)


begin_time = time.time()

calc_tread_numb_1 = threading.Thread(target=calc_qual_mob_operators, args=('mts', operators, zoom, coordinates,))
calc_tread_numb_2 = threading.Thread(target=calc_qual_mob_operators, args=('beeline', operators, zoom, coordinates,))
calc_tread_numb_3 = threading.Thread(target=calc_qual_mob_operators, args=('yota', operators, zoom, coordinates,))

calc_tread_numb_1.start()
calc_tread_numb_2.start()
calc_tread_numb_3.start()

calc_tread_numb_1.join()
calc_tread_numb_2.join()
calc_tread_numb_3.join()

end_time = time.time()

print(f'Все вычисления завершены. Время выполнения составило - {end_time - begin_time} sec')
