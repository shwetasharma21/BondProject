
var createUsersTable = function () {
	let query = `create table Users(
					user_id serial primary key ,
					name varchar(25),
					email varchar(255) unique not null,
					pass varchar(25) not null)`;
	client.query(query, function (err) {
		if (err) throw err;
		console.log("Successfully query execution");
	});
}
//createStudentTable();


var createMessageTable = function () {
	let query = `create table Message(msg_id serial primary key ,
		sender_id int references Users(user_id),receiver_id int 
		references Users(user_id),msg_txt varchar,msg_time timestamp)`;
	client.query(query, function (err) {
		if (err) throw err;
		console.log("Successfully query execution");
	});
}
//createMessageTable();

var truncateStudentTable = function () {
	let query = "truncate table Student";
	client.query(query, function (err) {
		if (err) throw err;
		console.log("Successfully Reset");
	});
}
//truncateStudentTable();

var allRecords = function () {
	let query = "select * from Users ";
	client.query(query, function (err, results) {
		try {
			if (err) throw err;
			console.log(results.rows);
		}
		catch (err) {
			console.log(err);
		}
	});
}
//allRecords();

var createTeacherInfoTable = function () {
	let query = `create table TeacherInfo(teacher_id integer primary key references Users(user_id),orgName varchar(100) not null,
	orgWebsiteLink varchar(100) not null)`;
	//Added teacher_code after this
	client.query(query, function (err) {
		if (err) throw err;
		console.log("Successful query Execution");
	});
}
//createTeacherInfoTable();


createConnectionTable = function(){
	let query = `CREATE TABLE ConnectionTable(connection_id serial primary key, student_id integer references Users(user_id), 
	teacher_id integer references TeacherInfo(teacher_id), connection_status boolean default '0', UNIQUE(student_id,teacher_id))  `;
	client.query(query,function(err){
		if(err) throw err;
		console.log("Connection Table created successfully");
	});
} 
//createConnectionTable();

