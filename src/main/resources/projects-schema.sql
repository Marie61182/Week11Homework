//Im adding the DROP TABLE Statements, need to DROp the TABLES in CORRECT ORDER, FOREIGN KEY references to other tables must be DROPPED FIRST 

DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS project;


// Im writing the CREATE TABLE statements in INVERSE of the order they were DROPPED so im starting with PROJECT
// Im including AUTO INCREMENTING the PRIMARY KEY Columns, PRIMARY KEY Statements FOreign key statements with ON DELETE CASCADE, need to remember to CLOSE each DROP AND CREATE TABLE with a semicolon
// DELETE CASCADE in STEP TABLE MEANS delete all steps associated with a paticular project?

CREATE TABLE project(
	project_id INT NOT NULL AUTO_INCREMENT,
	project_name VARCHAR(128) NOT NULL,
	estimated_hours DECIMAL(7,2),
	actual_hours DECIMAL(7,2,
	difficulty INT,
	notes TEXT,
	
	PRIMARY KEY (project_id)
);	

CREATE TABLE category (
 category_id INT NOT NULL AUTO_INCREMENT,
 category_name VARCHAR(128) NOT NULL,
 PRIMARY KEY (category_id)
);

CREATE TABLE step (
	step_id INT NOT NULL AUTO_INCREMENT,
	project_id INT NOT NULL,
	step_text TEXT NOT NULL,
	step_order INT NOT NULL,
	PRIMARY KEY (step_id),
	FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE material (
 material_id INT AUTO_INCREMENT NOT NULL,
 project_id INT NOT NULL,
 material_name VARCHAR(128) NOT NULL,
 num_required INT,
 cost DECIMAL(7,2),
 PRIMARY KEY (material_id),
 FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
 
 
);

CREATE TABLE project_category (
 project_id INT AUTO_INCREMENT NOT NULL,
 category_id INT NOT NULL,
 FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE CASCADE,
 FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE,
 UNIQUE KEY (project_id, category_id)
);
 
 
 
 