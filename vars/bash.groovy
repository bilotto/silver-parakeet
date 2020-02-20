def stringToCommand(cmd){
	log ("DEBUG", "cmd before: ${cmd}")
	cmd = cmd.replace("\$", "\\\$")
	cmd = cmd.replace("\"", "\\\"")
	log ("DEBUG", "cmd after: ${cmd}")
	return cmd
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
	if (returnOutput) {
	       stdout = sh (returnStdout: true, script: "${cmd}").trim().toString()
	       return stdout
	} else {
	      sh "${cmd}"
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
