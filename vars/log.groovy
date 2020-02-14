def call(String logMessage){
	println logMessage
}

def call(String logType, String logMessage){
	if (env.LOG_TYPE == logType){
		println logMessage
	}
}

def raiseError(String logMessage){
	error logMessage
}

return this