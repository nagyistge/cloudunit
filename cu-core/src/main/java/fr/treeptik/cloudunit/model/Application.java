package fr.treeptik.cloudunit.model;

/*
* LICENCE : CloudUnit is available under the GNU Affero General Public License : https://gnu.org/licenses/agpl.html
* but CloudUnit is licensed too under a standard commercial license.
* Please contact our sales team if you would like to discuss the specifics of our Enterprise license.
* If you are not sure whether the AGPL is right for you,
* you can always test our software under the AGPL and inspect the source code before you contact us
* about purchasing a commercial license.
*
* LEGAL TERMS : "CloudUnit" is a registered trademark of Treeptik and can't be used to endorse
* or promote products derived from this project without prior written permission from Treeptik.
* Products or services derived from this software may not be called "CloudUnit"
* nor may "Treeptik" or similar confusing terms appear in their names without prior written permission.
* For any questions, contact us : contact@treeptik.fr
*/

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.treeptik.cloudunit.utils.AlphaNumericsCharactersCheckUtils;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "name", "cuInstanceName" }))
public class Application implements Serializable {

	public static final String ALREADY_DEPLOYED = "ALREADY_DEPLOYED";

	public static final String NONE = "NONE";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private String displayName;

	/**
	 * CloudUnit instance name (e.g. DEV, QA, PROD).
	 */
	private String cuInstanceName;

	/**
	 * Origin property issue from snapshot when created by clone process.
	 */
	private String origin;

	@Enumerated(EnumType.STRING)
	private Status status;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "YYYY-MM-dd HH:mm")
	private Date date;

	@ManyToOne
	private User user;

	@OrderBy("id asc")
	@OneToMany(mappedBy = "application", fetch = FetchType.LAZY)
	private Set<Module> modules;

	@OneToOne(mappedBy = "application", fetch = FetchType.LAZY)
	private Server server;

	@OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Set<Deployment> deployments;

	@ElementCollection
	private Set<String> aliases;

	/**
	 * Suffixe du domaine des applications déployées Ce champ est dynamique à
	 * travers le profil maven
	 */
	private String suffixCloudUnitIO;

	private String domainName;

	private String managerIp;

	private String managerPort;

	// version de java sous laquelle tourne les containers serveurs et git
	// (maven)
	private String jvmRelease;

	@JsonIgnore
	private String restHost;

	private String deploymentStatus;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	private String contextPath;

	private boolean isAClone;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
	@OrderBy(value = "port")
	private Set<PortToOpen> portsToOpen;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "application")
	private Set<EnvironmentVariable> environmentVariables;

	private String location;

	public Application() {
		super();
		date = new Date();
		isAClone = false;
		deploymentStatus = Application.NONE;
	}

	public Application(Integer id, String name, String cuInstanceName, User user, List<Module> modules) {
		super();
		this.id = id;
		this.name = name;
		this.cuInstanceName = cuInstanceName;
		this.user = user;
		this.modules = new HashSet<>(modules);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name.toLowerCase();
		this.name = AlphaNumericsCharactersCheckUtils.convertToAlphaNumerics(name);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCuInstanceName() {
		return cuInstanceName;
	}

	public void setCuInstanceName(String cuInstanceName) {
		this.cuInstanceName = cuInstanceName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public void removeServer() {
		server.setApplication(null);
		this.server = null;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Module> getModules() {
		if (modules != null) {
			return new ArrayList<>(modules);
		} else {
			return new ArrayList<>();
		}
	}

	public void setModules(List<Module> modules) {
		this.modules = new HashSet<>(modules);
	}

	public void removeModule(Module module) {
		module.setApplication(null);
		modules.remove(module);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Deployment> getDeployments() {
		if (deployments != null) {
			return new ArrayList<>(deployments);
		} else {
			return new ArrayList<>();
		}
	}

	public void setDeployments(List<Deployment> deployments) {
		this.deployments = new HashSet<>(deployments);
	}

	public void addDeployment(Deployment deployment) { this.deployments.add(deployment); }

	public String getSuffixCloudUnitIO() {
		return suffixCloudUnitIO;
	}

	public void setSuffixCloudUnitIO(String suffixCloudUnitIO) {
		this.suffixCloudUnitIO = suffixCloudUnitIO;
	}

	public String getManagerIp() {
		return managerIp;
	}

	public void setManagerIp(String managerIp) {
		this.managerIp = managerIp;
	}

	public String getManagerPort() {
		return managerPort;
	}

	public void setManagerPort(String managerPort) {
		this.managerPort = managerPort;
	}

	public String getLocation() {
		location = "http://" + name + "-" + user.getLogin() + "-" + user.getOrganization() + suffixCloudUnitIO;
		return location;
	}

	public Set<String> getAliases() {
		return aliases;
	}

	public void setAliases(Set<String> aliases) {
		this.aliases = aliases;
	}

	public String getJvmRelease() {
		return jvmRelease;
	}

	public void setJvmRelease(String jvmRelease) {
		this.jvmRelease = jvmRelease;
	}

	@Override
	public String toString() {
		return "Application{" + "id=" + id + ", name='" + name + '\'' + ", status=" + status + ", date=" + date
				+ ", user=" + user + ", domainName='" + domainName + '\'' + ", managerIP='" + managerIp + '\''
				+ ", managerPort='" + managerPort + '\'' + ", jvmRelease='" + jvmRelease + '\'' + ", restHost='"
				+ restHost + '\'' + ", deploymentStatus='" + deploymentStatus + '\'' + ", suffixCloudUnitIO='"
				+ suffixCloudUnitIO + '\'' + ", isAClone=" + isAClone + ", cuInstanceName=" + cuInstanceName
				+ ", origin=" + origin + '\'' + '}';
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Application other = (Application) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public boolean isAClone() {
		return isAClone;
	}

	public void setAClone(boolean isAClone) {
		this.isAClone = isAClone;
	}

	public String getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(String deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

	public Set<PortToOpen> getPortsToOpen() {

		if (portsToOpen == null) {
			return new HashSet<>();
		}

		return this.portsToOpen;
	}



}
