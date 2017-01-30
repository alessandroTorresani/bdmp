import matplotlib.pyplot as plt
import os.path
from mpl_toolkits.mplot3d import Axes3D
import sys

def get_clusters(foldername):
    clusters = {}
    points = []
    counter = 0
    cycle = True
    while(cycle):
        points = []
        if (os.path.exists(os.path.expanduser('~')+"/Documents/bdmpFiles/output/"+foldername+"/cluster"+str(counter)+".txt")):
            f = open(os.path.expanduser('~')+"/Documents/bdmpFiles/output/"+foldername+"/cluster"+str(counter)+".txt")
            for line in f:
                point = line.split("\t")
                points.append(float(point[1]))
                points.append(float(point[2]))
                points.append(float(point[3]))
            clusters['cluster'+str(counter)] = points   
            counter = counter + 1
        else:
            cycle = False;
    return clusters

if (len(sys.argv) != 2):
    print "Error, usage python [script][foldername]"
else :
    foldername = sys.argv[1]
    clusters = get_clusters(foldername)
    output = {}
    colors = ['red', 'green','blue','yellow','orange']

    for key in clusters:
        points = clusters[key]
        first_list = []
        second_list = []
        third_list = []

        index = 0;
        for x in range(0,len(points)/3):
            first_list.append(points[index])
            second_list.append(points[index+1])
            third_list.append(points[index+2])
            index = index + 3
        
        output_list = []
        output_list.append(first_list)
        output_list.append(second_list)
        output_list.append(third_list)
        output[key] = output_list

    index = 0
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')

    for key in output:
        input_list = output[key]
        first_list = []
        second_list = []
        third_list = []

        for item in input_list[0]:
            first_list.append(float(item))
        for item in input_list[1]:
            second_list.append(float(item))
        for item in input_list[2]:
            third_list.append(float(item))

        ax.scatter(first_list, second_list, third_list, c=colors[index])
        
        index = index + 1
    
    plt.show()

