package uk.ac.ebi.phenotype.pojo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "population")
public class Population {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	protected Integer id;

	@OneToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "parameter_id")
	private Parameter parameter;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumns({ @JoinColumn(name = "acc"), @JoinColumn(name = "db_id"), })
	private GenomicFeature gene;

	@ManyToMany
	@JoinTable(name = "observation_population", joinColumns = { @JoinColumn(name = "population_id") }, inverseJoinColumns = { @JoinColumn(name = "observation_id") })
	private Set<Observation> observations = new HashSet<Observation>();

	@Enumerated(EnumType.STRING)
	@Column(name = "zygosity")
	private ZygosityType zygosity;

	@Enumerated(EnumType.STRING)
	@Column(name = "sex")
	private SexType sex;

	@Column(name = "control_batches")
	private Integer controlBatches;

	@Column(name = "experimental_batches")
	private Integer experimentalBatches;

	@Column(name = "concurrent_controls")
	private Boolean concurrentControls;

	@OneToOne(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;

	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	
	public ZygosityType getZygosity() {
		return zygosity;
	}
	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
	}
	
	public SexType getSex() {
		return sex;
	}
	public void setSex(SexType sex) {
		this.sex = sex;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public GenomicFeature getGene() {
		return gene;
	}

	public void setGene(GenomicFeature gene) {
		this.gene = gene;
	}

	public Set<Observation> getObservations() {
		return observations;
	}

	public void setObservations(Set<Observation> observations) {
		this.observations = observations;
	}

	public Integer getControlBatches() {
		return controlBatches;
	}

	public void setControlBatches(Integer controlBatches) {
		this.controlBatches = controlBatches;
	}

	public Integer getExperimentalBatches() {
		return experimentalBatches;
	}

	public void setExperimentalBatches(Integer experimentalBatches) {
		this.experimentalBatches = experimentalBatches;
	}

	public Boolean getConcurrentControls() {
		return concurrentControls;
	}

	public void setConcurrentControls(Boolean concurrentControls) {
		this.concurrentControls = concurrentControls;
	}

	@Override
	public String toString() {
		return "Population [id=" + id + ", parameter=" + parameter + ", gene="
				+ gene + ", # observations=" + observations.size() + ", zygosity="
				+ zygosity + ", sex=" + sex + ", controlBatches="
				+ controlBatches + ", experimentalBatches="
				+ experimentalBatches + ", concurrentControls="
				+ concurrentControls + ", organisation=" + organisation.getName() + "]";
	}

	
	

}
