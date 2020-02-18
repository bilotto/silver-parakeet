package org.com

class NodeNew {
	String user
	String hostname
	String homeDir
	NodeNew jumpServer
	def pipelineTools
	String releaseBaseDir
	NodeNew(user, hostname, homeDir, jumpServer, pipelineTools) {
		this.user = user
		this.hostname = hostname
		this.homeDir = homeDir
		this.jumpServer = jumpServer
		this.tools = pipelineTools.getTools()
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
		this.tools.log "Copying file ${file.fullPath} to node ${this.hostname}"
		if (!destinationDir) {
			destinationDir = this.homeDir
		}
		if (!this.directoryExists(destinationDir)) {
			this.tools.error "Directory ${destinationDir} does not exists in node"
		}
		if (!this.jumpServer) {
			if (!file.node.jumpServer) {
				this.tools.copy_file_to_node(file.node.user, file.node.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			} else {
			//todo: the code below is probably failing
				if (!nodeObject.isTheSameNode(this.jumpServer, file.node.jumpServer)) {
					//first, copy the file to the jump server from the jump server's side
					this.tools.copy_file_from_node(file.node.user, file.node.hostname, file.fullPath, jumpServer.user, jumpServer.hostname, jumpServer.homeDir)
					//now, copy the file from the jumpServer to the node
					file.replaceNode(file.node.jumpServer, jumpServer.homeDir)
					newFile = fileObject(file.name, jp_server.homeDir, file.node.jumpServer)
					this.copyFileToDir(newFile, destinationDir)
				}
				//todo: if the nodes share the same jump server, it asssumes they connect with each other without the jump server
			}
		} else {
			this.tools.log "The node ${this.hostname} has a jump server"
			//it copies the file to the jump server (if it's not there yet), and then it copies to the node
			this.tools.log "Checking if the file already exists in the jump server"
			if (!file.existsInNode(this.jumpServer, this.jumpServer.homeDir)) {
				this.jumpServer.copyFileToDir(file, this.jumpServer.homeDir)
			}
			file.replaceNode(jumpServer, jumpServer.homeDir)
			this.tools.transferFileBetweenHosts(this.jumpServer.user, this.jumpServer.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			//clean the file in the jump server if you want
			//file.deleteItself()
		}
		return true
	}
	
	Boolean copyRelease(ReleaseNew release, FileNew releaseFile) {
		if (!this.releaseBaseDir) {
			this.tools.error "releaseBaseDir not set in node object"
		}
		def command = "cd ${this.releaseBaseDir}; if [ -e ${release.filename} ]; then rm -f ${release.filename}; fi"
		this.execute(command)
		command = "cd ${this.releaseBaseDir}; if [ -e ${release.name} ]; then rm -rf ${release.name}; fi"
		this.execute(command)
		this.copyFileToDir(releaseFile, this.releaseBaseDir)
		return true
	}
	
	Boolean directoryExists(String directory) {
		def command = "if [ -e ${directory} ]; then echo true; else echo false; fi"
    	def stdout = this.executeAndGetOutput(command)
    	if (stdout == 'true') {
    		return true
    	}
    	return false 
	}


}

