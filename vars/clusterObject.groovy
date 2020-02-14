import org.com.Cluster

def call(String clusterName, Map nodeObjects){
	nodeList = []
	nodeObjects.each{ k, v -> nodeList.add(v) }
	return new Cluster(clusterName, nodeList)
}

