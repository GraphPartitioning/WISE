# WISE: Fast and Efficient Vertex-cut Graph Partitioning

This repository contains the code for our novel algorithm to partition large-scale graphs with billions of edges with linear runtime complexity. Users can specify their runtime preference to control the trade-off between partitioning quality and runtime. For example, if you partition the graph as a pre-processing step for complex distributed graph algorithms such as PageRank or Deep Learning algorithms, you want to achieve high partitioning quality at the cost of higher partitioning latency -- as this reduces overall graph processing latency significantly:

For example: we executed PageRank on a web graph partitioned by different state-of-the-art algorithms. Our algorithm (AD)WISE reduces graph processing latency by 50% and network traffic by 66%!

![alt tag](https://github.com/GraphPartitioning/WISE/tree/master/GraphPartitioning/graphTraffic.jpg)
![alt tag](https://github.com/GraphPartitioning/WISE/tree/master/GraphPartitioning/graphLatency.jpg)
