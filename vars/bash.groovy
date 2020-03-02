String stringToCommand(String commandString){
	commandString = commandString.replace("\$", "\\\$")
	commandString = commandString.replace("\"", "\\\"")
	return commandString
}

String makeSshCommand(String remoteUser, String remoteHostname, String remoteCommand){
	remoteCommand = this.stringToCommand(remoteCommand)
	//def cmd = """
	//			ssh ${remoteUser}@${remoteHostname} \
	//			\"
	//				${remoteCommand}
	//			\"
	//		"""
	def cmd = "ssh ${remoteUser}@${remoteHostname} '${remoteCommand}' "
	return cmd   
}

String printWithNoTrace(command) {
	command = '#!/bin/sh -e\n ' + command
	return command
}

List runCmdOnNodeSavingExitCodeAndStdout(String cmd) {
    def rc = 0
    def stdout = null
    def tempFileName = 'tmp_' + UUID.randomUUID()
    def tempFilePath = "/tmp/" + tempFileName
	def command = "${cmd} > ${tempFilePath} 2>&1"
    rc = sh(script: "${command}", returnStatus: true)
    stdout = readFile(tempFilePath).trim()
    if (stdout != ''){
		log("DEBUG", "\n${stdout}")
	}
    // Delete temporary file from the node
	sh(script: this.printWithNoTrace('rm -f ') + tempFilePath, returnStatus: true)
    return [ rc, stdout ]
}


List executeLocalCommand(command){
	commandResult = this.runCmdOnNodeSavingExitCodeAndStdout(command)
	return commandResult
}


List executeRemoteCommand(String remoteUser, String remoteHostname, String remoteCommand) {
	def command = this.makeSshCommand(remoteUser, remoteHostname, remoteCommand)
	def commandResult = this.executeLocalCommand(command)
	return commandResult
}


List executeRemoteCommandThroughJumpServer(jumpServerUser, jumpServerHostname, String remoteUser, String remoteHostname, String remoteCommand) {
	remoteCommand = this.makeSshCommand(remoteUser, remoteHostname, remoteCommand)
	//def command = """
	//			ssh ${jumpServerUser}@${jumpServerHostname} \
	//			'
	//				${remoteCommand}
	//			'
	//		"""
	def command = "ssh ${jumpServerUser}@${jumpServerHostname} '${remoteCommand}'"
	def commandResult = this.executeLocalCommand(command)	
	return commandResult	
}


def transferFileBetweenHosts(String sourceUser, String sourceHostname, String fileFullPath, String destinationUser, String destinationHostname, String destinationDir) {
	def cmd = """
				scp ${fileFullPath} ${destinationUser}@${destinationHostname}:${destinationDir}
			"""
	returnOutput = false
	this.executeRemoteCommand(sourceUser, sourceHostname, cmd, return_output)
}


