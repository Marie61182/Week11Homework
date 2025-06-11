
package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;


public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	//@formatter:off
	
	// adding homework instructions week10 list of projects
	private List<String> operations = List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project",
			"4) Update project details",
			"5) Delete a project"
			
			);
	//@formatter:on
	

	
	
	
	
	// method that processes the menu
	
	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

	/// proccessUsersSelections method, this displays menu selections, get selection from user and acts
	// now building on this adding case 1, page 10 homework
	
	private void processUserSelections() {
		boolean done = false;
		while(!done) {
			try {
				int selection = getUserSelection();
				
				switch(selection) {
				case -1:
					done = exitMenu();
					break;
					
				case 1:
					createProject();
					break;
				case 2:
					listProjects();
					break;
				case 3:
					selectProject();
					break;
					
				case 4:
					updateProjectDetails();
					break;
					
				case 5:
					deleteProject();
					break;
				
				default:
					System.out.println("\n" + selection + "is not a valid selection. Try again.");
				break;
				
				}
			}
			catch(Exception e) {
				System.out.println("\nERROR: " + e + " Try agian.");
				
			}
		}
	
	}
	
private void deleteProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter the ID of the project to delete");
		
		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");
		
		if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

// step one in wk 11 homework CHECKING if curProject is NULL, if so printing msg
	
	private void updateProjectDetails() {
		if(Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}
	// for each FIELD in PROJECT Object print a msg along w current setting in curProject
		String projectName =
				getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
		
		BigDecimal estimatedHours =
				getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
		
		BigDecimal actualHours =
				getDecimalInput("Enter the actual hours + [" + curProject.getActualHours() + "]");
		
		Integer difficulty =
				getIntInput("Enter the project difficulty scale (1 - 5) [" + curProject.getDifficulty() + "]");
	
		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
		
// wk 11 step 1) c. create a new project object
		
	Project project = new Project();
	
// if the user input is not null add the value to the Project Object, if the user input IS NULL add the value from curProject
	
	project.setProjectId(curProject.getProjectId());
	project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName) ;
	
	project.setEstimatedHours(
			Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
	
	project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours );
	project.setDifficulty(Objects.isNull(difficulty) ?  curProject.getDifficulty() : difficulty);
	project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
	
// this is to pick up the changes by calling
	projectService.modifyProjectDetails(project);
	curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	// this is my select project method
private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
	
// this is to throw an exception if an invalid project ID is entered
		curProject = projectService.fetchProjectById(projectId);
		
}

// this is my list project method

private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
		
	}

// method for createProject step 3, part 2 homework	
	private void createProject() {
		 String projectName = getStringInput("Enter the project name");
		    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		    String notes = getStringInput("Enter the project notes");

		    Project project = new Project();

		    project.setProjectName(projectName);
		    project.setEstimatedHours(estimatedHours);
		    project.setActualHours(actualHours);
		    project.setDifficulty(difficulty);
		    project.setNotes(notes);

		    Project dbProject = projectService.addProject(project);
		    System.out.println("You have successfully created project: " + dbProject);

		
		
	}

	
	// Big decimal Method
	
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)){
	return null;

		}
		try	{
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}



	}
	
// Called when user wants to Exit
	
	private boolean exitMenu() {
		System.out.println("Exiting the Menu.");
		return true;
	}

	// step 5 create method getUserSelction 5a)call to the method printOperations 5b) method call to getInput(will return user's selection)
	// 5c) return statement to check if null or it returns input 

	private int getUserSelection() {
		printOprerations();
		
		Integer input = getIntInput("Enter a menu Selection");
		
		return Objects.isNull(input) ? -1 : input;
	}
	
	
	
	// step 6, creating method printOperations, use \n to print on separate lines, I guess this take no parameters and returns nothing it just prints
	// 6b) print all available menu selections, indent each line 2 spaces, I tried the "Lambda expression", I think I need to do a "for each method", or I can do an enhanced for loop, need to ask Mentor or rewatch vids or check solutions
	// I used an enhanced for loop I couldnt get Lambda to work
	
	private void printOprerations() {
	System.out.println("\nThese are available selections. Press the Enter Key to quit:");
	for(String line : operations){
		System.out.println("  " + line);
	}
	
	if(Objects.isNull(curProject)) {
		System.out.println("\nYou are NOT working with a project.");
	}
	else {
		System.out.println("\nYou ARE working with a project: " + curProject);
	}
	}
	
	// step 7 writing a method that returns an integer value, "getInput", accepts input from user and converts to integer, its called by getUserSelection
	// 7a assigning local variable name "input"
	// 7b test value in the variable input
	// 7c try/catch 2 test the value returned can be converted to an integer, catch should except a parameter or type numberformatexception
	
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
			return null;
		}
		
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number. try agian");
			}
	
	}
	
// step 8 create getStringInput (this REALLY prints the prompt and gets the input from user) this will be called on by other input methods
// keep cursor on same line just print not print Ln	

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
		
		
	
	
	
	
}


	

