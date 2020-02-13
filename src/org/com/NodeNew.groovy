package org.com

class NodeNew {
	String user
	String hostname
	String homeDir
	NodeNew jumpServer
	def tools
	String releaseBaseDir
	NodeNew(user, hostname, homeDir, jumpServer, tools) {
		this.user = user
		this.hostname = hostname
		this.homeDir = homeDir
		this.jumpServer = jumpServer
		this.tools = tools
	}
	
	def executeCommand(String command, Boolean returnOutput){
		if (!this.jumpServer) {
			this.tools.executeRemoteCommand(this.user, this.hostname, command, returnOutput)
		} else {
			this.tools.executeRemoteCommandThroughJumpServer(jumpServer.user, jumpServer.hostname, \
																this.user, this.hostname, command, returnOutput)
	  	}
	}
	
	void execute(String command) {
		def returnOutput = false
		this.executeCommand(command, returnOutput)
	}
	
	String executeAndGetOutput(String command) {
		def returnOutput = true
		def output = this.executeCommand(command, returnOutput)
		return output
	}
	
	
	//todo: if two nodes share the same jump server, they can connect with each other without the jump server
	Boolean copyFileToDir(FileNew file, String destinationDir) {
		if (!destinationDir) {
			destinationDir = this.homeDir
		}
		if (!this.jumpServer) {
			if (!file.node.jumpServer) {
				this.tools.copy_file_to_node(file.node.user, file.node.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			} else {
				if (!nodeObject.isTheSameNode(this.jumpServer, file.node.jumpServer)) {
					//first, copy the file to the jump server from the jump server's side
					this.tools.copy_file_from_node(file.node.user, file.node.hostname, file.fullPath, jumpServer.user, jumpServer.hostname, jumpServer.homeDir)
					//now, copy the file from the jumpServer to the node
					newFile = fileObject(file.name, jp_server.homeDir, file.node.jumpServer)
					this.copyFileToDir(newFile, destinationDir)
				}
				//todo: if the nodes share the same jump server, it asssumes they connect with each other without the jump server
			}
		} else {
			//it copies the file to the jump server (if it's not there yet), and then it copies to the node
			if (!file.existsinNode(this.jumpServer, this.jumpServer.homeDir)) {
				this.jumpServer.copyFileToDir(file, jumpServer.homeDir)
			}
			file.replaceNode(this.jumpServer, jumpServer.homeDir)
			this.tools.transferFileBetweenHosts(jumpServer.user, jumpServer.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			//clean the file in the jump server
			file.deleteItself()
		}
		return true
	}

}

