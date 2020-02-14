import org.com.Cluster

def call(String clusterName, Map nodeObjects){
	nodeList = []
	nodeObjects.each{ k, v -> nodeList.add(v) }
	if (!nodeList.size()){
	    error "empty node list"
	}
	return new Cluster(clusterName, nodeList, tools)
}

