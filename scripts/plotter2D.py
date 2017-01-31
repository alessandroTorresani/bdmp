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
            clusters['cluster'+str(counter)] = points   
            counter = counter + 1
        else:
            cycle = False;
    return clusters

if (len(sys.argv) != 2):
    print "Error, usage python [script][foldername]"
else:
    foldername = sys.argv[1]
    clusters = get_clusters(foldername)
    output = {}
    colors = ['red', 'green','blue','yellow','orange', 'black', "grey", 'pink', 'violet', 'indigo',"brown"]

    for key in clusters:
        points = clusters[key]
        first_list = []
        second_list = []
        
        index = 0
        for x in range(0,len(points)/2):
            first_list.append(points[index])
            second_list.append(points[index+1])
            index = index + 2 

        output_list = []
        output_list.append(first_list)
        output_list.append(second_list)
        output[key] = output_list

    index = 0;
    figure = plt.figure(foldername)
    for key in output:
        input_list = output[key]
        first_list = input_list[0]
        second_list = input_list[1]
        plt.plot(first_list, second_list, 'ro', color=colors[index])
        index = index + 1

   # figure = plt.figure(foldername)
    plt.show()  






