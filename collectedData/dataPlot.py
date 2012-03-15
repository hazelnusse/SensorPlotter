import matplotlib.pyplot as plt
import numpy as np

files = ['Accelerometer_data.csv']

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
            t0 = float(t)

        T.append((float(t) - t0)*1e-9)
        X.append(float(x))
        Y.append(float(y))
        Z.append(float(z))

    XYZ = np.array([X, Y, Z])
    #T = np.array(T)

    plt.plot(T, XYZ.T)
plt.show()










