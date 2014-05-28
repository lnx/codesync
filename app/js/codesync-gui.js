function enableWindowListener() {
	var gui = require('nw.gui');
	var win = gui.Window.get();

	$('li.window-min').click(function() {
		win.minimize();
	});

	$('li.window-adjust').click(function() {
		if ($(this).prop('id') === 'window-max') {
			$(this).prop('id', 'window-normal');
			win.maximize();
		} else if ($(this).prop('id') === 'window-normal') {
			$(this).prop('id', 'window-max');
			win.unmaximize();
		}
	});

	$('li.window-close').click(function() {
		win.close();
	});
}

function enableUserListener() {
	$('button.user-save').click(save);

	$('#user').keyup('s', function(e) {
		if (e.altKey) {
			save();
		}
	});

	function save() {
		$('#user').modal('toggle');
		var host = $('input.user-host').val();
		var port = $('input.user-port').val();
		var username = $('input.user-username').val();
		var password = $('input.user-password').val();
		updateUserProfile(host, port, username, password);
	}
}

function enableAddListener() {
	$('button.add-add').click(add);

	$('#add').keyup('a', function(e) {
		if (e.altKey) {
			add();
		}
	});

	function add() {
		$('#add').modal('toggle');
		var workspace = $('input.add-workspace');
		var name = $('input.add-name');
		addProject(workspace.val(), name.val());
		workspace.val('');
		name.val('');
	}
}

function enableDelListener() {
	$('button.del-delete').click(del);

	$('#del').keyup('d', function(e) {
		if (e.altKey) {
			del();
		}
	});

	function del() {
		$('#del').modal('toggle');
		deleteProject();
	}
}

function enableProjectsListener() {
	var projectElements = $('div.projects').find('a');
	projectElements.click(function() {
		projectElements.removeClass('active');
		$(this).addClass('active');
		refresh();
	});
}

function enableUpdatedFilesListener() {
	$('#check-all').change(function() {
		$('input[class="updated-files"]').prop('checked', $(this).is(':checked'));
	});

	$('input[class="updated-files"]').change(function() {
		var checkAll = true;
		$('input[class="updated-files"]').each(function() {
			if (!$(this).is(':checked')) {
				checkAll = false;
				return false;
			}
		});
		$('#check-all').prop('checked', checkAll);
	});
}

function finishLoading() {
	$('#loading').hide();
	$('#check-all').show();
	var filesNumber = $('input[class="updated-files"]').length;
	if (filesNumber === 0) {
		$('#check-all').prop("disabled", true);
	} else {
		var footElement = $('tfoot').find('tr').find('td');
		if (filesNumber === 1) {
			footElement.html('1 file needs to be updated.');
		} else {
			footElement.html(filesNumber + ' files need to be updated.');
		}
	}
}

$(document).ready(function() {
	enableWindowListener();
	enableUserListener();
	enableAddListener();
	enableDelListener();
	loadUserProfile();
	loadProjects();
});
