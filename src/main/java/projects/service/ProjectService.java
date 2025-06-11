/**
 * 
 */
package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

/** this Class is implementing the service layer, remember this is a 3-tier application. In this case the CRUD (create,read,ect) are so simple that this acts mainly as a pss-through from
 * from the input layer to the data layer
 */
public class ProjectService {
	private ProjectDao projectDao = new ProjectDao();
	
	// this Method calls the DAO class to insert a project row

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
		
	}

	// this method call the DAO to get the project details like materials steps and categories, throws exception if project ID is invalid
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException("Project with project ID=" + projectId + " does not exist. "));
		
	
	}
	
	// This method calls the DAO to retrieve all project rows not details though

	public List<Project> fetchAllProjects() {
		
		return projectDao.fetchAllProjects();
	}
	
// week 11 homework, "Project Service.Java" step 1 a & b calling the project.Dao passing object as a parameter, boolean that indicates if the UPDATE worked, throw DbExcepton if it didn't work " does not exist" 
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		
		
	}

	
		
	}

	public void deleteProject(Integer projectId) {
	if(!projectDao.deleteProject(projectId)) {
		throw new DbException ("Project with ID=" + projectId + " does not exist. ");
	}
		
	}

}
