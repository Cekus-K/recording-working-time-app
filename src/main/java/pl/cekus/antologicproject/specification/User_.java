package pl.cekus.antologicproject.specification;

import pl.cekus.antologicproject.model.Project;
import pl.cekus.antologicproject.model.Role;
import pl.cekus.antologicproject.model.User;
import pl.cekus.antologicproject.model.WorkingTime;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.util.Set;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(User.class)
abstract class User_ {

    public static volatile SingularAttribute<User, String> login;
    public static volatile SingularAttribute<User, String> firstName;
    public static volatile SingularAttribute<User, String> lastName;
    public static volatile SingularAttribute<User, Role> role;
    public static volatile SingularAttribute<User, Double> costPerHour;
    public static volatile SingularAttribute<User, Set<Project>> projects;
    public static volatile SingularAttribute<User, Set<WorkingTime>> workingTimes;

    public static final String ID = "id";
    public static final String LOGIN = "login";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ROLE = "role";
    public static final String COST_PER_HOUR = "costPerHour";
    public static final String PROJECTS = "projects";
    public static final String WORKING_TIMES = "workingTimes";

}
