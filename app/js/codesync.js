function loadUserProfile() {
	var fs = require('fs');
	fs.readFile('app/user.json', function(error, data) {
		var user = data;
		if (error) {
			console.log(error);
		} else {
			user = JSON.parse(user);
		}
		var template = Handlebars.compile($('script.user').html());
		$('div.user').html(template(user));
	});
}

function updateUserProfile(host, port, username, password) {
	var user = {
		'host': host.trim(),
		'port': port.trim(),
		'username': username.trim(),
		'password': password.trim()
	}
	var fs = require('fs');
	fs.writeFile('app/user.json', JSON.stringify(user, null, 4), function(error) {
		if (error) {
			console.log(error);
		} else {
			loadUserProfile();
		}
	});
}

function addProject(workspace, name) {
	var fs = require('fs');
	fs.readFile('app/projects.json', function(error, data) {
		workspace = workspace.trim().replace(/\\/g, '/');
		name = name.trim();
		var projects = data;
		if (error) {
			console.log(error);
			projects = {
				'projects': [{
					'workspace': workspace,
					'name': name
				}]
			};
		} else {
			projects = JSON.parse(projects);
			projects.projects.push({
				'workspace': workspace,
				'name': name
			});
		}
		fs.writeFile('app/projects.json', JSON.stringify(projects, null, 4), function(error) {
			if (error) {
				console.log(error);
			} else {
				loadProjects();
			}
		});
	});
}

function deleteProject() {
	var fs = require('fs');
	fs.readFile('app/projects.json', function(error, data) {
		var projects = data;
		if (error) {
			console.log(error);
			projects = {
				'projects': []
			};
		} else {
			projects = JSON.parse(projects);
			var updatedProjects = [];
			if (projects.projects != null) {
				var selectedProject = getSelectedProject();
				for (var i = 0; i < projects.projects.length; i++) {
					var project = projects.projects[i];
					if (project.workspace != selectedProject.workspace || project.name != selectedProject.name) {
						updatedProjects.push(project);
					}
				}
			}
			projects.projects = updatedProjects;
		}
		fs.writeFile('app/projects.json', JSON.stringify(projects, null, 4), function(error) {
			if (error) {
				console.log(error);
			} else {
				loadProjects();
			}
		});
	});
}

function loadProjects() {
	var fs = require('fs');
	fs.readFile('app/projects.json', function(error, data) {
		var projects = data;
		if (error) {
			console.log(error);
		} else {
			projects = JSON.parse(projects);
		}
		var template = Handlebars.compile($('script.projects').html());
		$('div.projects').html(template(projects));
		$('a.list-group-item:first-child').addClass('active');
		enableProjectsListener();
		refresh();
	});
}

function refresh() {
	var selectedProject = getSelectedProject();
	refreshDelete(selectedProject.workspace, selectedProject.name);
	refreshUpdatedFiles(selectedProject.workspace, selectedProject.name);
}

function refreshDelete(workspace, name) {
	var template = Handlebars.compile($('script.del').html());
	$('div.del').html(template({
		'workspace': workspace,
		'name': name
	}));
}

function refreshUpdatedFiles(workspace, name) {
	var template = Handlebars.compile($('script.updated-files').html());
	$('div.updated-files').html(template({
		"updatedFiles": []
	}));
	var exec = require('child_process').exec;
	var child = exec('java -jar app/java/FileChecker.jar ' + workspace + ' ' + name, function(error, stdout, stderr) {
		var updatedFiles = stdout;
		if (error) {
			console.log(error);
		} else {
			updatedFiles = JSON.parse(updatedFiles);
		}
		$('div.updated-files').html(template(updatedFiles));
		finishLoading();
		enableUpdatedFilesListener();
	});
}

function getSelectedProject() {
	var project = {
		'workspace': '',
		'name': ''
	};
	var activeElement = $('div.projects').find('a.active');
	if (activeElement != null) {
		project.workspace = activeElement.data('workspace');
		project.name = activeElement.data('name');
	}
	return project;
}
