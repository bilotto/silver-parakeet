package org.com

class Cluster {
	String name
	List nodeList
	List jumpServerList
	def tools
	Cluster(name, nodeList, tools) {
		this.name = name
		this.nodeList = nodeList
		this.tools = tools
		this.jumpServerList = [  ]
	
		for (node in this.nodeList) {
			if (!node.jumpServer) {
				continue
			}
			if (!this.jumpServerList.size()) {
				this.jumpServerList.add(node.jumpServer)
			}
			this.jumpServerList.each { jumpServer ->
				if (node.jumpServer != jumpServer) {
					if ( node.jumpServer.user != jumpServer.user || node.jumpServer.hostname != jumpServer.hostname ) {
						this.jumpServerList.add(node.jumpServer)
					}
				}
			}
		}
	}
	
	void execute(String command){
		def branches = [ : ]
		this.nodeList.each { node ->
			branches[ node.hostname ] = { node.execute(command) }
		}
		this.tools.executeInParallel(branches)
	}
	
	Boolean copyFileToDir(FileNew file, String destinationDir) {
		def branches = [ : ]
		if (this.jumpServerList.size()) {
			this.jumpServerList.each { node ->
				branches[ node.hostname ] = { node.copyFileToDir(file, node.homeDir) }
			}
			this.tools.executeInParallel(branches)
		}
		branches = [ : ]
		this.nodeList.each { node ->
			if (!destinationDir) {
				destinationDir = node.homeDir
			}
			branches[ node.hostname ] = { node.copyFileToDir(file, destinationDir) }
		}
		this.tools.executeInParallel(branches)      
		return true    
	}
	
	Boolean copyRelease(ReleaseNew release, FileNew releaseFile){
		def branches = [ : ]
		if (this.jumpServerList.size()) {
			this.jumpServerList.each { node ->
				branches[ node.hostname ] = { node.copyRelease(release, releaseFile.homeDir) }
			}
			this.tools.executeInParallel(branches)
		}
		branches = [ : ]
		this.nodeList.each { node ->
			if (!destinationDir) {
				destinationDir = node.homeDir
			}
			branches[ node.hostname ] = { node.copyRelease(release, releaseFile) }
		}
		this.tools.executeInParallel(branches)
		return true 	
	}
}



