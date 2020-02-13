package org.com

class Cluster {
	String name
	List nodeList
	def tools
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
		this.tools.executeInParallel(branches)
		//parallel branches
	}
	
	void copyFile(FileNew file, String destinationDir) {
		def branches = [ : ]
	
		def jpObjectsList = [  ]
		this.nodeList.each { node ->
			if (!node.jumpServer) {
				continue
			}
			if (!jpObjectsList.size()) {
				jpObjectsList.add(node.jumpServer)
			}
			jpObjectsList.each { jumpServer ->
				
				if (node.jumpServer != jumpServer) {
					if ( node.jumpServer.user != jumpServer.user || node.jumpServer.hostname != jumpServer.hostname ) {
						jpObjectsList.add(node.jumpServer)
					}
				}
			}
		}			
		
		if (jpObjectsList.size()) {
			jpObjectsList.each { node ->
				branches[ node.hostname ] = { node.copyFile(file, null) }
			}
			this.tools.executeInParallel(branches)
		}
		
		branches = [ : ]
		this.nodeList.each { node ->
			if (!destinationDir) {
				destinationDir = node.homeDir
			}
			branches[ node.hostname ] = { node.copyFile(file, destinationDir) }
		}
		this.tools.executeInParallel(branches)
		
		               
	}

	
}



