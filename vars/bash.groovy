def stringToCommand(commandString){
	commandString = commandString.replace("\$", "\\\$")
	commandString = commandString.replace("\"", "\\\"")
	return commandString
}

def makeSshCommand(remoteUser, remoteHostname, remoteCommand){
	remoteCommand = this.stringToCommand(remoteCommand)
	def cmd = """
				ssh ${remoteUser}@${remoteHostname} \
				\"
					${remoteCommand}
				\"
			"""
	return cmd   
}

def executeLocalCommand(command, returnOutput){
	if (!returnOutput) {
		sh "${command}"
	} else {
       def stdout = sh (returnStdout: true, script: "${command}").trim().toString()
       return stdout
  	}
}

def executeRemoteCommand(String remoteUser, String remoteHostname, String remoteCommand, Boolean returnOutput) {
	def command = this.makeSshCommand(remoteUser, remoteHostname, remoteCommand)
	this.executeLocalCommand(command, returnOutput)
}


def executeRemoteCommandThroughJumpServer(jumpServerUser, jumpServerHostname, String remoteUser, String remoteHostname, String remoteCommand, Boolean returnOutput) {
	remoteCommand = this.makeSshCommand(remoteUser, remoteHostname, remoteCommand)
	def command = """
				ssh ${jumpServerUser}@${jumpServerHostname} \
				'
					${remoteCommand}
				'
			"""
	this.executeLocalCommand(command, returnOutput)		
}
