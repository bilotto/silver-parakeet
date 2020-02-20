package org.com

class NodeNew {
	String user
	String hostname
	String homeDir
	NodeNew jumpServer
	def pipelineTools
	def tools
	String releaseBaseDir
	NodeNew(user, hostname, homeDir, jumpServer, pipelineTools) {
		this.user = user
		this.hostname = hostname
		this.homeDir = homeDir
		this.jumpServer = jumpServer
		this.pipelineTools = pipelineTools
		this.tools = pipelineTools.tools
	}
	
	def executeCommand(String command, Boolean returnOutput){
		def bash = this.pipelineTools.bash
		if (!this.jumpServer) {
			bash.executeRemoteCommand(this.user, this.hostname, command, returnOutput)
		} else {
			bash.executeRemoteCommandThroughJumpServer(jumpServer.user, jumpServer.hostname, \
																this.user, this.hostname, command, returnOutput)
	  	}
	}
	
	Boolean isTheSameNode(NodeNew node) {
		if (node != this) {
			if ( node.user != this.user || node.hostname != this.hostname ) {
				return false
			}
		}
		return true
	}
	
	void execute(String command) {
		def returnOutput = false
		this.executeCommand(command, returnOutput)
	}
	
	String executeAndGetOutput(String command) {
		def log = this.pipelineTools.log
		def returnOutput = true
		def output = this.executeCommand(command, returnOutput)
		if (!output) {
			log.raiseError "No output to return"
		}
		return output
	}
	
	//todo: if two nodes share the same jump server, they can connect with each other without the jump server
	Boolean copyFileToDir(FileNew file, String destinationDir) {
		def log = this.pipelineTools.log
		log("INFO", "Copying file ${file.fullPath} to node ${this.hostname}")
		if (!destinationDir) {
			destinationDir = this.homeDir
		}
		if (!this.directoryExists(destinationDir)) {
			log("INFO", "Directory ${destinationDir} does not exists in node")
		}
		if (!this.jumpServer) {
			if (!file.node.jumpServer) {
				this.pipelineTools.tools.copy_file_to_node(file.node.user, file.node.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			} else {
				if ( this.isTheSameNode(file.node.jumpServer) ) {
					log("DEBUG", "The nodes are the same")
					//first, copy the file to the jump server from the jump server's side
					this.pipelineTools.tools.copy_file_from_node(file.node.user, file.node.hostname, file.fullPath, this.user, this.hostname, this.homeDir)
				}
			}
		} else {
			log("DEBUG", "The node ${this.hostname} has a jump server")
			//it copies the file to the jump server (if it's not there yet), and then it copies to the node
			log("DEBUG", "Checking if the file already exists in the jump server")
			if (!file.existsInNode(this.jumpServer, this.jumpServer.homeDir)) {
				this.jumpServer.copyFileToDir(file, this.jumpServer.homeDir)
			}
			file.replaceNode(jumpServer, jumpServer.homeDir)
			this.pipelineTools.tools.transferFileBetweenHosts(this.jumpServer.user, this.jumpServer.hostname, file.fullPath, this.user, this.hostname, destinationDir)
			//clean the file in the jump server if you want
			//file.deleteItself()
		}
		return true
	}
	
	Boolean copyRelease(ReleaseNew release, FileNew releaseFile) {
		def log = this.pipelineTools.log
		if (!this.releaseBaseDir) {
			log.raiseError "releaseBaseDir not set in node object"
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
	
	//this method only works if the env variable is in the .bashrc file and not .bash_profile
	String getEnvironmentVariable(var){
		def command = "env | grep ${var}"
	    def output = this.executeAndGetOutput(command)
	    def value = output.split("=")[ 1 ]
	    return value
	}



}

