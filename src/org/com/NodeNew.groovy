package org.com

class NodeNew {
	String user
	String hostname
	String homeDir
	NodeNew jumpServer
	String releaseBaseDir
	NodeNew(user, hostname, homeDir, jumpServer) {
		this.user = user
		this.hostname = hostname
		this.homeDir = homeDir
		this.jumpServer = jumpServer
	}
	
	private def executeCommand(String command, Boolean returnOutput){
		if (!this.jumpServer) {
			tools.executeRemoteCommand(this.user, this.hostname, command, returnOutput)
		} else {
			tools.executeRemoteCommandThroughJumpServer(jumpServer.user, jumpServer.hostname, \
																this.user, this.hostname, command, returnOutput)
	  	}
	}
	
	void execute(String command) {
		def returnOutput = false
		this.executeCommand(command, returnOutput)
	}
	
	void executeAndGetOutput(String command) {
		def returnOutput = true
		return this.executeCommand(command, returnOutput)
	}
	
	
	//todo: it needs the new definitions of the File class
	//todo: if two nodes share the same jump server, they can connect with each other without the jump server
	Boolean copyFileToDir(File file, String destinationDir) {
		if (!destinationDir) {
			destinationDir = this.homeDir
		}
		if (!this.jumpServer) {
			if (!file.node.jumpServer) {
				tools.copy_file_to_node(file.node.user, file.node.hostname, file.file_full_path, this.user, this.hostname, destinationDir)
			} else {
				//first, copy the file to the jump server from the jump server's side
				def jumpServer = file.node.jumpServer
				tools.copy_file_from_node(file.node.user, file.node.hostname, file.file_full_path, jumpServer.user, jumpServer.hostname, jumpServer.homeDir)
				//now, copy the file from the jumpServer to the node
				newFile = fileObject(file.name, jp_server.homeDir, file.node.jumpServer)
				this.copyFileToDir(newFile, destinationDir)
			}
		} else {
			//it copies the file to the jump server, and then it copies to the node
			this.jumpServer.copyFileToDir(file, jumpServer.home_dir)
			newFile = fileObject(file.name, this.jumpServer.homeDir, this.jumpServer)
			tools.transferFileBetweenHosts(jumpServer.user, jumpServer.hostname, newFile.fullPath, this.user, this.hostname, destinationDir)
		}
		return true
	}

}

