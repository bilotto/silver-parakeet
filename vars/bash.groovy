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


def executeLocalCommand_bkp(command, returnOutput){
	if (!returnOutput) {
		sh "${command}"
	} else {
       def stdout = sh (returnStdout: true, script: "${command}").trim().toString()
       return stdout
  	}
}


def executeLocalCommand(command, returnOutput){
	commandResult = this.runCmdOnNodeSavingExitCodeAndStdout(command)
	if (returnOutput) {
		return commandResult[ 1 ]
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


def transferFileBetweenHosts(String sourceUser, String sourceHostname, String fileFullPath, String destinationUser, String destinationHostname, String destinationDir) {
	def cmd = """
				scp ${fileFullPath} ${destinationUser}@${destinationHostname}:${destinationDir}
			"""
	returnOutput = false
	this.executeRemoteCommand(sourceUser, sourceHostname, cmd, return_output)
}



def getTempDirOnNode() {
	return env.TMPDIR != null ? env.TMPDIR : '/tmp'
}

/*  May not work if "cmd" already contains output redirection or more complex shell syntax. */
def runCmdOnNodeSavingExitCodeAndStdout(cmd) {
    def rc = 0
    def stdout = null
    def tempFileName = 'runCmdOnNodeSavingExitCodeAndStdout_' + UUID.randomUUID() + '.txt'
    def tempFilePath = this.getTempDirOnNode() + "/" + tempFileName
    
    rc = sh(script: cmd + ' > ' + tempFilePath, returnStatus: true)
    stdout = readFile(tempFilePath).trim()
    
    log("DEBUG", "stdout: ${stdout}")

    // Delete temporary file from the node
	sh(script: 'rm -f ' + tempFilePath, returnStatus: true)
    
    return [ rc, stdout ]
}
