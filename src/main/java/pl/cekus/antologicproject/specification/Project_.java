package pl.cekus.antologicproject.specification;

import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.User;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;
import java.util.Set;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Project.class)
abstract class Project_ {

    public static volatile SingularAttribute<Project, String> projectName;
    public static volatile SingularAttribute<Project, LocalDate> startDate;
    public static volatile SingularAttribute<Project, LocalDate> endDate;
    public static volatile SingularAttribute<Project, Set<User>> users;
    public static volatile SingularAttribute<Project, Double> budget;

    public static final String ID = "id";
    public static final String PROJECT_NAME = "projectName";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String USERS = "users";
    public static final String BUDGET = "budget";
}
