def call(){
	def pipelineTools = [ : ]
	pipelineTools.tools = tools
	pipelineTools.log = log
	pipelineTools.regex = regex
	pipelineTools.slackNotifier = slackNotifier
	return pipelineTools
}

