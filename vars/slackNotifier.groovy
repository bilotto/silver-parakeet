#!/usr/bin/env groovy

def call(String message, String channel) {
    slackSend color: "good", channel: "${channel}", message: "${message}"
}

def bkp(String buildResult, String channel) {
  if ( buildResult == "SUCCESS" ) {
    slackSend color: "good", channel: "${channel}", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was successful"
  }
  else if( buildResult == "FAILURE" ) { 
    slackSend color: "danger", channel: "${channel}", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was failed"
  }
  else if( buildResult == "UNSTABLE" ) { 
    slackSend color: "warning", channel: "${channel}", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} was unstable"
  }
  else {
    slackSend color: "danger", channel: "${channel}", message: "Job: ${env.JOB_NAME} with buildnumber ${env.BUILD_NUMBER} its resulat was unclear"	
  }
}

def stackSlackMessage_bkp(message){
  if (env.SLACK_MESSAGE) {
    env.SLACK_MESSAGE = message
  } else {
    env.SLACK_MESSAGE = env.SLACK_MESSAGE + "\n" + message
  }
}

def stackSlackMessage(message){
	if (env.SLACK_MESSAGE) {
    	env.SLACK_MESSAGE = env.SLACK_MESSAGE + message
    }
}