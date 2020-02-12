package org.com

class Cluster {
	String name
	List nodeList
	Cluster(name, nodeList, tools) {
		this.name = name
		this.nodeList = nodeList
		this.tools = tools
	}
	
	void execute(String command){
		def branches = [ : ]
		this.nodeList.each { node ->
			branches[ node.hostname ] = { node.execute(command) }
		}
		tools.executeInParallel(branches)
		//parallel branches 
	}
	
}



