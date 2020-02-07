def make_command(cmd){
	cmd = cmd.replace("\$", "\\\$")
	cmd = cmd.replace("\"", "\\\"")
	return cmd
}

def filename_from_filepath(fileFullPath){
	filename = fileFullPath.tokenize('/')[ fileFullPath.tokenize('/').size() - 1 ]
	return filename
}


def execute_remote_command(remote_user, remote_hostname, remote_command, return_output) {
	remote_command = this.make_command(remote_command)
	def cmd = """
				ssh ${remote_user}@${remote_hostname} \
				\"
					${remote_command}
				\"
			"""
	if (return_output) {
	       stdout = sh (returnStdout: true, script: "${cmd}").trim()
	       return stdout.toString()
	} else {
	      sh "${cmd}"
  	}
}

def execute_remote_command_through_jump_server(jump_server_user, jump_server_hostname, remote_user, remote_hostname, remote_command, return_output) {
	remote_command = this.make_command(remote_command)
	def cmd = """
				ssh ${jump_server_user}@${jump_server_hostname} \
				\"
					ssh ${remote_user}@${remote_hostname} \
					\\\"
						${remote_command}
					\\\"
				\"
			"""
	if (return_output) {
	       stdout = sh (returnStdout: true, script: "${cmd}").trim()
	       return stdout.toString()
	} else {
	      sh "${cmd}"
  	}
}

def execute_remote_command_through_jump_server_bkp(jump_server_user, jump_server_hostname, remote_user, remote_hostname, remote_command, return_output) {
	remote_command = this.make_command(remote_command)
	def cmd = """
				ssh ${jump_server_user}@${jump_server_hostname} \
				\"
					ssh ${remote_user}@${remote_hostname} \
					\\\"
						${remote_command} 1>&2
					\\\"
				\"
			"""
	if (return_output) {
	       stdout = sh (returnStdout: true, script: "${cmd}").trim()
	       return stdout.toString()
	} else {
	      sh "${cmd}"
  	}
}

def copy_file_to_node(src_user, src_hostname, file_path, dest_user, dest_hostname, destination_dir) {
	def cmd = """
				scp ${file_path} ${dest_user}@${dest_hostname}:${destination_dir}
			"""
	return_output = false
	this.execute_remote_command(src_user, src_hostname, cmd, return_output)
}

def copy_file_from_node(src_user, src_hostname, file_path, dest_user, dest_hostname, destination_dir) {
	def cmd = """
				scp ${src_user}@${src_hostname}:${file_path} ${destination_dir}
			"""
	return_output = false
	this.execute_remote_command(dest_user, dest_hostname, cmd, return_output)
}

def copy_file_to_node_bkp(file_path, remote_user, remote_hostname, destination_dir) {
	def cmd = """
				scp ${file_path} ${remote_user}@${remote_hostname}:${destination_dir}
			"""
	sh (returnStdout: true, script: "${cmd}").trim()

}


return this
