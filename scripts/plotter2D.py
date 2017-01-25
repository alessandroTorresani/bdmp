import matplotlib.pyplot as plt
import os.path
from mpl_toolkits.mplot3d import Axes3D

def get_clusters():
    clusters = {}
    points = []
    counter = 0
    cycle = True
    while(cycle):
        points = []
        if (os.path.exists("../output/cluster"+str(counter)+".txt")):
            f = open("../output/cluster"+str(counter)+".txt")
            for line in f:
                point = line.replace("\n","").split("\t")
                points.append(point)
            clusters['cluster'+str(counter)] = points   
            counter = counter + 1
        else:
            cycle = False;
    return clusters


clusters = get_clusters()
output = {}
colors = ['red', 'green','blue','yellow','orange']

for key in clusters:
    points = clusters[key]
    first_list = []
    second_list = []
    for point in points:
        first_list.append(point[0])
        second_list.append(point[1])
    
    output_list = []
    output_list.append(first_list)
    output_list.append(second_list)
    output[key] = output_list

index = 0
for key in output:
    input_list = output[key]
    first_list = input_list[0]
    second_list = input_list[1]
    plt.plot(first_list, second_list, 'ro', color=colors[index])
    plt.axis([0, 100, 0, 100])
    index = index + 1
    
plt.show()







