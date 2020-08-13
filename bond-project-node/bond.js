const express = require('express');
const app = express();
const parser = require('body-parser');
const { Client } = require('pg');

const port = process.env.PORT;
const connectionString = process.env.DATABASE_URL;

const client = new Client({
	connectionString: connectionString,
	ssl: true
});
client.connect(function (err) {
	if (err) throw err;
	console.log("Connected to database");
});

app.use(parser.urlencoded({ extended: true }));

//admin console for database
var executeQuery = function () {
	let query = "SELECT * FROM ConnectionTable";
	client.query(query, function (err, results) {
		if (err) throw err;
		console.log("Successfull");
		console.log(results.rows);
	});
}
//executeQuery();

var insertMessageTable = function (sender_id, receiver_id, msg_txt, msg_time, callback) {
	let query = "insert into Message(sender_id,receiver_id,msg_txt,msg_time) values($1, $2, $3, $4)";
	console.log(query);
	client.query(query, [sender_id, receiver_id, msg_txt, msg_time], function (err) {
		try {
			if (err) throw err;
			console.log("Inserted");
			callback(1);
		}
		catch (err) {
			console.log("Error Occured" + err);
			callback(-1);
		}
	});
}

var selectMessageReceive = function (sender_id, receiver_id, callback) {
	let query = "select * from Message where (sender_id = $1 and receiver_id = $2) or (sender_id = $2 and receiver_id = $1)";
	client.query(query, [sender_id, receiver_id], function (err, results) {
		try {
			if (err) throw err;
			console.log(results.rows);
			callback(1, results.rows);
		}
		catch (err) {
			console.log("error" + err);
			callback(-1, null);
		}
	});
}

var insertTeacherInfoTable = function (teacher_id, orgName, orgWebsiteLink, callback) {
	let query = "insert into TeacherInfo(teacher_id,orgName,orgWebsiteLink) values($1, $2, $3)";
	console.log(query);
	client.query(query, [teacher_id, orgName, orgWebsiteLink], function (err) {
		try {
			if (err) throw err;
			console.log("Data Inserted");
			callback(1);
		}
		catch (err) {
			console.log("Error Occured:" + err);
			callback(-1);
		}
	});
}

var insertUsersTable = function (name, email, pass, status_code, callback) {
	let query = "insert into Users(name,email,pass,status_code) values($1, $2, $3, $4)";
	console.log(query);
	client.query(query, [name, email, pass, status_code], function (err) {
		try {
			if (err) throw err;
			console.log("Inserted");
			callback(1);
		}
		catch (err) {
			console.log("Duplicate values found");
			callback(-1);
		}
	});
}

var selectUsersTable = function (email, pass, status_code, callback) {
	let query = "select name, user_id from Users where email = $1 and pass = $2 and status_code = $3";
	client.query(query, [email, pass, status_code], function (err, results) {
		try {
			if (err) throw err;
			if (results.rows.length != 0) {
				console.log(results.rows[0]);
				console.log("Welcome User");
				callback(1, results.rows[0]);
			}
			else {
				console.log("Record does not exist");
				callback(0);
			}
		}
		catch (err) {
			console.log("Error");
			callback(-1);
		}
	});
}

var getTeacherId = function (email, callback) {
	let query = "select user_id from Users where email = $1";
	client.query(query, [email], function (err, results) {
		try {
			if (err) throw err;
			console.log(results);
			callback(1, results.rows[0].user_id);
		}
		catch (err) {
			console.log("Error occured" + err);
			callback(-1, null);
		}
	})
}

app.get('/', function (req, res) {
	res.send("Welcome to bond");
});

app.post('/contacts', function (req, res) {
	let user_id = req.body.user_id;
	if (!user_id) {
		res.send("Insufficient data");
	}
	let query = "SELECT name,status_code FROM Users WHERE user_id = $1 "
	client.query(query, [user_id], function (err, results) {
		try {
			if (err) throw err;
			console.log(results.rows);
			let status_code = results.rows[0].status_code;
			if (status_code === '0') {
				query = `SELECT t2.name,t1.teacher_id FROM ConnectionTable AS t1 JOIN Users As t2 ON t1.teacher_id = t2.user_id
						WHERE t1.connection_status = '1' AND t1.student_id = $1`;
			}

			else if (status_code === '1') {
				query = `SELECT t2.name,t1.student_id FROM ConnectionTable AS t1 JOIN Users As t2 ON t1.student_id = t2.user_id
				WHERE t1.connection_status = '1' AND t1.teacher_id = $1`;
			}
			client.query(query, [user_id], function (err, results) {
				try {
					if (err) throw err;
					console.log(results.rows);
					res.send(results.rows);
				} catch (err) {
					console.log(err);
					res.send("Oops..! Something went wrong.");
				}

			});


		} catch (err) {
			console.log("Error occurred" + err);
			res.send("Oops..! Something went wrong.");
		}
	});

});
app.post('/processRequest', function (req, res) {
	let req_id = req.body.req_id;
	let req_status = req.body.req_status;

	if (!req_id || !req_status) {
		res.send("Insufficient data");
	}
	let query;
	//Accept request
	if (req_status === '1') {
		query = "UPDATE ConnectionTable SET connection_status = '1' WHERE connection_id = $1";
	}
	//Reject request
	else if (req_status === '0') {
		query = "DELETE FROM ConnectionTable WHERE connection_id = $1";
	}
	client.query(query, [req_id], function (err) {
		try {
			if (err) throw err;
			if (req_status === '1') {
				console.log("Request accepted");
				res.send("Request accepted");
			}
			else if (req_status === '0') {
				console.log("Request rejected");
				res.send("Request rejected");
			}
		} catch (err) {
			console.log("Request Error");
			res.send("Oops..! An error occurred");
		}
	})
});
app.post('/sendRequest', function (req, res) {
	let student_id = req.body.student_id;
	let teacher_id = req.body.teacher_id;
	if (!student_id || !teacher_id) {
		res.send("Insufficient data");
	}
	let query = "INSERT INTO ConnectionTable(student_id, teacher_id) VALUES($1,$2)";
	client.query(query, [student_id, teacher_id], function (err) {
		try {
			if (err) throw err;
			console.log("Record inserted in connection table");
			res.send("Request Sent");
		} catch (err) {
			console.log("Error Occurred :" + err);
			res.send("Failed to send request");
		}
	})
});

app.post('/receiveRequest', function (req, res) {
	let teacher_id = req.body.teacher_id;
	let student_id = req.body.student_id;
	let conn_id = req.body.conn_id;
	if (!teacher_id) {
		res.send("Insufficient data");
	}
	let query = "SELECT t1.connection_id, t2.name FROM ConnectionTable AS t1 JOIN Users AS t2 ON teacher_id =$1 AND t1.student_id = t2.user_id";
	client.query(query, [teacher_id], function (err, results) {
		try {
			if (err) throw err;
			console.log(results.rows);
			res.send(results.rows);
		} catch (err) {
			console.log(err);
			res.send("Error Occurred");
		}
	});
});

app.post('/teacherProfile', function (req, res) {
	console.log(JSON.stringify(req.body));
	let teacher_id = req.body.teacher_id;
	if (!teacher_id) {
		res.send("Insufficient data");
	}
	let query = "select t1.name,t1.email,t2.orgName,t2.orgWebsiteLink,t2.teacher_code from Users AS t1 INNER JOIN TeacherInfo AS t2 ON t1.user_id = t2.teacher_id WHERE t2.teacher_id = $1";
	client.query(query, [teacher_id], function (err, results) {
		try {
			if (err) throw err;
			res.send(results.rows[0]);
			console.log(results.rows[0]);
		}
		catch (err) {
			res.send("Error occured");
			console.log("Error:" + err);
		}
	});
});

app.post('/teacherSearch', function (req, res) {
	console.log(JSON.stringify(req.body));
	let teacher_code = req.body.teacher_code;
	if (!teacher_code) {
		res.send("Insufficient data");
		return;
	}
	let query = "SELECT t1.name,t2.teacher_id,t2.orgName,t2.orgWebsiteLink FROM Users AS t1 JOIN TeacherInfo AS t2 ON t2.teacher_code = $1";
	client.query(query, [teacher_code], function (err, results) {
		try {
			if (err) throw err;
			console.log(results.rows[0]);
			if (results.rows[0]) {
				res.send(results.rows[0]);
			}
			else {
				res.send("No record found");
			}
		}
		catch (err) {
			console.log("Error " + err);
			res.send("Error occurred");
		}
	});
});
app.post('/teacherUpdate', function (req, res) {
	console.log(JSON.stringify(req.body));
	let teacher_id = req.body.teacher_id;
	let teacher_code = req.body.teacher_code;
	let orgName = req.body.orgName;
	let orgWebsiteLink = req.body.orgWebsiteLink;
	let query;
	let data;

	if (!teacher_id) {
		res.send("Insufficient data");
		return;
	}
	if (teacher_code) {
		query = "update TeacherInfo set teacher_code = $1 where teacher_id =$2";
		data = teacher_code;
	}
	else if (orgName) {
		query = "update TeacherInfo set orgName = $1 where teacher_id =$2";
		data = orgName;
	}
	else if (orgWebsiteLink) {
		query = "update TeacherInfo set orgWebsiteLink = $1 where teacher_id =$2";
		data = orgWebsiteLink;
	}
	else {
		res.send("Insufficient data");
	}
	console.log(query + " data :" + data);
	client.query(query, [data, teacher_id], function (err) {
		try {
			if (err) throw err;
			res.send("Updated Successfully");
			console.log("Updated successfully");
		}
		catch (err) {
			res.send("Error occurred");
			console.log("Error" + err);
		}
	});
});

app.post('/messageReceive', function (req, res) {
	console.log(JSON.stringify(req.body));
	let sender_id = req.body.sender_id;
	let receiver_id = req.body.receiver_id;
	if (!sender_id || !receiver_id) {
		res.send("Insufficient data");
		return;
	}
	selectMessageReceive(sender_id, receiver_id, function (status, results) {
		if (status === 1) {
			console.log(results);
			res.send(results);
		}
		else {
			res.send("[]");
		}
	});
});

app.post('/messageSend', function (req, res) {
	console.log(JSON.stringify(req.body));
	let sender_id = req.body.sender_id;
	let receiver_id = req.body.receiver_id;
	let msg_txt = req.body.msg_txt;
	let msg_time = req.body.msg_time;
	if (!sender_id || !receiver_id || !msg_txt || !msg_time) {
		res.send("Insufficient data");
		return;
	}
	insertMessageTable(sender_id, receiver_id, msg_txt, msg_time, function (status) {
		if (status === 1) {
			res.send("sent");
		}
		else {
			res.send("error");
		}
	});
});

app.post('/login', function (req, res) {
	// console.log(JSON.stringify(req.body));
	let email = req.body.email;
	let pass = req.body.pass;
	let status_code = req.body.status_code;
	if (!email || !pass || !status_code) {
		res.send("Insufficient data");
		return;
	}
	selectUsersTable(email, pass, status_code, function (status, results) {
		if (status === 1) {
			res.send(results);
		}
		else if (status === 0) {
			res.send("Record does not exist");
		}
		else {
			res.send("Error occured");
		}
	});
});

app.post('/register', function (req, res) {
	// console.log(JSON.stringify(req.body));
	let name = req.body.name;
	let email = req.body.email;
	let pass = req.body.pass;
	let status_code = req.body.status_code;
	if (!email || !pass || !status_code) {
		res.send("Insufficient data");
		return;
	}
	if (status_code == 0) {
		insertUsersTable(name, email, pass, status_code, function (status) {
			if (status === 1) {
				res.send("Successfully registered");
			}
			else {
				res.send("Record already exists");
			}
		});
	}
	else {
		let orgName = req.body.orgName;
		let orgWebsiteLink = req.body.orgWebsiteLink;
		if (!name || !orgName || !orgWebsiteLink) {
			res.send("Insufficient data");
			return;
		}
		insertUsersTable(name, email, pass, status_code, function (status) {
			if (status === 1) {
				getTeacherId(email, function (status2, teacher_id) {
					console.log(status2 + " :" + teacher_id);
					if (status2 === 1) {
						insertTeacherInfoTable(teacher_id, orgName, orgWebsiteLink, function (status3) {
							if (status3 === 1) {
								res.send("Successfully Registered");
							}
							else {
								res.send("Error Occured");
							}
						});
					}
				});
			}
			else {
				res.send("Record already exists");
			}
		});
	}
});

app.listen(port, function (err) {
	if (err) throw err;
	console.log("Server is listening at port " + port);
});
