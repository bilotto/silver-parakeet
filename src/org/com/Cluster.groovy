package org.com

class Cluster {
	String name
	List nodeList
	Cluster(name, nodeList) {
		this.name = name
		this.nodeList = nodeList
	}
	
	void execute(String command){
		script {
			this.nodeList.each { node ->
				branches[ node.hostname ] = { node.execute(command) }
			}
			parallel branches 
		}
	}
	
}



