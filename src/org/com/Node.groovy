package org.com

/** A map that holds all constants and data members that can be override when constructing  */
nodeEnv

def construct(String user, String hostname, String base_dir, Node jump_server){
	nodeEnv = [
			user : user,
			hostname: hostname,
			base_dir: base_dir,
			jump_server: jump_server
	]
	this.print_object()
}


def print_object() {
	println nodeEnv
}

def get_object() {
	return nodeEnv
}



def execute_command_output(String command, Boolean return_output) {
	if (nodeEnv.jump_server == null) {
		tools.execute_remote_command(nodeEnv.user, nodeEnv.hostname, command, return_output)
	} else {
		def jump_server_node = nodeEnv.jump_server.get_object()
		tools.execute_remote_command_through_jump_server(jump_server_node.user, jump_server_node.hostname, \
															nodeEnv.user, nodeEnv.hostname, command, return_output)
  	}
}

def execute_command(String command) {
	return_output = false
	this.execute_command_output(command, return_output)
}


def execute_command_and_get_output(String command) {
	return_output = true
	stdout = this.execute_command_output(command, return_output)
	return stdout
}


def copy_file_to_node_new(File file, String destination_dir) {
	file_attr = file.get_object()
	file_node = file_attr.node
	file_node_ = file_node.get_object()
	if (!nodeEnv.jump_server) {
		if (this.file_exists(file, destination_dir)) {
			return false
		}
		if (!file_node_.jump_server) {
			tools.copy_file_to_node(file_node_.user, file_node_.hostname, file_attr.file_full_path, nodeEnv.user, nodeEnv.hostname, destination_dir)
		} else {
			//first, copy the file to the jump server from the jump server's side
			jp_server_ = file_node_.jump_server.get_object()
			tools.copy_file_from_node(file_node_.user, file_node_.hostname, file_attr.file_full_path, jp_server_.user, jp_server_.hostname, jp_server_.base_dir)
			//now, copy the file from the jump_server to the node
			new_file = fileObject(file_attr.name, file_node_.jump_server, jp_server_.base_dir)
			this.copy_file_to_node_new(new_file, destination_dir)
		}
	} else {
		jp_node = nodeEnv.jump_server
		jump_server = jp_node.get_object()
		if (!jp_node.file_exists(file, jump_server.base_dir)) {
			jp_node.copy_file_to_node_new(file, jump_server.base_dir)
		}
		new_file = fileObject(file_attr.name, jp_node, jump_server.base_dir)
		file_full_path = new_file.get_object().file_full_path
		tools.copy_file_to_node(jump_server.user, jump_server.hostname, file_full_path, nodeEnv.user, nodeEnv.hostname, destination_dir)
	}
	return true
}


def copy_file_to_node(file, String destination_dir) {
	if (nodeEnv.jump_server == null) {
		tools.copy_file_to_node_bkp(file, nodeEnv.user, nodeEnv.hostname, destination_dir)
	} else {
		return_output = false
		def jump_server_node = nodeEnv.jump_server
		def jump_server_node_attributes = nodeEnv.jump_server.get_object()
		tools.copy_file_to_node_bkp(file, jump_server_node_attributes.user, jump_server_node_attributes.hostname, jump_server_node_attributes.base_dir)
		def file_location = jump_server_node_attributes.base_dir + "/" + file.tokenize('/')[ file.tokenize('/').size() - 1 ]
		def cmd = "scp " + file_location + " " + nodeEnv.user + "@" + nodeEnv.hostname + ":" + destination_dir
		tools.execute_remote_command(jump_server_node_attributes.user,jump_server_node_attributes.hostname, cmd, return_output)
  	}
}


//it takes as input the build object, that is, the buildEnv map
def copy_release(Release release, File release_file) {
	release = release.get_object()
	command = "cd ${nodeEnv.base_dir}; if [ -e ${release.filename} ]; then rm -f ${release.filename}; fi"
	this.execute_command(command)
	command = "cd ${nodeEnv.base_dir}; if [ -e ${release.name} ]; then rm -rf ${release.name}; fi"
	this.execute_command(command)
	this.copy_file_to_node_new(release_file, nodeEnv.base_dir)
}

def check_if_directory_exists(String directory){
	command = "if [ -e ${directory} ]; then echo true; else echo false; fi"
    stdout = this.execute_command_and_get_output(command)
    return stdout
}

def file_exists(File file, String directory){
	file = file.get_object()
	command = "cd ${directory}; if [ -e ${file.name} ]; then echo true; else echo false; fi"
    stdout = this.execute_command_and_get_output(command)
	if (stdout != 'true'){
		return false
	}
	println "File exists. Is the same file?"
	cksum_command = "cd ${directory}; cksum ${file.name}"
	cksum = this.execute_command_and_get_output(cksum_command)
	if (cksum == file.cksum) {
		println "They are the same file"
		return true
	}
	println "They are not the same file"
	return false
}



return this



