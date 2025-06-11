package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

// this class uses JDBC to perform CRUD on the project tables

public class ProjectDao extends DaoBase {

	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	// insert a project row into the project table
	// throw DbException if theres an error inserting the row

	public Project insertProject(Project project) {
		String sql = "" + "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) " + "VALUES " + "(?, ?, ?, ?, ?)";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {

		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						projects.add(extract(rs, Project.class));

					}

					return projects;

				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public Optional<Project> fetchProjectById(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try {
				Project project = null;

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));

				}

				commitTransaction(conn);

				// we do Option.ofNullable because the project may be null at this point if the
				// given project id is invalid
				return Optional.ofNullable(project);

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/**
	 * This method retrieves all the categories associated with the given project
	 * ID. Note the inner join to join the category rows to the project_category
	 * join table. The join table is needed because projects and categories have a
	 * many-to-many relationship. Categories can exist on their own without having
	 * associated projects and projects can exist on their own without having any
	 * categories. The join table links the project and category tables together.
	 * 
	 * The connection is supplied by the caller so that the categories can be
	 * returned within the current transaction.
	 * 
	 * @param conn      The Connection object supplied by the caller.
	 * @param projectId The project ID to use for the categories.
	 * @return A list of categories if successful.
	 * @throws DbException Thrown if an exception is thrown by the driver.
	 */
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) {
		// @formatter:off
	    String sql = ""
	        + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
	        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
	        + "WHERE project_id = ?";
	    // @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				}

				return categories;
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	/**
	 * This method uses JDBC method calls to retrieve project steps for the given
	 * project ID. The connection is supplied by the caller so that steps can be
	 * retrieved on the current transaction.
	 * 
	 * @param conn      The caller-supplied connection.
	 * @param projectId The project ID used to retrieve the steps.
	 * @return A list of steps in step order.
	 * @throws SQLException Thrown if the database driver encounters an error.
	 */
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}

				return steps;
			}
		}
	}

	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}

				return materials;
			}
		}
	}

// similar to insertProject Method , SQL UPDATE statement w parameter placeholders, obtain Connection and start transaction, next obtain PreparedStatement object, set 6 parameter values Finally call executeUpdate 
// on the PreparedStatement and commit the transaction

// KEY POINT, is this method examines return value from exectuteUPdate, a single row being acted on should return 1, if its 0 it means no rows were acted on and the Primary Key was not found, true if 1 false if 0

	public boolean modifyProjectDetails(Project project) {
		String sql = ""
	
				+ "UPDATE " + PROJECT_TABLE + " SET "
				+ "project_name = ?, "
				+ "estimated_hours = ?, "
				+ "actual_hours = ?, "
				+ "difficulty = ?, "
				+ "notes = ? "
				+ "WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
				boolean modified = stmt.executeUpdate() == 1;
						commitTransaction(conn);
						
						return modified;
				
		} 
			catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			
			throw new DbException(e);
		}
	}
	
	// test this in the video, pick option 8 without selecting a project to get an error msg
	// then test by selecting a project	

	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);
				
				boolean deleted = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return deleted;
		
} 
	catch (Exception e) {
		rollbackTransaction(conn);
		throw new DbException(e);
	}
} catch (SQLException e) {
	
	throw new DbException(e);
}
}

				
}
	

	


