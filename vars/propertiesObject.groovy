import org.com.Properties

def call(properties_map) {
	Properties properties = new Properties()
	properties.construct(properties_map)
	return properties
}

def generate_properties_map_old(properties_files_dir){
	properties_files = sh (returnStdout: true, script: "ls ${properties_files_dir}")
	properties_map = [ : ]
	properties_files.tokenize("\n").each { fileName ->
		dot_char = fileName.indexOf(".")
		key = fileName[ 0..(dot_char - 1) ].toUpperCase()
		writeFile(file: "${fileName}", text: libraryResource("org/foo/${fileName}"), encoding: "UTF-8")
		yaml_map = readYaml file: "${fileName}"
		//todo: check the filename and mapkey relation
		properties_map.put(key, yaml_map.get(key))
	}
	return properties_map
}

def generate_properties_map(properties_files_dir){
	properties_files = sh (returnStdout: true, script: "cd ${properties_files_dir}; find . -type f")
	properties_map = [ : ]

	properties_files.tokenize("\n").each { fileRelativePath ->
		file_dir = properties_files_dir.drop("resources/".length())
		base_map = properties_map

		fileRelativePath = fileRelativePath.drop("./".length())
		slash_char = fileRelativePath.indexOf("/")
		last_it_key = null

		while (slash_char != -1) {
			folder = fileRelativePath[ 0..(slash_char - 1) ]
			key = folder.toUpperCase()
			file_dir = file_dir + "/" + folder
			if (!last_it_key) {
				if (!properties_map.get(key)) {
					properties_map.put(key, [:])
				}
				base_map = properties_map.get(key)
			} else {
				base_map = properties_map.get(last_it_key)
				if (!base_map.get(key)) {
					base_map.put(key, [:])
				}
				base_map = base_map.get(key)
			}
			fileRelativePath = fileRelativePath.drop("${key}/".length())
			slash_char = fileRelativePath.indexOf("/")
			last_it_key = key
		}
		fileName = fileRelativePath
		dot_char = fileName.indexOf(".")
		key = fileName[ 0..(dot_char - 1) ].toUpperCase()
		writeFile(file: "${fileName}", text: libraryResource("${file_dir}/${fileName}"), encoding: "UTF-8")
		yaml_map = readYaml file: "${fileRelativePath}"
		if (!yaml_map.get(key)) {
			error "Properties file ${fileRelativePath} must contain the same name as its first key"
		}
		base_map.put(key, yaml_map.get(key))


	}
	return properties_map
}