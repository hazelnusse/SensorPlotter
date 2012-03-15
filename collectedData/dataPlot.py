import matplotlib.pyplot as plt
import numpy as np

files = ['Accelerometer_data.csv',
         'Gravity_data.csv',
         'Gyroscope_data.csv',
         'LinearAcceleration_data.csv',
         'MagneticField_data.csv',
         'Orientation_data.csv',
         'RotationVector_data.csv']

sensor_dict = {1 : 'Accelerometer',
               9 : 'Gravity',
               4 : 'Gyroscope',
               10 : 'Linear Acceleration',
               2 : 'Magnetic Field',
               3 : 'Orientation',
               11 : 'Rotation Vector'}

delay_dict = {0 : 'Fastest',
              1 : 'Game',
              3 : 'Normal',
              2 : 'UI'}

for f in files:
    fp = open(f, 'r')
    labels = fp.readline().split(',')

    T = []
    X = []
    Y = []
    Z = []

    t0 = 0.0
    legend = ""
    for i, line in enumerate(fp):
        t, x, y, z, sensor_type, sensor_delay = line.split(",")

        if i == 0:
            print line
            t0 = float(t)
            sensor = sensor_dict[int(sensor_type)]
            delay = delay_dict[int(sensor_delay)]


        T.append((float(t) - t0)*1e-9)
        X.append(float(x))
        Y.append(float(y))
        Z.append(float(z))

    plt.figure()
    plt.plot(T, X, label='x axis')
    plt.plot(T, Y, label='y axis')
    plt.plot(T, Z, label='z axis')
    plt.title("Sensor: {0}, Delay: {1}".format(sensor, delay))
    plt.xlabel('Time [seconds]')
    plt.legend(loc=0)
    plt.savefig(f[:-3] + "png")

#plt.show()
